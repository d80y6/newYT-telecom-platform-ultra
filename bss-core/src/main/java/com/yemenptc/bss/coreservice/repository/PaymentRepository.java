package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByPaymentReference(String paymentReference);
    List<Payment> findByAccountId(UUID accountId);
    List<Payment> findByInvoiceId(UUID invoiceId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
}
