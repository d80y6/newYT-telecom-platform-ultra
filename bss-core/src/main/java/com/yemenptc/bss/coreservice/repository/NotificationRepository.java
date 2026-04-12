package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByEventType(String eventType);
    List<Notification> findByDeliveryStatus(Notification.DeliveryStatus status);
    List<Notification> findBySubscriptionId(UUID subscriptionId);
}
