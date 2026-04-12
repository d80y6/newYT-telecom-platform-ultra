package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Subscription;
import com.yemenptc.bss.coreservice.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Subscription createTestSubscription() {
        Subscription sub = new Subscription();
        sub.setCustomerId(UUID.randomUUID().toString());
        sub.setAccountId(UUID.randomUUID().toString());
        sub.setServiceType("MOBILE_4G");
        sub.setPlanName("Test Plan");
        return sub;
    }

    @Test
    void createSubscription_Success() {
        Subscription request = createTestSubscription();
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> {
            Subscription s = i.getArgument(0);
            s.setId(UUID.randomUUID().toString());
            s.setSubscriptionNumber("SUB-TEST123");
            return s;
        });

        Subscription result = subscriptionService.createSubscription(request);

        assertNotNull(result);
        assertEquals(Subscription.SubscriptionStatus.PENDING, result.getStatus());
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void getSubscription_Success() {
        String id = UUID.randomUUID().toString();
        Subscription sub = createTestSubscription();
        sub.setId(id);
        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(sub));

        Subscription result = subscriptionService.getSubscription(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getSubscription_NotFound() {
        String id = UUID.randomUUID().toString();
        when(subscriptionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> subscriptionService.getSubscription(id));
    }

    @Test
    void listSubscriptions_Success() {
        Page<Subscription> page = new PageImpl<>(List.of());
        when(subscriptionRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Subscription> result = subscriptionService.listSubscriptions(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void getSubscriptionsByCustomer_Success() {
        String customerId = UUID.randomUUID().toString();
        when(subscriptionRepository.findByCustomerId(customerId)).thenReturn(List.of());

        List<Subscription> result = subscriptionService.getSubscriptionsByCustomer(customerId);

        assertNotNull(result);
    }

    @Test
    void getSubscriptionsByAccount_Success() {
        String accountId = UUID.randomUUID().toString();
        when(subscriptionRepository.findByAccountId(accountId)).thenReturn(List.of());

        List<Subscription> result = subscriptionService.getSubscriptionsByAccount(accountId);

        assertNotNull(result);
    }

    @Test
    void activateSubscription_Success() {
        String id = UUID.randomUUID().toString();
        Subscription existing = createTestSubscription();
        existing.setId(id);
        existing.setStatus(Subscription.SubscriptionStatus.PENDING);
        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));

        Subscription result = subscriptionService.activateSubscription(id);

        assertNotNull(result);
        assertEquals(Subscription.SubscriptionStatus.ACTIVE, result.getStatus());
        assertNotNull(result.getStartDate());
    }

    @Test
    void suspendSubscription_Success() {
        String id = UUID.randomUUID().toString();
        Subscription existing = createTestSubscription();
        existing.setId(id);
        existing.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));

        Subscription result = subscriptionService.suspendSubscription(id);

        assertNotNull(result);
        assertEquals(Subscription.SubscriptionStatus.SUSPENDED, result.getStatus());
    }

    @Test
    void terminateSubscription_Success() {
        String id = UUID.randomUUID().toString();
        Subscription existing = createTestSubscription();
        existing.setId(id);
        existing.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(i -> i.getArgument(0));

        Subscription result = subscriptionService.terminateSubscription(id);

        assertNotNull(result);
        assertEquals(Subscription.SubscriptionStatus.TERMINATED, result.getStatus());
        assertNotNull(result.getEndDate());
    }

    @Test
    void deleteSubscription_Success() {
        String id = UUID.randomUUID().toString();
        subscriptionService.deleteSubscription(id);
        verify(subscriptionRepository).deleteById(id);
    }
}
