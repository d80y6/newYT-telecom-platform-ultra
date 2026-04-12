package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Subscription;
import com.yemenptc.bss.coreservice.service.SubscriptionService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tmf-api/productInventory/v5/productInventory")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<TmfResponse<Subscription>> create(@RequestBody Subscription request) {
        Subscription subscription = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(subscription, "Subscription"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TmfResponse<Subscription>> getById(@PathVariable String id) {
        Subscription subscription = subscriptionService.getSubscription(id);
        return ResponseEntity.ok(TmfResponse.success(subscription, "Subscription"));
    }

    @GetMapping
    public ResponseEntity<TmfResponse<Subscription>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Subscription> result = subscriptionService.listSubscriptions(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "Subscription"));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<TmfResponse<Subscription>> getByCustomer(@PathVariable String customerId) {
        List<Subscription> subscriptions = subscriptionService.getSubscriptionsByCustomer(customerId);
        return ResponseEntity.ok(TmfResponse.list(
                subscriptions, subscriptions.size(), 0, subscriptions.size(), "Subscription"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TmfResponse<Subscription>> update(
            @PathVariable String id, @RequestBody Subscription request) {
        Subscription subscription = subscriptionService.updateSubscription(id, request);
        return ResponseEntity.ok(TmfResponse.success(subscription, "Subscription"));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<TmfResponse<Subscription>> activate(@PathVariable String id) {
        Subscription subscription = subscriptionService.activateSubscription(id);
        return ResponseEntity.ok(TmfResponse.success(subscription, "Subscription"));
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<TmfResponse<Subscription>> suspend(@PathVariable String id) {
        Subscription subscription = subscriptionService.suspendSubscription(id);
        return ResponseEntity.ok(TmfResponse.success(subscription, "Subscription"));
    }

    @PostMapping("/{id}/terminate")
    public ResponseEntity<TmfResponse<Subscription>> terminate(@PathVariable String id) {
        Subscription subscription = subscriptionService.terminateSubscription(id);
        return ResponseEntity.ok(TmfResponse.success(subscription, "Subscription"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
