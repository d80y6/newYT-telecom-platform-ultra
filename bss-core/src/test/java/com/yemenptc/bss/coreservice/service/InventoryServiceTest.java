package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.NetworkElement;
import com.yemenptc.bss.coreservice.repository.NetworkElementRepository;
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
class InventoryServiceTest {

    @Mock
    private NetworkElementRepository elementRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void createElement_Success() {
        NetworkElement request = new NetworkElement();
        request.setName("Test Element");
        request.setType("ROUTER");
        when(elementRepository.save(any(NetworkElement.class))).thenAnswer(i -> {
            NetworkElement e = i.getArgument(0);
            e.setId(UUID.randomUUID().toString());
            return e;
        });

        NetworkElement result = inventoryService.createElement(request);

        assertNotNull(result);
        verify(elementRepository).save(any(NetworkElement.class));
    }

    @Test
    void getElement_Success() {
        String id = UUID.randomUUID().toString();
        NetworkElement element = new NetworkElement();
        element.setId(id);
        element.setName("Test Element");
        when(elementRepository.findById(id)).thenReturn(Optional.of(element));

        NetworkElement result = inventoryService.getElement(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void listElements_Success() {
        Page<NetworkElement> page = new PageImpl<>(List.of());
        when(elementRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<NetworkElement> result = inventoryService.listElements(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void getElementsByType_Success() {
        String type = "ROUTER";
        when(elementRepository.findByType(type)).thenReturn(List.of());

        List<NetworkElement> result = inventoryService.getElementsByType(type);

        assertNotNull(result);
    }
}
