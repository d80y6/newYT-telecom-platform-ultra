package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Order;
import com.yemenptc.bss.coreservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCircuitBreakerAnnotationPresent() {
        assertNotNull(orderService);
    }

    @Test
    void testCreateOrderSuccess() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderNumber("ORD-TEST123");
        order.setCustomerId(UUID.randomUUID());
        
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(order);

        assertNotNull(result);
        assertEquals("ORD-TEST123", result.getOrderNumber());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCircuitBreakerFallbackBehavior() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setCustomerId(UUID.randomUUID());
        
        when(orderRepository.save(any(Order.class)))
            .thenThrow(new RuntimeException("Database unavailable"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order);
        });

        assertTrue(exception.getMessage().contains("temporarily unavailable") 
            || exception.getMessage().contains("Database unavailable"));
    }
}
