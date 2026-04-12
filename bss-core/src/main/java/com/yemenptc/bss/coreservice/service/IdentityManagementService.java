package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.IdentityManagement;
import com.yemenptc.bss.coreservice.repository.IdentityManagementRepository;
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
public class IdentityManagementService {

    private final IdentityManagementRepository identityRepository;

    @Transactional
    public IdentityManagement createIdentity(IdentityManagement request) {
        log.info("Creating identity: {} - {}", request.getIdentityType(), request.getIdentityValue());

        IdentityManagement identity = IdentityManagement.builder()
            .identityId(request.getIdentityId() != null ? 
                request.getIdentityId() : "IDM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .partyId(request.getPartyId())
            .identityType(request.getIdentityType())
            .identityValue(request.getIdentityValue())
            .isVerified(false)
            .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
            .status(IdentityManagement.IdentityStatus.PENDING)
            .build();

        IdentityManagement saved = identityRepository.save(identity);
        log.info("Identity created: {}", saved.getIdentityId());
        return saved;
    }

    @Transactional(readOnly = true)
    public IdentityManagement getIdentity(UUID id) {
        return identityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Identity not found: " + id));
    }

    @Transactional(readOnly = true)
    public IdentityManagement getIdentityByIdentityId(String identityId) {
        return identityRepository.findByIdentityId(identityId)
            .orElseThrow(() -> new RuntimeException("Identity not found: " + identityId));
    }

    @Transactional(readOnly = true)
    public Page<IdentityManagement> listIdentities(Pageable pageable) {
        return identityRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<IdentityManagement> getIdentitiesByParty(UUID partyId) {
        return identityRepository.findByPartyId(partyId);
    }

    @Transactional(readOnly = true)
    public List<IdentityManagement> getIdentitiesByType(IdentityManagement.IdentityType type) {
        return identityRepository.findByIdentityType(type);
    }

    @Transactional(readOnly = true)
    public List<IdentityManagement> getIdentitiesByStatus(IdentityManagement.IdentityStatus status) {
        return identityRepository.findByStatus(status);
    }

    @Transactional
    public IdentityManagement verifyIdentity(UUID id) {
        IdentityManagement identity = getIdentity(id);
        identity.setIsVerified(true);
        identity.setStatus(IdentityManagement.IdentityStatus.VERIFIED);
        identity.setVerifiedAt(Instant.now());
        identity.setLastVerifiedAt(Instant.now());
        log.info("Identity verified: {}", identity.getIdentityId());
        return identityRepository.save(identity);
    }

    @Transactional
    public IdentityManagement setPrimary(UUID id) {
        IdentityManagement identity = getIdentity(id);
        List<IdentityManagement> existingPrimary = identityRepository.findByPartyId(identity.getPartyId())
            .stream()
            .filter(IdentityManagement::getIsPrimary)
            .toList();
        
        for (IdentityManagement existing : existingPrimary) {
            existing.setIsPrimary(false);
            identityRepository.save(existing);
        }
        
        identity.setIsPrimary(true);
        log.info("Identity set as primary: {}", identity.getIdentityId());
        return identityRepository.save(identity);
    }

    @Transactional
    public IdentityManagement revokeIdentity(UUID id) {
        IdentityManagement identity = getIdentity(id);
        identity.setStatus(IdentityManagement.IdentityStatus.REVOKED);
        log.info("Identity revoked: {}", identity.getIdentityId());
        return identityRepository.save(identity);
    }

    @Transactional
    public IdentityManagement updateIdentity(UUID id, IdentityManagement request) {
        IdentityManagement identity = getIdentity(id);
        if (request.getIdentityValue() != null) identity.setIdentityValue(request.getIdentityValue());
        if (request.getIdentityType() != null) identity.setIdentityType(request.getIdentityType());
        return identityRepository.save(identity);
    }

    @Transactional
    public void deleteIdentity(UUID id) {
        IdentityManagement identity = getIdentity(id);
        identityRepository.delete(identity);
        log.info("Identity deleted: {}", identity.getIdentityId());
    }

    @Transactional(readOnly = true)
    public long countByStatus(IdentityManagement.IdentityStatus status) {
        return identityRepository.countByStatus(status);
    }
}
