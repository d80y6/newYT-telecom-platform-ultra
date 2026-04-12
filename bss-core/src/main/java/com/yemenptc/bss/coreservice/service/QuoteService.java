package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Quote;
import com.yemenptc.bss.coreservice.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuoteService {
    private final QuoteRepository quoteRepository;

    @Transactional
    public Quote createQuote(Quote request) {
        Quote quote = Quote.builder()
            .quoteId(request.getQuoteId() != null ? request.getQuoteId() : "QTE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .customerId(request.getCustomerId())
            .status(Quote.QuoteStatus.DRAFT)
            .totalAmount(request.getTotalAmount())
            .currency(request.getCurrency() != null ? request.getCurrency() : "YER")
            .validUntil(request.getValidUntil())
            .build();
        return quoteRepository.save(quote);
    }

    @Transactional(readOnly = true)
    public Quote getQuote(UUID id) {
        return quoteRepository.findById(id).orElseThrow(() -> new RuntimeException("Quote not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Quote> listQuotes(Pageable pageable) {
        return quoteRepository.findAll(pageable);
    }

    @Transactional
    public Quote updateQuoteStatus(UUID id, Quote.QuoteStatus status) {
        Quote quote = getQuote(id);
        quote.setStatus(status);
        return quoteRepository.save(quote);
    }
}
