package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ServiceSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceSpecificationRepository extends JpaRepository<ServiceSpecification, UUID> {

    Optional<ServiceSpecification> findBySpecificationId(String specificationId);

    List<ServiceSpecification> findByState(ServiceSpecification.SpecState state);

    List<ServiceSpecification> findByServiceType(String serviceType);

    boolean existsBySpecificationId(String specificationId);

    void deleteBySpecificationId(String specificationId);
}
