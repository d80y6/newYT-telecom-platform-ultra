package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, UUID> {
    List<Quote> findByCustomerId(UUID customerId);
    List<Quote> findByStatus(Quote.QuoteStatus status);
}
