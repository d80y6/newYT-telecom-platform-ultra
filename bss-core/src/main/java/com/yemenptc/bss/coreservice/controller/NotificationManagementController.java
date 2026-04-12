package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tmf-api/notificationManagement/v5")
@RequiredArgsConstructor
public class NotificationManagementController {

    @GetMapping("/notification")
    public ResponseEntity<TmfResponse> listNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String filter) {
        return ResponseEntity.ok(TmfResponse.list(new ArrayList<>(), 0, page * size, size, "Notification"));
    }

    @PostMapping("/notification")
    public ResponseEntity<TmfResponse> sendNotification(@RequestBody Map<String, Object> request) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("id", UUID.randomUUID().toString());
        notification.put("status", "SENT");
        notification.put("sentAt", new Date().toString());
        notification.put("channel", request.get("channel"));
        return ResponseEntity.status(HttpStatus.CREATED).body(TmfResponse.success(notification, "Notification"));
    }

    @GetMapping("/notification/{id}")
    public ResponseEntity<TmfResponse> getNotificationStatus(@PathVariable String id) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("id", id);
        notification.put("status", "DELIVERED");
        return ResponseEntity.ok(TmfResponse.success(notification, "Notification"));
    }

    @GetMapping("/notificationTemplate")
    public ResponseEntity<TmfResponse> listNotificationTemplates() {
        return ResponseEntity.ok(TmfResponse.success(new ArrayList<>(), "NotificationTemplate"));
    }

    @PostMapping("/notificationTemplate")
    public ResponseEntity<TmfResponse> createNotificationTemplate(@RequestBody Map<String, Object> request) {
        Map<String, Object> template = new HashMap<>();
        template.put("id", UUID.randomUUID().toString());
        template.put("name", request.get("name"));
        return ResponseEntity.status(HttpStatus.CREATED).body(TmfResponse.success(template, "NotificationTemplate"));
    }

    @GetMapping("/channel")
    public ResponseEntity<TmfResponse> listChannels() {
        List<String> channels = Arrays.asList("SMS", "EMAIL", "PUSH", "IVR", "WHATSAPP", "TELEGRAM");
        return ResponseEntity.ok(TmfResponse.success(channels, "Channel"));
    }
}