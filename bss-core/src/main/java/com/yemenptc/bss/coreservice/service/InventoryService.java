package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.NetworkElement;
import com.yemenptc.bss.coreservice.entity.NetworkResource;
import com.yemenptc.bss.coreservice.entity.NumberPool;
import com.yemenptc.bss.coreservice.repository.NetworkElementRepository;
import com.yemenptc.bss.coreservice.repository.NetworkResourceRepository;
import com.yemenptc.bss.coreservice.repository.NumberPoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final NetworkElementRepository elementRepository;
    private final NetworkResourceRepository resourceRepository;
    private final NumberPoolRepository numberPoolRepository;

    @Transactional
    public NetworkElement createElement(NetworkElement request) {
        NetworkElement element = NetworkElement.builder()
            .name(request.getName())
            .type(request.getType())
            .vendor(request.getVendor())
            .model(request.getModel())
            .ipAddress(request.getIpAddress())
            .status(NetworkElement.ElementStatus.ACTIVE)
            .location(request.getLocation())
            .siteId(request.getSiteId())
            .build();
        return elementRepository.save(element);
    }

    @Transactional(readOnly = true)
    public NetworkElement getElement(String id) {
        return elementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Network element not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<NetworkElement> listElements(Pageable pageable) {
        return elementRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<NetworkElement> getElementsByType(String type) {
        return elementRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public List<NetworkElement> getElementsBySite(String siteId) {
        return elementRepository.findBySiteId(siteId);
    }

    @Transactional
    public NetworkResource createResource(NetworkResource request) {
        NetworkResource resource = NetworkResource.builder()
            .resourceIdentifier(request.getResourceIdentifier())
            .resourceType(request.getResourceType())
            .resourceStatus(NetworkResource.ResourceStatus.AVAILABLE)
            .category(request.getCategory())
            .location(request.getLocation())
            .parentResourceId(request.getParentResourceId())
            .build();
        return resourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    public NetworkResource getResource(UUID id) {
        return resourceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Network resource not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<NetworkResource> listResources(Pageable pageable) {
        return resourceRepository.findAll(pageable);
    }

    @Transactional
    public NumberPool createNumberPool(NumberPool request) {
        NumberPool pool = NumberPool.builder()
            .number(request.getNumber())
            .numberType(request.getNumberType())
            .status(NumberPool.NumberStatus.AVAILABLE)
            .exchange(request.getExchange())
            .build();
        return numberPoolRepository.save(pool);
    }

    @Transactional(readOnly = true)
    public NumberPool getNumberPool(String id) {
        return numberPoolRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Number pool not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<NumberPool> listNumberPools(Pageable pageable) {
        return numberPoolRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<NumberPool> getAvailableNumbers(NumberPool.NumberType type, String exchange) {
        return numberPoolRepository.findByNumberTypeAndStatusAndExchange(
            type, NumberPool.NumberStatus.AVAILABLE, exchange);
    }

    @Transactional
    public NumberPool reserveNumber(String id) {
        NumberPool pool = getNumberPool(id);
        pool.setStatus(NumberPool.NumberStatus.RESERVED);
        pool.setUpdatedAt(Instant.now());
        return numberPoolRepository.save(pool);
    }

    @Transactional
    public NumberPool assignNumber(String id, String assignedTo) {
        NumberPool pool = getNumberPool(id);
        pool.setStatus(NumberPool.NumberStatus.ASSIGNED);
        pool.setAssignedTo(assignedTo);
        pool.setUpdatedAt(Instant.now());
        return numberPoolRepository.save(pool);
    }

    @Transactional
    public NumberPool releaseNumber(String id) {
        NumberPool pool = getNumberPool(id);
        pool.setStatus(NumberPool.NumberStatus.AVAILABLE);
        pool.setAssignedTo(null);
        pool.setUpdatedAt(Instant.now());
        return numberPoolRepository.save(pool);
    }
}
