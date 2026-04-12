package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.PaymentBehavior;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentBehaviorRepository extends JpaRepository<PaymentBehavior, UUID> {

    Optional<PaymentBehavior> findByCreditProfileId(UUID creditProfileId);
}
