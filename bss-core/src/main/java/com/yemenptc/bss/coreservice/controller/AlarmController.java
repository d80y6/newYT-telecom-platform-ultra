package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Alarm;
import com.yemenptc.bss.coreservice.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/alarmManagement/v5")
@RequiredArgsConstructor
@Tag(name = "Alarm Management", description = "TMF654 Alarm Management API")
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("/alarm")
    @Operation(summary = "Create alarm", description = "Creates a new alarm")
    public ResponseEntity<Alarm> createAlarm(@RequestBody Alarm request) {
        Alarm alarm = alarmService.createAlarm(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alarm);
    }

    @GetMapping("/alarm")
    @Operation(summary = "List alarms", description = "Retrieves all alarms with pagination")
    public ResponseEntity<Page<Alarm>> listAlarms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Alarm> alarms = alarmService.listAlarms(PageRequest.of(page, size));
        return ResponseEntity.ok(alarms);
    }

    @GetMapping("/alarm/{id}")
    @Operation(summary = "Get alarm", description = "Retrieves an alarm by ID")
    public ResponseEntity<Alarm> getAlarm(@PathVariable UUID id) {
        Alarm alarm = alarmService.getAlarm(id);
        return ResponseEntity.ok(alarm);
    }

    @PutMapping("/alarm/{id}")
    @Operation(summary = "Update alarm", description = "Updates an existing alarm")
    public ResponseEntity<Alarm> updateAlarm(
            @PathVariable UUID id, 
            @RequestBody Alarm request) {
        Alarm alarm = alarmService.updateAlarm(id, request);
        return ResponseEntity.ok(alarm);
    }

    @DeleteMapping("/alarm/{id}")
    @Operation(summary = "Delete alarm", description = "Deletes an alarm")
    public ResponseEntity<Void> deleteAlarm(@PathVariable UUID id) {
        alarmService.deleteAlarm(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/alarm/{id}/acknowledge")
    @Operation(summary = "Acknowledge alarm", description = "Acknowledges an alarm")
    public ResponseEntity<Alarm> acknowledgeAlarm(@PathVariable UUID id) {
        Alarm alarm = alarmService.acknowledgeAlarm(id);
        return ResponseEntity.ok(alarm);
    }

    @PatchMapping("/alarm/{id}/clear")
    @Operation(summary = "Clear alarm", description = "Clears an alarm")
    public ResponseEntity<Alarm> clearAlarm(@PathVariable UUID id) {
        Alarm alarm = alarmService.clearAlarm(id);
        return ResponseEntity.ok(alarm);
    }

    @PatchMapping("/alarm/{id}/close")
    @Operation(summary = "Close alarm", description = "Closes an alarm")
    public ResponseEntity<Alarm> closeAlarm(@PathVariable UUID id) {
        Alarm alarm = alarmService.closeAlarm(id);
        return ResponseEntity.ok(alarm);
    }

    @GetMapping("/alarm/status/{status}")
    @Operation(summary = "Get alarms by status", description = "Retrieves alarms by status")
    public ResponseEntity<List<Alarm>> getAlarmsByStatus(@PathVariable Alarm.AlarmStatus status) {
        List<Alarm> alarms = alarmService.getAlarmsByStatus(status);
        return ResponseEntity.ok(alarms);
    }

    @GetMapping("/alarm/severity/{severity}")
    @Operation(summary = "Get alarms by severity", description = "Retrieves alarms by severity")
    public ResponseEntity<List<Alarm>> getAlarmsBySeverity(@PathVariable Alarm.AlarmSeverity severity) {
        List<Alarm> alarms = alarmService.getAlarmsBySeverity(severity);
        return ResponseEntity.ok(alarms);
    }

    @GetMapping("/alarm/resource/{resourceId}")
    @Operation(summary = "Get alarms for resource", description = "Retrieves alarms for a specific resource")
    public ResponseEntity<List<Alarm>> getAlarmsForResource(@PathVariable UUID resourceId) {
        List<Alarm> alarms = alarmService.getAlarmsForResource(resourceId);
        return ResponseEntity.ok(alarms);
    }
}
