package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {
    List<ProductCategory> findByParentId(String parentId);
    List<ProductCategory> findByParentIdIsNull();
    List<ProductCategory> findByIsActiveTrue();
}
