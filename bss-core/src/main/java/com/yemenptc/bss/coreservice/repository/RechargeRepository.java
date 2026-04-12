package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Recharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface RechargeRepository extends JpaRepository<Recharge, String> {
    Optional<Recharge> findByTransactionId(String transactionId);
    List<Recharge> findByAccountId(String accountId);
    List<Recharge> findByMsisdn(String msisdn);
    List<Recharge> findByStatus(Recharge.RechargeStatus status);
}
