package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Fault;
import com.yemenptc.bss.coreservice.repository.FaultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FaultManagementService {

    private final FaultRepository faultRepository;

    @Transactional
    public Fault createFault(Fault request) {
        log.info("Creating fault: type={}, severity={}", request.getAlarmType(), request.getSeverity());

        Fault fault = Fault.builder()
            .faultId("FLT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .severity(request.getSeverity())
            .status(Fault.FaultStatus.ACTIVE)
            .alarmType(request.getAlarmType())
            .alarmName(request.getAlarmName())
            .description(request.getDescription())
            .resourceId(request.getResourceId())
            .resourceType(request.getResourceType())
            .resourceName(request.getResourceName())
            .neId(request.getNeId())
            .locationId(request.getLocationId())
            .locationName(request.getLocationName())
            .serviceId(request.getServiceId())
            .customerId(request.getCustomerId())
            .correlationId(request.getCorrelationId())
            .parentAlarmId(request.getParentAlarmId())
            .detectedAt(LocalDateTime.now())
            .build();

        Fault saved = faultRepository.save(fault);
        log.info("Fault created: {}", saved.getFaultId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Fault getFault(UUID id) {
        return faultRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Fault not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Fault> listFaults(Pageable pageable) {
        return faultRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Fault> findByStatus(Fault.FaultStatus status, Pageable pageable) {
        return faultRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Fault> findBySeverity(Fault.AlarmSeverity severity, Pageable pageable) {
        return faultRepository.findBySeverity(severity, pageable);
    }

    @Transactional
    public Fault acknowledgeFault(UUID id, String acknowledgedBy) {
        log.info("Acknowledging fault {} by {}", id, acknowledgedBy);
        
        Fault fault = getFault(id);
        fault.setStatus(Fault.FaultStatus.ACKNOWLEDGED);
        fault.setAcknowledgedAt(LocalDateTime.now());
        fault.setAcknowledgedBy(acknowledgedBy);
        
        return faultRepository.save(fault);
    }

    @Transactional
    public Fault clearFault(UUID id, String clearedBy, String resolutionNotes) {
        log.info("Clearing fault {} by {}", id, clearedBy);
        
        Fault fault = getFault(id);
        fault.setStatus(Fault.FaultStatus.CLEARED);
        fault.setClearedAt(LocalDateTime.now());
        fault.setClearedBy(clearedBy);
        fault.setResolutionNotes(resolutionNotes);
        
        return faultRepository.save(fault);
    }

    @Transactional
    public Fault closeFault(UUID id) {
        log.info("Closing fault {}", id);
        
        Fault fault = getFault(id);
        fault.setStatus(Fault.FaultStatus.CLOSED);
        
        return faultRepository.save(fault);
    }

    @Transactional
    public Fault addResolutionNotes(UUID id, String notes) {
        Fault fault = getFault(id);
        fault.setResolutionNotes(notes);
        return faultRepository.save(fault);
    }
}