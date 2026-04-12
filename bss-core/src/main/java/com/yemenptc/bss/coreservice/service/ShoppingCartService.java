package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ShoppingCart;
import com.yemenptc.bss.coreservice.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {
    private final ShoppingCartRepository cartRepository;

    @Transactional
    public ShoppingCart createCart(UUID customerId) {
        return cartRepository.save(ShoppingCart.builder()
            .cartId("CART-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .customerId(customerId)
            .status(ShoppingCart.CartStatus.ACTIVE)
            .build());
    }

    @Transactional(readOnly = true)
    public ShoppingCart getCart(UUID id) {
        return cartRepository.findById(id).orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Transactional(readOnly = true)
    public Page<ShoppingCart> listCarts(Pageable pageable) {
        return cartRepository.findAll(pageable);
    }

    @Transactional
    public ShoppingCart updateCartStatus(UUID id, ShoppingCart.CartStatus status) {
        ShoppingCart cart = cartRepository.findById(id).orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.setStatus(status);
        return cartRepository.save(cart);
    }
}
