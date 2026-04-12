package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Invoice;
import com.yemenptc.bss.coreservice.entity.Payment;
import com.yemenptc.bss.coreservice.repository.InvoiceRepository;
import com.yemenptc.bss.coreservice.repository.PaymentRepository;
import com.yemenptc.bss.coreservice.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BillingService billingService;

    @Test
    void createInvoice_Success() {
        Invoice request = new Invoice();
        request.setAccountId(UUID.randomUUID());
        request.setSubtotalAmount(BigDecimal.valueOf(1000));
        request.setIssueDate(LocalDate.now());
        request.setDueDate(LocalDate.now().plusDays(30));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> {
            Invoice inv = i.getArgument(0);
            inv.setId(UUID.randomUUID());
            inv.setInvoiceNumber("INV-TEST123");
            return inv;
        });

        Invoice result = billingService.createInvoice(request);

        assertNotNull(result);
        assertEquals(Invoice.InvoiceStatus.DRAFT, result.getStatus());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void getInvoice_Success() {
        UUID id = UUID.randomUUID();
        Invoice invoice = new Invoice();
        invoice.setId(id);
        invoice.setInvoiceNumber("INV-TEST123");
        when(invoiceRepository.findById(id)).thenReturn(Optional.of(invoice));

        Invoice result = billingService.getInvoice(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void listInvoices_Success() {
        Page<Invoice> page = new PageImpl<>(List.of());
        when(invoiceRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Invoice> result = billingService.listInvoices(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void createPayment_Success() {
        Payment request = new Payment();
        request.setAccountId(UUID.randomUUID());
        request.setAmount(BigDecimal.valueOf(500));
        request.setPaymentMethod("CASH");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId(UUID.randomUUID());
            p.setPaymentReference("PAY-TEST123");
            return p;
        });

        Payment result = billingService.createPayment(request);

        assertNotNull(result);
        assertEquals(Payment.PaymentStatus.PENDING, result.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void getPayment_Success() {
        UUID id = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setId(id);
        payment.setPaymentReference("PAY-TEST123");
        when(paymentRepository.findById(id)).thenReturn(Optional.of(payment));

        Payment result = billingService.getPayment(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void listPayments_Success() {
        Page<Payment> page = new PageImpl<>(List.of());
        when(paymentRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Payment> result = billingService.listPayments(PageRequest.of(0, 20));

        assertNotNull(result);
    }
}
