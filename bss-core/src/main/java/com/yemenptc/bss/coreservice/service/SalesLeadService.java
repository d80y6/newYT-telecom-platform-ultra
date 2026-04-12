package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.SalesLead;
import com.yemenptc.bss.coreservice.repository.SalesLeadRepository;
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
public class SalesLeadService {

    private final SalesLeadRepository salesLeadRepository;

    @Transactional
    public SalesLead createLead(SalesLead request) {
        log.info("Creating sales lead: {}", request.getCustomerName());

        SalesLead lead = SalesLead.builder()
            .leadId(request.getLeadId() != null ? request.getLeadId() : "LEAD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .customerName(request.getCustomerName())
            .contactPhone(request.getContactPhone())
            .contactEmail(request.getContactEmail())
            .status(SalesLead.LeadStatus.NEW)
            .potentialValue(request.getPotentialValue())
            .currency(request.getCurrency() != null ? request.getCurrency() : "YER")
            .source(request.getSource())
            .assignedTo(request.getAssignedTo())
            .build();

        SalesLead saved = salesLeadRepository.save(lead);
        log.info("Sales lead created: {}", saved.getLeadId());
        return saved;
    }

    @Transactional(readOnly = true)
    public SalesLead getLead(UUID id) {
        return salesLeadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sales lead not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<SalesLead> listLeads(Pageable pageable) {
        return salesLeadRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<SalesLead> getLeadsByStatus(SalesLead.LeadStatus status) {
        return salesLeadRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<SalesLead> getLeadsByAssignedTo(UUID assignedTo) {
        return salesLeadRepository.findByAssignedTo(assignedTo);
    }

    @Transactional
    public SalesLead updateLeadStatus(UUID id, SalesLead.LeadStatus status) {
        SalesLead lead = getLead(id);
        lead.setStatus(status);
        if (status == SalesLead.LeadStatus.CONVERTED) {
            lead.setConvertedAt(Instant.now());
        }
        log.info("Sales lead status updated: {} -> {}", lead.getLeadId(), status);
        return salesLeadRepository.save(lead);
    }

    @Transactional
    public SalesLead assignLead(UUID id, UUID assignedTo) {
        SalesLead lead = getLead(id);
        lead.setAssignedTo(assignedTo);
        log.info("Sales lead assigned: {}", lead.getLeadId());
        return salesLeadRepository.save(lead);
    }

    @Transactional
    public void deleteLead(UUID id) {
        SalesLead lead = getLead(id);
        salesLeadRepository.delete(lead);
        log.info("Sales lead deleted: {}", lead.getLeadId());
    }
}
