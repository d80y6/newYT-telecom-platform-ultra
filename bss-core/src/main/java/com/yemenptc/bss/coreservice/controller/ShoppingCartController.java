package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.ShoppingCart;
import com.yemenptc.bss.coreservice.service.ShoppingCartService;
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
@RequestMapping("/tmf-api/shoppingCart/v5")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart", description = "TMF695 Shopping Cart API")
public class ShoppingCartController {
    private final ShoppingCartService cartService;

    @PostMapping("/cart")
    @Operation(summary = "Create shopping cart")
    public ResponseEntity<ShoppingCart> createCart(@RequestBody ShoppingCart request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.createCart(request.getCustomerId()));
    }

    @GetMapping("/cart")
    public ResponseEntity<Page<ShoppingCart>> listCarts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(cartService.listCarts(PageRequest.of(page, size)));
    }

    @GetMapping("/cart/{id}")
    public ResponseEntity<ShoppingCart> getCart(@PathVariable UUID id) {
        return ResponseEntity.ok(cartService.getCart(id));
    }

    @PatchMapping("/cart/{id}/status")
    public ResponseEntity<ShoppingCart> updateCartStatus(@PathVariable UUID id, @RequestBody ShoppingCart.CartStatus status) {
        return ResponseEntity.ok(cartService.updateCartStatus(id, status));
    }
}
