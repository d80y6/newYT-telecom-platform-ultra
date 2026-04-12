package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.PaymentHistoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentHistoryItemRepository extends JpaRepository<PaymentHistoryItem, UUID> {

    List<PaymentHistoryItem> findByBillingSummaryId(UUID billingSummaryId);
}
