package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.TroubleTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TroubleTicketRepository extends JpaRepository<TroubleTicket, UUID> {
    List<TroubleTicket> findByPartyId(UUID partyId);
    List<TroubleTicket> findByStatus(TroubleTicket.TicketStatus status);
    List<TroubleTicket> findBySeverity(TroubleTicket.TicketSeverity severity);
    Page<TroubleTicket> findByStatus(TroubleTicket.TicketStatus status, Pageable pageable);
    Page<TroubleTicket> findBySeverity(TroubleTicket.TicketSeverity severity, Pageable pageable);
}
