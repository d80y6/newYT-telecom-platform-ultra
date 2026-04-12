package balance

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
)

type Service struct {
	redis *redis.Client
}

type Balance struct {
	AccountID     string    `json:"accountId"`
	MainBalance   float64   `json:"mainBalance"`
	BonusBalance  float64   `json:"bonusBalance"`
	Currency      string    `json:"currency"`
	LastUpdated   time.Time `json:"lastUpdated"`
	ReservedTotal float64   `json:"reservedTotal"`
}

func NewService(redis *redis.Client) *Service {
	return &Service{redis: redis}
}

func (s *Service) GetBalance(ctx context.Context, accountID string) (*Balance, error) {
	key := fmt.Sprintf("balance:%s", accountID)
	data, err := s.redis.Get(ctx, key).Bytes()
	if err == redis.Nil {
		return &Balance{
			AccountID:   accountID,
			MainBalance: 0,
			Currency:    "YER",
			LastUpdated: time.Now(),
		}, nil
	}
	if err != nil {
		return nil, fmt.Errorf("failed to get balance: %w", err)
	}

	var bal Balance
	if err := json.Unmarshal(data, &bal); err != nil {
		return nil, fmt.Errorf("failed to unmarshal balance: %w", err)
	}
	return &bal, nil
}

func (s *Service) Reserve(ctx context.Context, accountID string, amount float64, currency string) (string, error) {
	key := fmt.Sprintf("balance:%s", accountID)
	reservationID := fmt.Sprintf("res-%s-%d", accountID, time.Now().UnixNano())

	bal, err := s.GetBalance(ctx, accountID)
	if err != nil {
		return "", err
	}

	available := bal.MainBalance - bal.ReservedTotal
	if available < amount {
		return "", fmt.Errorf("insufficient balance: available=%.2f, requested=%.2f", available, amount)
	}

	bal.ReservedTotal += amount
	bal.LastUpdated = time.Now()

	data, err := json.Marshal(bal)
	if err != nil {
		return "", fmt.Errorf("failed to marshal balance: %w", err)
	}

	if err := s.redis.Set(ctx, key, data, 24*time.Hour).Err(); err != nil {
		return "", fmt.Errorf("failed to update balance: %w", err)
	}

	resKey := fmt.Sprintf("reservation:%s", reservationID)
	resData := map[string]interface{}{
		"accountId": accountID,
		"amount":    amount,
		"currency":  currency,
		"status":    "reserved",
		"createdAt": time.Now(),
	}
	resJSON, _ := json.Marshal(resData)
	s.redis.Set(ctx, resKey, resJSON, 5*time.Minute)

	return reservationID, nil
}

func (s *Service) Confirm(ctx context.Context, accountID, reservationID string, amount float64) error {
	key := fmt.Sprintf("balance:%s", accountID)

	bal, err := s.GetBalance(ctx, accountID)
	if err != nil {
		return err
	}

	bal.MainBalance -= amount
	bal.ReservedTotal -= amount
	if bal.ReservedTotal < 0 {
		bal.ReservedTotal = 0
	}
	bal.LastUpdated = time.Now()

	data, err := json.Marshal(bal)
	if err != nil {
		return fmt.Errorf("failed to marshal balance: %w", err)
	}

	if err := s.redis.Set(ctx, key, data, 24*time.Hour).Err(); err != nil {
		return fmt.Errorf("failed to update balance: %w", err)
	}

	resKey := fmt.Sprintf("reservation:%s", reservationID)
	s.redis.Del(ctx, resKey)

	return nil
}

func (s *Service) Deduct(ctx context.Context, accountID string, amount float64) error {
	key := fmt.Sprintf("balance:%s", accountID)

	bal, err := s.GetBalance(ctx, accountID)
	if err != nil {
		return err
	}

	if bal.MainBalance < amount {
		return fmt.Errorf("insufficient balance: available=%.2f, requested=%.2f", bal.MainBalance, amount)
	}

	bal.MainBalance -= amount
	bal.LastUpdated = time.Now()

	data, err := json.Marshal(bal)
	if err != nil {
		return fmt.Errorf("failed to marshal balance: %w", err)
	}

	return s.redis.Set(ctx, key, data, 24*time.Hour).Err()
}

func (s *Service) TopUp(ctx context.Context, accountID string, amount float64, isBonus bool) error {
	key := fmt.Sprintf("balance:%s", accountID)

	bal, err := s.GetBalance(ctx, accountID)
	if err != nil {
		return err
	}

	if isBonus {
		bal.BonusBalance += amount
	} else {
		bal.MainBalance += amount
	}
	bal.LastUpdated = time.Now()

	data, err := json.Marshal(bal)
	if err != nil {
		return fmt.Errorf("failed to marshal balance: %w", err)
	}

	return s.redis.Set(ctx, key, data, 24*time.Hour).Err()
}
