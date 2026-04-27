package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Invoice;
import com.yemenptc.bss.coreservice.entity.InvoiceItem;
import com.yemenptc.bss.coreservice.entity.Payment;
import com.yemenptc.bss.coreservice.entity.Account;
import com.yemenptc.bss.coreservice.repository.InvoiceRepository;
import com.yemenptc.bss.coreservice.repository.PaymentRepository;
import com.yemenptc.bss.coreservice.repository.AccountRepository;
import com.yemenptc.bss.coreservice.repository.InvoiceItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;

    @Transactional
    @CircuitBreaker(name = "billingService", fallbackMethod = "createInvoiceFallback")
    @Retry(name = "billingService")
    @Bulkhead(name = "billingService")
    public Invoice createInvoice(Invoice request) {
        if (request.getSubtotal() == null || request.getSubtotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invoice subtotal amount must be greater than zero");
        }
        
        Invoice invoice = Invoice.builder()
            .accountId(request.getAccountId())
            .status(Invoice.InvoiceStatus.DRAFT)
            .subtotal(request.getSubtotal())
            .discountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO)
            .taxAmount(BigDecimal.ZERO)
            .totalAmount(BigDecimal.ZERO)
            .currency("YER")
            .issueDate(request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now())
            .dueDate(request.getDueDate() != null ? request.getDueDate() : LocalDate.now().plusDays(30))
            .build();
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice finalizeInvoice(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));
        
        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw new RuntimeException("Invoice cannot be finalized in status: " + invoice.getStatus());
        }
        
        BigDecimal taxRate = new BigDecimal("0.05");
        BigDecimal taxableAmount = invoice.getSubtotal().subtract(invoice.getDiscountAmount());
        BigDecimal taxAmount = taxableAmount.multiply(taxRate);
        BigDecimal total = taxableAmount.add(taxAmount);
        
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(total);
        invoice.setStatus(Invoice.InvoiceStatus.FINALIZED);
        invoice.setUpdatedAt(Instant.now());
        
        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public Invoice getInvoice(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));
    }

    @Transactional(readOnly = true)
    public Page<Invoice> listInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Invoice> getInvoicesByAccount(UUID accountId, Pageable pageable) {
        return invoiceRepository.findByAccountId(accountId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Invoice> getInvoicesByStatus(Invoice.InvoiceStatus status, Pageable pageable) {
        return invoiceRepository.findByStatus(status, pageable);
    }

    @Transactional
    public Payment createPayment(Payment request) {
        Payment payment = Payment.builder()
            .accountId(request.getAccountId())
            .invoiceId(request.getInvoiceId())
            .amount(request.getAmount())
            .paymentMethod(request.getPaymentMethod())
            .status(Payment.PaymentStatus.PENDING)
            .currency("YER")
            .build();
        return paymentRepository.save(payment);
    }

    @Transactional
    @CircuitBreaker(name = "billingService", fallbackMethod = "processPaymentFallback")
    @Retry(name = "billingService")
    @Bulkhead(name = "billingService")
    public Payment processPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new RuntimeException("Payment cannot be processed in status: " + payment.getStatus());
        }
        
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setCompletedAt(Instant.now());
        paymentRepository.save(payment);
        
        if (payment.getAccountId() != null) {
            Account account = accountRepository.findById(payment.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
            BigDecimal newBalance = account.getBalance().add(payment.getAmount());
            account.setBalance(newBalance);
            account.setUpdatedAt(Instant.now());
            accountRepository.save(account);
        }
        
        if (payment.getInvoiceId() != null) {
            Invoice invoice = invoiceRepository.findById(payment.getInvoiceId()).orElse(null);
            if (invoice != null && invoice.getStatus() == Invoice.InvoiceStatus.FINALIZED) {
                invoice.setStatus(Invoice.InvoiceStatus.PAID);
                invoice.setUpdatedAt(Instant.now());
                invoiceRepository.save(invoice);
            }
        }
        
        return payment;
    }

    @Transactional(readOnly = true)
    public Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByAccount(UUID accountId) {
        return paymentRepository.findByAccountId(accountId);
    }

    @Transactional(readOnly = true)
    public Page<Payment> listPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    private Invoice createInvoiceFallback(Invoice request, Throwable t) {
        log.error("Circuit breaker fallback for createInvoice: {}", t.getMessage());
        throw new RuntimeException("Billing service temporarily unavailable. Please try again later.", t);
    }

    private Payment processPaymentFallback(UUID paymentId, Throwable t) {
        log.error("Circuit breaker fallback for processPayment: {}", t.getMessage());
        throw new RuntimeException("Payment processing temporarily unavailable. Please try again later.", t);
    }
}
