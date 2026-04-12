package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Alarm;
import com.yemenptc.bss.coreservice.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    @Transactional
    public Alarm createAlarm(Alarm request) {
        log.info("Creating alarm: {} - {}", request.getAlarmType(), request.getSeverity());

        Alarm alarm = Alarm.builder()
            .alarmId(request.getAlarmId() != null ? 
                request.getAlarmId() : "ALM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .severity(request.getSeverity() != null ? request.getSeverity() : Alarm.AlarmSeverity.WARNING)
            .status(Alarm.AlarmStatus.ACTIVE)
            .source(request.getSource())
            .resourceId(request.getResourceId())
            .alarmType(request.getAlarmType())
            .description(request.getDescription())
            .rootCause(request.getRootCause())
            .build();

        Alarm saved = alarmRepository.save(alarm);
        log.info("Alarm created: {}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Alarm getAlarm(UUID id) {
        return alarmRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alarm not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Alarm> listAlarms(Pageable pageable) {
        return alarmRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Alarm> getAlarmsByStatus(Alarm.AlarmStatus status) {
        return alarmRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Alarm> getAlarmsBySeverity(Alarm.AlarmSeverity severity) {
        return alarmRepository.findBySeverity(severity);
    }

    @Transactional(readOnly = true)
    public List<Alarm> getAlarmsForResource(UUID resourceId) {
        return alarmRepository.findByResourceId(resourceId);
    }

    @Transactional
    public Alarm acknowledgeAlarm(UUID id) {
        Alarm alarm = getAlarm(id);
        alarm.setStatus(Alarm.AlarmStatus.ACKNOWLEDGED);
        alarm.setAcknowledgedAt(Instant.now());
        log.info("Alarm acknowledged: {}", alarm.getAlarmId());
        return alarmRepository.save(alarm);
    }

    @Transactional
    public Alarm clearAlarm(UUID id) {
        Alarm alarm = getAlarm(id);
        alarm.setStatus(Alarm.AlarmStatus.CLEARED);
        alarm.setClearedAt(Instant.now());
        log.info("Alarm cleared: {}", alarm.getAlarmId());
        return alarmRepository.save(alarm);
    }

    @Transactional
    public Alarm closeAlarm(UUID id) {
        Alarm alarm = getAlarm(id);
        alarm.setStatus(Alarm.AlarmStatus.CLOSED);
        log.info("Alarm closed: {}", alarm.getAlarmId());
        return alarmRepository.save(alarm);
    }

    @Transactional
    public Alarm updateAlarm(UUID id, Alarm request) {
        Alarm alarm = getAlarm(id);
        if (request.getSeverity() != null) alarm.setSeverity(request.getSeverity());
        if (request.getDescription() != null) alarm.setDescription(request.getDescription());
        if (request.getRootCause() != null) alarm.setRootCause(request.getRootCause());
        return alarmRepository.save(alarm);
    }

    @Transactional
    public void deleteAlarm(UUID id) {
        Alarm alarm = getAlarm(id);
        alarmRepository.delete(alarm);
        log.info("Alarm deleted: {}", alarm.getAlarmId());
    }

    @Transactional(readOnly = true)
    public long countBySeverity(Alarm.AlarmSeverity severity) {
        return alarmRepository.countBySeverity(severity);
    }

    @Transactional(readOnly = true)
    public long countByStatus(Alarm.AlarmStatus status) {
        return alarmRepository.countByStatus(status);
    }
}
