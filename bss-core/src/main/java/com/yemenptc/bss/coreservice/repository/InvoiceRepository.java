package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Page<Invoice> findByAccountId(UUID accountId, Pageable pageable);
    Page<Invoice> findByStatus(Invoice.InvoiceStatus status, Pageable pageable);
    Page<Invoice> findByAccountIdAndStatus(UUID accountId, Invoice.InvoiceStatus status, Pageable pageable);
    List<Invoice> findByStatusAndDueDateBefore(Invoice.InvoiceStatus status, LocalDate dueDate);
}
