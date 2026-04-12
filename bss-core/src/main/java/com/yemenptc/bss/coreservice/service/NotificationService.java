package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Notification;
import com.yemenptc.bss.coreservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Notification createNotification(Notification request) {
        Notification notification = Notification.builder()
            .notificationId("NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .eventType(request.getEventType())
            .callbackUrl(request.getCallbackUrl())
            .subscriptionId(request.getSubscriptionId())
            .listenerType(request.getListenerType())
            .deliveryStatus(Notification.DeliveryStatus.PENDING)
            .retryCount(0)
            .build();
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Notification getNotification(UUID id) {
        return notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Notification> listNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByEvent(String eventType) {
        return notificationRepository.findByEventType(eventType);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByStatus(Notification.DeliveryStatus status) {
        return notificationRepository.findByDeliveryStatus(status);
    }

    @Transactional
    public Notification deliverNotification(UUID id) {
        Notification notification = getNotification(id);
        notification.setDeliveryStatus(Notification.DeliveryStatus.DELIVERED);
        notification.setDeliveredAt(Instant.now());
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification failNotification(UUID id, String errorMessage) {
        Notification notification = getNotification(id);
        notification.setDeliveryStatus(Notification.DeliveryStatus.FAILED);
        notification.setRetryCount(notification.getRetryCount() + 1);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification retryNotification(UUID id) {
        Notification notification = getNotification(id);
        notification.setDeliveryStatus(Notification.DeliveryStatus.RETRYING);
        notification.setRetryCount(notification.getRetryCount() + 1);
        return notificationRepository.save(notification);
    }

    public void publishEvent(String topic, Object event) {
        kafkaTemplate.send(topic, event);
    }
}
