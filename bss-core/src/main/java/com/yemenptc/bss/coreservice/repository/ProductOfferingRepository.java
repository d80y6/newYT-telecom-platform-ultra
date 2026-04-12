package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ProductOffering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductOfferingRepository extends JpaRepository<ProductOffering, UUID> {
    Page<ProductOffering> findByStatus(ProductOffering.ProductStatus status, Pageable pageable);
    Page<ProductOffering> findByServiceType(String serviceType, Pageable pageable);
    Page<ProductOffering> findByCategoryId(String categoryId, Pageable pageable);
    Page<ProductOffering> findByStatusAndServiceType(ProductOffering.ProductStatus status, String serviceType, Pageable pageable);
    List<ProductOffering> findByIsBundleTrue();
    long countByStatus(ProductOffering.ProductStatus status);
}
