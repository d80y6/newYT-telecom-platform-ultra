package main

import (
	"context"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/redis/go-redis/v9"
	"github.com/rs/zerolog"
	"github.com/rs/zerolog/log"
	"github.com/yemenptc/charging-engine/internal/balance"
	"github.com/yemenptc/charging-engine/internal/cdr"
	"github.com/yemenptc/charging-engine/internal/rating"
)

func main() {
	zerolog.TimeFieldFormat = zerolog.TimeFormatUnix
	log.Logger = log.Output(zerolog.ConsoleWriter{Out: os.Stderr})

	cfg := loadConfig()

	redisClient := redis.NewClient(&redis.Options{
		Addr:     cfg.RedisAddr,
		Password: cfg.RedisPassword,
		DB:       cfg.RedisDB,
	})
	defer redisClient.Close()

	ctx := context.Background()
	if err := redisClient.Ping(ctx).Err(); err != nil {
		log.Fatal().Err(err).Msg("Failed to connect to Redis")
	}
	log.Info().Msg("Connected to Redis")

	balanceSvc := balance.NewService(redisClient)
	ratingSvc := rating.NewService()
	mediator := cdr.NewMediator(balanceSvc, ratingSvc)

	go mediator.StartConsumption(ctx, cfg.KafkaBrokers, cfg.KafkaGroupID)

	router := setupRouter(balanceSvc)
	srv := &http.Server{
		Addr:    ":" + cfg.Port,
		Handler: router,
	}

	go func() {
		log.Info().Str("port", cfg.Port).Msg("Starting HTTP server")
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatal().Err(err).Msg("HTTP server failed")
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	log.Info().Msg("Shutting down server...")
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err := srv.Shutdown(ctx); err != nil {
		log.Fatal().Err(err).Msg("Server forced to shutdown")
	}
	log.Info().Msg("Server exited")
}

func setupRouter(balanceSvc *balance.Service) *gin.Engine {
	gin.SetMode(gin.ReleaseMode)
	router := gin.Default()

	router.GET("/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{"status": "healthy"})
	})

	v1 := router.Group("/api/v1")
	{
		v1.GET("/balance/:accountId", func(c *gin.Context) {
			accountID := c.Param("accountId")
			bal, err := balanceSvc.GetBalance(c.Request.Context(), accountID)
			if err != nil {
				c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
				return
			}
			c.JSON(http.StatusOK, bal)
		})

		v1.POST("/balance/:accountId/reserve", func(c *gin.Context) {
			accountID := c.Param("accountId")
			var req struct {
				Amount   float64 `json:"amount" binding:"required"`
				Currency string  `json:"currency" binding:"required"`
			}
			if err := c.ShouldBindJSON(&req); err != nil {
				c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
				return
			}
			reservationID, err := balanceSvc.Reserve(c.Request.Context(), accountID, req.Amount, req.Currency)
			if err != nil {
				c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
				return
			}
			c.JSON(http.StatusOK, gin.H{"reservationId": reservationID})
		})

		v1.POST("/balance/:accountId/confirm", func(c *gin.Context) {
			accountID := c.Param("accountId")
			var req struct {
				ReservationID string  `json:"reservationId" binding:"required"`
				Amount        float64 `json:"amount" binding:"required"`
			}
			if err := c.ShouldBindJSON(&req); err != nil {
				c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
				return
			}
			err := balanceSvc.Confirm(c.Request.Context(), accountID, req.ReservationID, req.Amount)
			if err != nil {
				c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
				return
			}
			c.JSON(http.StatusOK, gin.H{"status": "confirmed"})
		})
	}

	return router
}

type config struct {
	Port          string
	RedisAddr     string
	RedisPassword string
	RedisDB       int
	KafkaBrokers  []string
	KafkaGroupID  string
}

func loadConfig() config {
	return config{
		Port:          getEnv("PORT", "8081"),
		RedisAddr:     getEnv("REDIS_ADDR", "localhost:6379"),
		RedisPassword: getEnv("REDIS_PASSWORD", ""),
		RedisDB:       0,
		KafkaBrokers:  []string{getEnv("KAFKA_BROKERS", "localhost:9092")},
		KafkaGroupID:  getEnv("KAFKA_GROUP_ID", "charging-engine-group"),
	}
}

func getEnv(key, fallback string) string {
	if value, ok := os.LookupEnv(key); ok {
		return value
	}
	return fallback
}
