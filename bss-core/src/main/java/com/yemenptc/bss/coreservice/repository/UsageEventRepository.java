package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.UsageEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface UsageEventRepository extends JpaRepository<UsageEvent, UUID> {
    List<UsageEvent> findBySubscriptionId(UUID subscriptionId);
    List<UsageEvent> findByServiceType(String serviceType);
    List<UsageEvent> findBySourceSystem(String sourceSystem);
    List<UsageEvent> findByEventId(String eventId);
}
