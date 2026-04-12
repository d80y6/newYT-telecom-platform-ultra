package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    List<OrderItem> findByOrderId(String orderId);
    List<OrderItem> findByOrderIdAndStatus(String orderId, OrderItem.ItemStatus status);
    long countByOrderIdAndStatus(String orderId, OrderItem.ItemStatus status);
}
