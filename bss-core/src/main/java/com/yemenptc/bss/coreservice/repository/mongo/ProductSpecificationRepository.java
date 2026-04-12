package com.yemenptc.bss.coreservice.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB repository for product specifications.
 */
@Repository
public interface ProductSpecificationRepository extends MongoRepository<ProductSpecificationDocument, String> {

    List<ProductSpecificationDocument> findByServiceType(String serviceType);

    List<ProductSpecificationDocument> findByLifecycleStatus(String status);

    List<ProductSpecificationDocument> findByNameContaining(String name);
}
