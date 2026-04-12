package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.ContactInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactInformationRepository extends JpaRepository<ContactInformation, UUID> {

    Optional<ContactInformation> findByCustomer360Id(UUID customer360Id);
}
