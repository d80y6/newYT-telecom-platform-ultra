package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.ServiceOrder;
import com.yemenptc.bss.coreservice.repository.ServiceOrderRepository;
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
class ProvisioningServiceTest {

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @InjectMocks
    private ProvisioningService provisioningService;

    private ServiceOrder createTestServiceOrder() {
        ServiceOrder order = new ServiceOrder();
        order.setOrderType(ServiceOrder.ServiceOrderType.ACTIVATION);
        order.setCfsType("MOBILE");
        order.setRfsType("4G");
        return order;
    }

    @Test
    void createServiceOrder_Success() {
        ServiceOrder request = createTestServiceOrder();
        when(serviceOrderRepository.save(any(ServiceOrder.class))).thenAnswer(i -> {
            ServiceOrder o = i.getArgument(0);
            o.setId(UUID.randomUUID());
            o.setOrderNumber("SO-TEST123");
            return o;
        });

        ServiceOrder result = provisioningService.createServiceOrder(request);

        assertNotNull(result);
        assertEquals(ServiceOrder.ServiceOrderStatus.ACKNOWLEDGED, result.getStatus());
        verify(serviceOrderRepository).save(any(ServiceOrder.class));
    }

    @Test
    void getServiceOrder_Success() {
        UUID id = UUID.randomUUID();
        ServiceOrder order = createTestServiceOrder();
        order.setId(id);
        when(serviceOrderRepository.findById(id)).thenReturn(Optional.of(order));

        ServiceOrder result = provisioningService.getServiceOrder(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void listServiceOrders_Success() {
        Page<ServiceOrder> page = new PageImpl<>(List.of());
        when(serviceOrderRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<ServiceOrder> result = provisioningService.listServiceOrders(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void getServiceOrder_NotFound() {
        UUID id = UUID.randomUUID();
        when(serviceOrderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> provisioningService.getServiceOrder(id));
    }

    @Test
    void completeOrder_Success() {
        UUID id = UUID.randomUUID();
        ServiceOrder existing = createTestServiceOrder();
        existing.setId(id);
        existing.setStatus(ServiceOrder.ServiceOrderStatus.IN_PROGRESS);
        when(serviceOrderRepository.findById(id)).thenReturn(Optional.of(existing));
        when(serviceOrderRepository.save(any(ServiceOrder.class))).thenAnswer(i -> i.getArgument(0));

        ServiceOrder result = provisioningService.completeOrder(id);

        assertNotNull(result);
        assertEquals(ServiceOrder.ServiceOrderStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getCompletedAt());
    }

    @Test
    void failOrder_Success() {
        UUID id = UUID.randomUUID();
        ServiceOrder existing = createTestServiceOrder();
        existing.setId(id);
        existing.setStatus(ServiceOrder.ServiceOrderStatus.IN_PROGRESS);
        when(serviceOrderRepository.findById(id)).thenReturn(Optional.of(existing));
        when(serviceOrderRepository.save(any(ServiceOrder.class))).thenAnswer(i -> i.getArgument(0));

        ServiceOrder result = provisioningService.failOrder(id, "Test error");

        assertNotNull(result);
        assertEquals(ServiceOrder.ServiceOrderStatus.FAILED, result.getStatus());
    }
}
