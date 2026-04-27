package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ConvergentBillingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConvergentBillingAccountRepository extends JpaRepository<ConvergentBillingAccount, UUID> {
    Optional<ConvergentBillingAccount> findByAccountId(String accountId);
}
