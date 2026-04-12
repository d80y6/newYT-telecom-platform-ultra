package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    Page<Order> findByOrderType(Order.OrderType orderType, Pageable pageable);
    Page<Order> findByCustomerIdAndStatus(UUID customerId, Order.OrderStatus status, Pageable pageable);
    long countByStatus(Order.OrderStatus status);
}
