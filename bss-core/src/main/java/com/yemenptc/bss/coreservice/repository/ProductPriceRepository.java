package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, String> {
    List<ProductPrice> findByProductOfferingId(String productOfferingId);
    List<ProductPrice> findByProductOfferingIdAndPriceType(String productOfferingId, ProductPrice.PriceType priceType);
    List<ProductPrice> findByPriceType(ProductPrice.PriceType priceType);
    @Query("SELECT p FROM ProductPrice p WHERE p.productOfferingId = :offeringId AND p.priceType = :priceType AND (p.validFrom IS NULL OR p.validFrom <= :now) AND (p.validTo IS NULL OR p.validTo >= :now)")
    Optional<ProductPrice> findActivePrice(@Param("offeringId") String offeringId, @Param("priceType") ProductPrice.PriceType priceType, @Param("now") Instant now);
}
