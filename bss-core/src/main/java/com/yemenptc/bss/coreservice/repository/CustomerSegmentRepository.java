package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.CustomerSegment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerSegmentRepository extends JpaRepository<CustomerSegment, UUID> {

    List<CustomerSegment> findByCustomer360Id(UUID customer360Id);

    @Query("SELECT cs FROM CustomerSegment cs WHERE " +
           "cs.customer360.id = :customer360Id AND " +
           "cs.segmentType = :segmentType")
    List<CustomerSegment> findByCustomer360IdAndSegmentType(
            @Param("customer360Id") UUID customer360Id,
            @Param("segmentType") String segmentType);
}
