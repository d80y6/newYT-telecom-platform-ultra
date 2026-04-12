package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Notification;
import com.yemenptc.bss.coreservice.service.NotificationService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/notificationListener/v5")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/notification")
    public ResponseEntity<TmfResponse<Notification>> create(@RequestBody Notification request) {
        Notification notification = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(notification, "Notification"));
    }

    @GetMapping("/notification/{id}")
    public ResponseEntity<TmfResponse<Notification>> getById(@PathVariable UUID id) {
        Notification notification = notificationService.getNotification(id);
        return ResponseEntity.ok(TmfResponse.success(notification, "Notification"));
    }

    @GetMapping("/notification")
    public ResponseEntity<TmfResponse<Notification>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Notification> result = notificationService.listNotifications(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "Notification"));
    }

    @PostMapping("/notification/{id}/deliver")
    public ResponseEntity<TmfResponse<Notification>> deliver(@PathVariable UUID id) {
        Notification notification = notificationService.deliverNotification(id);
        return ResponseEntity.ok(TmfResponse.success(notification, "Notification"));
    }

    @PostMapping("/notification/{id}/fail")
    public ResponseEntity<TmfResponse<Notification>> fail(@PathVariable UUID id) {
        Notification notification = notificationService.failNotification(id, "Delivery failed");
        return ResponseEntity.ok(TmfResponse.success(notification, "Notification"));
    }

    @PostMapping("/notification/{id}/retry")
    public ResponseEntity<TmfResponse<Notification>> retry(@PathVariable UUID id) {
        Notification notification = notificationService.retryNotification(id);
        return ResponseEntity.ok(TmfResponse.success(notification, "Notification"));
    }

    @GetMapping("/notification/event/{eventType}")
    public ResponseEntity<TmfResponse<Notification>> getByEvent(@PathVariable String eventType) {
        List<Notification> notifications = notificationService.getNotificationsByEvent(eventType);
        return ResponseEntity.ok(TmfResponse.list(
                notifications, notifications.size(), 0, notifications.size(), "Notification"));
    }

    @PostMapping("/notify")
    public ResponseEntity<Void> receiveNotification(@RequestBody Object event) {
        notificationService.publishEvent("notification.received", event);
        return ResponseEntity.ok().build();
    }
}
