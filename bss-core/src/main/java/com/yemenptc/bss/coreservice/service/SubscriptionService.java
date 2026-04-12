package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Subscription;
import com.yemenptc.bss.coreservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public Subscription createSubscription(Subscription request) {
        Subscription subscription = Subscription.builder()
            .subscriptionNumber("SUB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .customerId(request.getCustomerId())
            .accountId(request.getAccountId())
            .serviceType(request.getServiceType())
            .status(Subscription.SubscriptionStatus.PENDING)
            .productOfferingId(request.getProductOfferingId())
            .serviceIdentifier(request.getServiceIdentifier())
            .planName(request.getPlanName())
            .monthlyFee(request.getMonthlyFee())
            .startDate(request.getStartDate() != null ? request.getStartDate() : Instant.now())
            .endDate(request.getEndDate())
            .build();
        return subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public Subscription getSubscription(String id) {
        return subscriptionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Subscription> listSubscriptions(Pageable pageable) {
        return subscriptionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByCustomer(String customerId) {
        return subscriptionRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByAccount(String accountId) {
        return subscriptionRepository.findByAccountId(accountId);
    }

    @Transactional
    public Subscription activateSubscription(String id) {
        Subscription subscription = getSubscription(id);
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        subscription.setStartDate(Instant.now());
        subscription.setUpdatedAt(Instant.now());
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription suspendSubscription(String id) {
        Subscription subscription = getSubscription(id);
        subscription.setStatus(Subscription.SubscriptionStatus.SUSPENDED);
        subscription.setUpdatedAt(Instant.now());
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription terminateSubscription(String id) {
        Subscription subscription = getSubscription(id);
        subscription.setStatus(Subscription.SubscriptionStatus.TERMINATED);
        subscription.setEndDate(Instant.now());
        subscription.setUpdatedAt(Instant.now());
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription updateSubscription(String id, Subscription request) {
        Subscription existing = getSubscription(id);
        Subscription updated = Subscription.builder()
            .subscriptionNumber(existing.getSubscriptionNumber())
            .customerId(request.getCustomerId() != null ? request.getCustomerId() : existing.getCustomerId())
            .accountId(request.getAccountId() != null ? request.getAccountId() : existing.getAccountId())
            .serviceType(request.getServiceType() != null ? request.getServiceType() : existing.getServiceType())
            .status(request.getStatus() != null ? request.getStatus() : existing.getStatus())
            .productOfferingId(request.getProductOfferingId() != null ? request.getProductOfferingId() : existing.getProductOfferingId())
            .serviceIdentifier(request.getServiceIdentifier() != null ? request.getServiceIdentifier() : existing.getServiceIdentifier())
            .planName(request.getPlanName() != null ? request.getPlanName() : existing.getPlanName())
            .monthlyFee(request.getMonthlyFee() != null ? request.getMonthlyFee() : existing.getMonthlyFee())
            .startDate(existing.getStartDate())
            .endDate(request.getEndDate() != null ? request.getEndDate() : existing.getEndDate())
            .build();
        updated.setId(id);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(Instant.now());
        updated.setVersion(existing.getVersion() + 1);
        return subscriptionRepository.save(updated);
    }

    @Transactional
    public void deleteSubscription(String id) {
        subscriptionRepository.deleteById(id);
    }
}
