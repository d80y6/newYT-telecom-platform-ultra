package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {
    Optional<ShoppingCart> findByCustomerIdAndStatus(UUID customerId, ShoppingCart.CartStatus status);
}
