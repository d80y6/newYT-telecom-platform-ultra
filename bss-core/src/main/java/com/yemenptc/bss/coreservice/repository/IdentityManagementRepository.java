package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.IdentityManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdentityManagementRepository extends JpaRepository<IdentityManagement, UUID> {
    
    Optional<IdentityManagement> findByIdentityId(String identityId);
    
    List<IdentityManagement> findByPartyId(UUID partyId);
    
    List<IdentityManagement> findByIdentityType(IdentityManagement.IdentityType identityType);
    
    List<IdentityManagement> findByStatus(IdentityManagement.IdentityStatus status);
    
    Optional<IdentityManagement> findByIdentityValueAndIdentityType(String identityValue, IdentityManagement.IdentityType identityType);
    
    List<IdentityManagement> findByIsPrimaryTrue();
    
    long countByStatus(IdentityManagement.IdentityStatus status);
}
