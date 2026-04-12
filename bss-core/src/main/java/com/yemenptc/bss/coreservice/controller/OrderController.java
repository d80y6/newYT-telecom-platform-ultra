package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Order;
import com.yemenptc.bss.coreservice.service.OrderService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/productOrderingManagement/v5/productOrder")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<TmfResponse<Order>> create(@RequestBody Order request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(order, "ProductOrder"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TmfResponse<Order>> getById(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(TmfResponse.success(order, "ProductOrder"));
    }

    @GetMapping
    public ResponseEntity<TmfResponse<Order>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Order> result = orderService.listOrders(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "ProductOrder"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TmfResponse<Order>> update(
            @PathVariable UUID id, @RequestBody Order request) {
        Order order = orderService.updateOrder(id, request);
        return ResponseEntity.ok(TmfResponse.success(order, "ProductOrder"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
