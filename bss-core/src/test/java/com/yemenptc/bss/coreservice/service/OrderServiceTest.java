package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Order;
import com.yemenptc.bss.coreservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order createTestOrder() {
        Order order = new Order();
        order.setCustomerId(UUID.randomUUID());
        order.setOrderType(Order.OrderType.ACQUISITION);
        order.setChannel("API");
        return order;
    }

    @Test
    void createOrder_Success() {
        Order request = createTestOrder();
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(UUID.randomUUID());
            o.setOrderNumber("ORD-TEST123");
            return o;
        });

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(Order.OrderStatus.ACKNOWLEDGED, result.getStatus());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrder_Success() {
        UUID id = UUID.randomUUID();
        Order order = createTestOrder();
        order.setId(id);
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void listOrders_Success() {
        Page<Order> page = new PageImpl<>(List.of());
        when(orderRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Order> result = orderService.listOrders(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void updateOrder_Success() {
        UUID id = UUID.randomUUID();
        Order existing = createTestOrder();
        existing.setId(id);
        existing.setOrderNumber("ORD-TEST123");
        existing.setStatus(Order.OrderStatus.ACKNOWLEDGED);
        existing.setVersion(1);
        when(orderRepository.findById(id)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order update = new Order();
        update.setStatus(Order.OrderStatus.IN_PROGRESS);

        Order result = orderService.updateOrder(id, update);

        assertNotNull(result);
        assertEquals(Order.OrderStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void deleteOrder_Success() {
        UUID id = UUID.randomUUID();
        when(orderRepository.existsById(id)).thenReturn(true);

        orderService.deleteOrder(id);

        verify(orderRepository).deleteById(id);
    }
}
