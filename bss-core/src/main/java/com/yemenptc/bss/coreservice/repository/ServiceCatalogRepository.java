package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, UUID> {

    Optional<ServiceCatalog> findByCatalogId(String catalogId);

    List<ServiceCatalog> findByStatus(ServiceCatalog.CatalogStatus status);

    List<ServiceCatalog> findByCategory(String category);

    boolean existsByCatalogId(String catalogId);

    void deleteByCatalogId(String catalogId);
}
