package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.SlaTemplate;
import com.yemenptc.bss.coreservice.repository.SlaTemplateRepository;
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
public class SlaService {

    private final SlaTemplateRepository slaTemplateRepository;

    @Transactional
    public SlaTemplate createTemplate(SlaTemplate request) {
        log.info("Creating SLA template: {}", request.getTemplateName());
        
        SlaTemplate template = SlaTemplate.builder()
            .templateName(request.getTemplateName())
            .templateType(request.getTemplateType())
            .description(request.getDescription())
            .serviceLevel(request.getServiceLevel())
            .availabilityTarget(request.getAvailabilityTarget())
            .responseTimeSla(request.getResponseTimeSla())
            .resolutionTimeSla(request.getResolutionTimeSla())
            .status(SlaTemplate.SlaStatus.DRAFT)
            .validFrom(request.getValidFrom())
            .validTo(request.getValidTo())
            .isActive(true)
            .priority(request.getPriority())
            .category(request.getCategory())
            .build();
        
        return slaTemplateRepository.save(template);
    }

    @Transactional(readOnly = true)
    public SlaTemplate getTemplate(UUID id) {
        return slaTemplateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("SLA template not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<SlaTemplate> listTemplates(Pageable pageable) {
        return slaTemplateRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<SlaTemplate> listTemplatesByStatus(SlaTemplate.SlaStatus status, Pageable pageable) {
        return slaTemplateRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public List<SlaTemplate> getActiveTemplates() {
        return slaTemplateRepository.findByIsActiveTrue();
    }

    @Transactional
    public SlaTemplate activateTemplate(UUID id) {
        SlaTemplate template = getTemplate(id);
        template.setStatus(SlaTemplate.SlaStatus.ACTIVE);
        template.setUpdatedAt(Instant.now());
        log.info("SLA template activated: {}", id);
        return slaTemplateRepository.save(template);
    }

    @Transactional
    public SlaTemplate deprecateTemplate(UUID id) {
        SlaTemplate template = getTemplate(id);
        template.setStatus(SlaTemplate.SlaStatus.DEPRECATED);
        template.setIsActive(false);
        template.setUpdatedAt(Instant.now());
        log.info("SLA template deprecated: {}", id);
        return slaTemplateRepository.save(template);
    }

    @Transactional
    public void deleteTemplate(UUID id) {
        SlaTemplate template = getTemplate(id);
        template.setStatus(SlaTemplate.SlaStatus.ARCHIVED);
        template.setIsActive(false);
        template.setUpdatedAt(Instant.now());
        slaTemplateRepository.save(template);
        log.info("SLA template archived: {}", id);
    }
}