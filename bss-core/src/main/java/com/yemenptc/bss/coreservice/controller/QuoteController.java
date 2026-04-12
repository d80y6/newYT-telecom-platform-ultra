package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Quote;
import com.yemenptc.bss.coreservice.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/quoteManagement/v5")
@RequiredArgsConstructor
@Tag(name = "Quote Management", description = "TMF694 Quote Management API")
public class QuoteController {
    private final QuoteService quoteService;

    @PostMapping("/quote")
    @Operation(summary = "Create quote")
    public ResponseEntity<Quote> createQuote(@RequestBody Quote request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quoteService.createQuote(request));
    }

    @GetMapping("/quote")
    public ResponseEntity<Page<Quote>> listQuotes(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(quoteService.listQuotes(PageRequest.of(page, size)));
    }

    @GetMapping("/quote/{id}")
    public ResponseEntity<Quote> getQuote(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.getQuote(id));
    }

    @PatchMapping("/quote/{id}/status")
    public ResponseEntity<Quote> updateQuoteStatus(@PathVariable UUID id, @RequestBody Quote.QuoteStatus status) {
        return ResponseEntity.ok(quoteService.updateQuoteStatus(id, status));
    }
}
