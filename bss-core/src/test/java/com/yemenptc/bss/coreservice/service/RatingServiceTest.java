package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.RatingRecord;
import com.yemenptc.bss.coreservice.entity.UsageEvent;
import com.yemenptc.bss.coreservice.repository.RatingRecordRepository;
import com.yemenptc.bss.coreservice.repository.UsageEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private UsageEventRepository usageEventRepository;
    @Mock
    private RatingRecordRepository ratingRecordRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void createUsageEvent_Success() {
        UsageEvent request = new UsageEvent();
        request.setEventId("TEST-001");
        request.setServiceType("MOBILE");
        request.setEventType("VOICE_CALL");
        request.setUsageValue(BigDecimal.valueOf(100));
        when(usageEventRepository.save(any(UsageEvent.class))).thenAnswer(i -> {
            UsageEvent e = i.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        UsageEvent result = ratingService.createUsageEvent(request);

        assertNotNull(result);
        verify(usageEventRepository).save(any(UsageEvent.class));
    }

    @Test
    void listUsageEvents_Success() {
        Page<UsageEvent> page = new PageImpl<>(List.of());
        when(usageEventRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<UsageEvent> result = ratingService.listUsageEvents(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void rateUsage_Success() {
        UsageEvent event = new UsageEvent();
        event.setEventId("TEST-001");
        event.setServiceType("MOBILE");
        event.setEventType("VOICE_CALL");
        when(ratingRecordRepository.save(any(RatingRecord.class))).thenAnswer(i -> {
            RatingRecord r = i.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        RatingRecord result = ratingService.rateUsage(event);

        assertNotNull(result);
        assertEquals(RatingRecord.RatingStatus.RATED, result.getRatingStatus());
        verify(ratingRecordRepository).save(any(RatingRecord.class));
    }

    @Test
    void listRatingRecords_Success() {
        Page<RatingRecord> page = new PageImpl<>(List.of());
        when(ratingRecordRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<RatingRecord> result = ratingService.listRatingRecords(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void getRatingRecord_Success() {
        UUID id = UUID.randomUUID();
        RatingRecord record = new RatingRecord();
        record.setId(id);
        record.setEventId("TEST-001");
        when(ratingRecordRepository.findById(id)).thenReturn(Optional.of(record));

        RatingRecord result = ratingService.getRatingRecord(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }
}
