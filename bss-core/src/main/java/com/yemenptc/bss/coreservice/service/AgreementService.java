package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Agreement;
import com.yemenptc.bss.coreservice.repository.AgreementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgreementService {

    private final AgreementRepository agreementRepository;

    @Transactional
    public Agreement createAgreement(Agreement request) {
        log.info("Creating agreement: {}", request.getAgreementType());
        
        Agreement agreement = Agreement.builder()
            .agreementNumber("AGR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .agreementType(request.getAgreementType())
            .description(request.getDescription())
            .status(Agreement.AgreementStatus.DRAFT)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .customerId(request.getCustomerId())
            .customerName(request.getCustomerName())
            .contractValue(request.getContractValue())
            .terms(request.getTerms())
            .build();
        
        return agreementRepository.save(agreement);
    }

    @Transactional(readOnly = true)
    public Agreement getAgreement(UUID id) {
        return agreementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agreement not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Agreement> listAgreements(Pageable pageable) {
        return agreementRepository.findAll(pageable);
    }

    @Transactional
    public Agreement activateAgreement(UUID id) {
        Agreement agreement = getAgreement(id);
        agreement.setStatus(Agreement.AgreementStatus.ACTIVE);
        agreement.setUpdatedAt(Instant.now());
        log.info("Agreement activated: {}", id);
        return agreementRepository.save(agreement);
    }

    @Transactional
    public Agreement terminateAgreement(UUID id) {
        Agreement agreement = getAgreement(id);
        agreement.setStatus(Agreement.AgreementStatus.TERMINATED);
        agreement.setUpdatedAt(Instant.now());
        log.info("Agreement terminated: {}", id);
        return agreementRepository.save(agreement);
    }
}