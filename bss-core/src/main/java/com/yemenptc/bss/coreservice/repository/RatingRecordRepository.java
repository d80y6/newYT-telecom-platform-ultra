package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.RatingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRecordRepository extends JpaRepository<RatingRecord, UUID> {
    List<RatingRecord> findBySubscriptionId(UUID subscriptionId);
    List<RatingRecord> findByServiceType(String serviceType);
    List<RatingRecord> findByRatingStatus(RatingRecord.RatingStatus status);
    List<RatingRecord> findByEventId(String eventId);
}
