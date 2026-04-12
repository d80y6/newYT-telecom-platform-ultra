package rating

import "time"

type Service struct {
	plans map[string]*RatingPlan
}

type RatingPlan struct {
	ID          string
	Name        string
	ServiceType string
	Timebands   []Timeband
	Zones       []Zone
}

type Timeband struct {
	Name      string
	DayOfWeek []int
	StartHour int
	EndHour   int
	Rate      float64
}

type Zone struct {
	Prefix    string
	Rate      float64
	Increment int
}

type RatedCDR struct {
	CDRID        string
	AccountID    string
	ServiceType  string
	Amount       float64
	Currency     string
	RatingPlanID string
	RatedAt      time.Time
}

func NewService() *Service {
	s := &Service{
		plans: make(map[string]*RatingPlan),
	}
	s.loadDefaultPlans()
	return s
}

func (s *Service) Rate(cdrID, accountID, serviceType string, duration int, volumeBytes int64, timestamp time.Time) (*RatedCDR, error) {
	plan := s.getPlanForService(serviceType)
	if plan == nil {
		return &RatedCDR{
			CDRID:       cdrID,
			AccountID:   accountID,
			ServiceType: serviceType,
			Amount:      0,
			Currency:    "YER",
			RatedAt:     time.Now(),
		}, nil
	}

	var amount float64
	switch serviceType {
	case "VOICE":
		amount = s.rateVoice(plan, duration, timestamp)
	case "DATA":
		amount = s.rateData(plan, volumeBytes)
	case "SMS":
		amount = s.rateSMS(plan)
	default:
		amount = 0
	}

	return &RatedCDR{
		CDRID:        cdrID,
		AccountID:    accountID,
		ServiceType:  serviceType,
		Amount:       amount,
		Currency:     "YER",
		RatingPlanID: plan.ID,
		RatedAt:      time.Now(),
	}, nil
}

func (s *Service) getPlanForService(serviceType string) *RatingPlan {
	for _, plan := range s.plans {
		if plan.ServiceType == serviceType {
			return plan
		}
	}
	return nil
}

func (s *Service) rateVoice(plan *RatingPlan, duration int, timestamp time.Time) float64 {
	hour := timestamp.Hour()
	day := int(timestamp.Weekday())

	var rate float64 = 1.0
	for _, tb := range plan.Timebands {
		dayMatch := false
		for _, d := range tb.DayOfWeek {
			if d == day {
				dayMatch = true
				break
			}
		}
		if dayMatch && hour >= tb.StartHour && hour < tb.EndHour {
			rate = tb.Rate
			break
		}
	}

	minutes := float64(duration) / 60.0
	return minutes * rate
}

func (s *Service) rateData(plan *RatingPlan, volumeBytes int64) float64 {
	mb := float64(volumeBytes) / (1024 * 1024)
	return mb * 0.5
}

func (s *Service) rateSMS(plan *RatingPlan) float64 {
	return 1.0
}

func (s *Service) loadDefaultPlans() {
	s.plans["voice-standard"] = &RatingPlan{
		ID:          "voice-standard",
		Name:        "Standard Voice",
		ServiceType: "VOICE",
		Timebands: []Timeband{
			{Name: "Peak", DayOfWeek: []int{1, 2, 3, 4, 5}, StartHour: 8, EndHour: 20, Rate: 10.0},
			{Name: "Off-peak", DayOfWeek: []int{1, 2, 3, 4, 5}, StartHour: 20, EndHour: 8, Rate: 5.0},
			{Name: "Weekend", DayOfWeek: []int{0, 6}, StartHour: 0, EndHour: 24, Rate: 5.0},
		},
	}

	s.plans["data-standard"] = &RatingPlan{
		ID:          "data-standard",
		Name:        "Standard Data",
		ServiceType: "DATA",
	}

	s.plans["sms-standard"] = &RatingPlan{
		ID:          "sms-standard",
		Name:        "Standard SMS",
		ServiceType: "SMS",
	}
}
