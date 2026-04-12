package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Customer;
import com.yemenptc.bss.coreservice.service.CustomerService;
import com.yemenptc.bss.sdk.response.TmfResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping({"/tmf-api/customerManagement/v5/party", "/tmf-api/customerManagement/v5/customer"})
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<TmfResponse<Customer>> create(@RequestBody Customer request) {
        Customer customer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TmfResponse.success(customer, "Customer"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TmfResponse<Customer>> getById(@PathVariable UUID id) {
        Customer customer = customerService.getCustomer(id);
        return ResponseEntity.ok(TmfResponse.success(customer, "Customer"));
    }

    @GetMapping
    public ResponseEntity<TmfResponse<Customer>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Customer> result = customerService.listCustomers(PageRequest.of(page, size));
        return ResponseEntity.ok(TmfResponse.list(
                result.getContent(), (int) result.getTotalElements(),
                page * size, size, "Customer"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TmfResponse<Customer>> update(
            @PathVariable UUID id, @RequestBody Customer request) {
        Customer customer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(TmfResponse.success(customer, "Customer"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
