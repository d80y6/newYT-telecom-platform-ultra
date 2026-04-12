package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    Optional<Subscription> findBySubscriptionNumber(String subscriptionNumber);
    List<Subscription> findByCustomerId(String customerId);
    List<Subscription> findByAccountId(String accountId);
    List<Subscription> findByStatus(Subscription.SubscriptionStatus status);
    List<Subscription> findByServiceType(String serviceType);
    List<Subscription> findByServiceIdentifier(String serviceIdentifier);
    long countByStatus(Subscription.SubscriptionStatus status);
}
