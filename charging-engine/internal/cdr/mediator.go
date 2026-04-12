package cdr

import (
	"context"
	"encoding/json"
	"fmt"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
	"github.com/rs/zerolog/log"
	"github.com/yemenptc/charging-engine/internal/balance"
	"github.com/yemenptc/charging-engine/internal/rating"
)

type Mediator struct {
	balanceSvc *balance.Service
	ratingSvc  *rating.Service
}

type CDREvent struct {
	EventID     string `json:"eventId"`
	EventType   string `json:"eventType"`
	CDRID       string `json:"cdrId"`
	AccountID   string `json:"accountId"`
	ServiceType string `json:"serviceType"`
	StartTime   int64  `json:"startTime"`
	DurationSec int    `json:"durationSeconds"`
	VolumeBytes int64  `json:"volumeBytes"`
	Source      string `json:"source"`
}

func NewMediator(balanceSvc *balance.Service, ratingSvc *rating.Service) *Mediator {
	return &Mediator{
		balanceSvc: balanceSvc,
		ratingSvc:  ratingSvc,
	}
}

func (m *Mediator) StartConsumption(ctx context.Context, brokers []string, groupID string) {
	consumer, err := kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers":  brokers[0],
		"group.id":           groupID,
		"auto.offset.reset":  "earliest",
		"enable.auto.commit": true,
	})
	if err != nil {
		log.Fatal().Err(err).Msg("Failed to create Kafka consumer")
	}
	defer consumer.Close()

	topics := []string{"usage.events", "catalog.events"}
	if err := consumer.SubscribeTopics(topics, nil); err != nil {
		log.Fatal().Err(err).Msg("Failed to subscribe to topics")
	}

	log.Info().Strs("topics", topics).Msg("CDR consumer started")

	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)

	run := true
	for run {
		select {
		case sig := <-sigChan:
			log.Info().Str("signal", sig.String()).Msg("Received signal, shutting down")
			run = false
		default:
			ev := consumer.Poll(100)
			if ev == nil {
				continue
			}

			switch e := ev.(type) {
			case *kafka.Message:
				m.processMessage(ctx, e)
			case kafka.Error:
				log.Error().Err(e).Msg("Kafka error")
				if e.IsFatal() {
					run = false
				}
			}
		}
	}

	log.Info().Msg("CDR consumer stopped")
}

func (m *Mediator) processMessage(ctx context.Context, msg *kafka.Message) {
	topic := *msg.TopicPartition.Topic

	switch topic {
	case "usage.events":
		m.processUsageEvent(ctx, msg.Value)
	case "catalog.events":
		m.processCatalogEvent(ctx, msg.Value)
	default:
		log.Warn().Str("topic", topic).Msg("Unknown topic")
	}
}

func (m *Mediator) processUsageEvent(ctx context.Context, data []byte) {
	var event CDREvent
	if err := json.Unmarshal(data, &event); err != nil {
		log.Error().Err(err).Msg("Failed to unmarshal CDR event")
		return
	}

	log.Info().
		Str("cdrId", event.CDRID).
		Str("accountId", event.AccountID).
		Str("serviceType", event.ServiceType).
		Msg("Processing CDR")

	timestamp := time.Unix(event.StartTime/1000, 0)
	rated, err := m.ratingSvc.Rate(
		event.CDRID,
		event.AccountID,
		event.ServiceType,
		event.DurationSec,
		event.VolumeBytes,
		timestamp,
	)
	if err != nil {
		log.Error().Err(err).Str("cdrId", event.CDRID).Msg("Failed to rate CDR")
		return
	}

	if rated.Amount > 0 {
		if err := m.balanceSvc.Deduct(ctx, event.AccountID, rated.Amount); err != nil {
			log.Error().Err(err).
				Str("cdrId", event.CDRID).
				Float64("amount", rated.Amount).
				Msg("Failed to deduct balance")
			return
		}

		log.Info().
			Str("cdrId", event.CDRID).
			Str("accountId", event.AccountID).
			Float64("amount", rated.Amount).
			Str("currency", rated.Currency).
			Msg("CDR rated and balance deducted")
	}
}

func (m *Mediator) processCatalogEvent(ctx context.Context, data []byte) {
	var event map[string]interface{}
	if err := json.Unmarshal(data, &event); err != nil {
		log.Error().Err(err).Msg("Failed to unmarshal catalog event")
		return
	}

	eventType, _ := event["eventType"].(string)
	log.Info().Str("eventType", eventType).Msg("Received catalog event")

	if eventType == "com.yemenptc.catalog.offering.updated" {
		log.Info().Msg("Rating plans may need refresh based on catalog update")
	}
}

func (m *Mediator) publishRatedEvent(ctx context.Context, rated *rating.RatedCDR) error {
	_ = ctx
	_ = rated
	fmt.Println("Publishing rated event (placeholder)")
	return nil
}
