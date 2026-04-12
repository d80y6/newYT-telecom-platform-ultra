package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Notification;
import com.yemenptc.bss.coreservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void createNotification_Success() {
        Notification request = new Notification();
        request.setEventType("ORDER_COMPLETED");
        request.setCallbackUrl("http://localhost:8080/notify");
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> {
            Notification n = i.getArgument(0);
            n.setId(UUID.randomUUID());
            n.setNotificationId("NOTIF-TEST123");
            return n;
        });

        Notification result = notificationService.createNotification(request);

        assertNotNull(result);
        assertEquals(Notification.DeliveryStatus.PENDING, result.getDeliveryStatus());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getNotification_Success() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(id);
        notification.setNotificationId("NOTIF-TEST123");
        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

        Notification result = notificationService.getNotification(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void listNotifications_Success() {
        Page<Notification> page = new PageImpl<>(List.of());
        when(notificationRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Notification> result = notificationService.listNotifications(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void deliverNotification_Success() {
        UUID id = UUID.randomUUID();
        Notification existing = new Notification();
        existing.setId(id);
        existing.setNotificationId("NOTIF-TEST123");
        existing.setDeliveryStatus(Notification.DeliveryStatus.PENDING);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        Notification result = notificationService.deliverNotification(id);

        assertNotNull(result);
        assertEquals(Notification.DeliveryStatus.DELIVERED, result.getDeliveryStatus());
        assertNotNull(result.getDeliveredAt());
    }

    @Test
    void failNotification_Success() {
        UUID id = UUID.randomUUID();
        Notification existing = new Notification();
        existing.setId(id);
        existing.setRetryCount(0);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        Notification result = notificationService.failNotification(id, "Test error");

        assertNotNull(result);
        assertEquals(Notification.DeliveryStatus.FAILED, result.getDeliveryStatus());
        assertEquals(1, result.getRetryCount());
    }

    @Test
    void getNotificationsByEvent_Success() {
        String eventType = "ORDER_COMPLETED";
        when(notificationRepository.findByEventType(eventType)).thenReturn(List.of());

        List<Notification> result = notificationService.getNotificationsByEvent(eventType);

        assertNotNull(result);
    }
}
