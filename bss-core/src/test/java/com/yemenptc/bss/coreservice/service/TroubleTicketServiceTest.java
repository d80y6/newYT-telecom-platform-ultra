package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.TroubleTicket;
import com.yemenptc.bss.coreservice.repository.TroubleTicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TroubleTicketServiceTest {

    @Mock
    private TroubleTicketRepository ticketRepository;

    @InjectMocks
    private TroubleTicketService ticketService;

    @Test
    void createTicket_Success() {
        TroubleTicket request = new TroubleTicket();
        request.setTicketType("NETWORK_ISSUE");
        request.setSeverity(TroubleTicket.TicketSeverity.MAJOR);
        request.setDescription("Test ticket");
        when(ticketRepository.save(any(TroubleTicket.class))).thenAnswer(i -> {
            TroubleTicket t = i.getArgument(0);
            t.setId(UUID.randomUUID());
            t.setTicketNumber("TT-TEST123");
            return t;
        });

        TroubleTicket result = ticketService.createTicket(request);

        assertNotNull(result);
        assertEquals(TroubleTicket.TicketStatus.SUBMITTED, result.getStatus());
        verify(ticketRepository).save(any(TroubleTicket.class));
    }

    @Test
    void getTicket_Success() {
        UUID id = UUID.randomUUID();
        TroubleTicket ticket = new TroubleTicket();
        ticket.setId(id);
        ticket.setTicketNumber("TT-TEST123");
        when(ticketRepository.findById(id)).thenReturn(Optional.of(ticket));

        TroubleTicket result = ticketService.getTicket(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void listTickets_Success() {
        Page<TroubleTicket> page = new PageImpl<>(List.of());
        when(ticketRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<TroubleTicket> result = ticketService.listTickets(PageRequest.of(0, 20));

        assertNotNull(result);
    }

    @Test
    void getTicketsByStatus_Success() {
        when(ticketRepository.findByStatus(TroubleTicket.TicketStatus.SUBMITTED))
            .thenReturn(List.of());

        List<TroubleTicket> result = ticketService.getTicketsByStatus(TroubleTicket.TicketStatus.SUBMITTED);

        assertNotNull(result);
    }

    @Test
    void closeTicket_Success() {
        UUID id = UUID.randomUUID();
        TroubleTicket existing = new TroubleTicket();
        existing.setId(id);
        existing.setTicketNumber("TT-TEST123");
        existing.setStatus(TroubleTicket.TicketStatus.IN_PROGRESS);
        when(ticketRepository.findById(id)).thenReturn(Optional.of(existing));
        when(ticketRepository.save(any(TroubleTicket.class))).thenAnswer(i -> i.getArgument(0));

        TroubleTicket result = ticketService.closeTicket(id, "Resolved");

        assertNotNull(result);
        assertEquals(TroubleTicket.TicketStatus.CLOSED, result.getStatus());
        assertEquals("Resolved", result.getResolutionNotes());
    }

    @Test
    void deleteTicket_Success() {
        UUID id = UUID.randomUUID();
        ticketService.deleteTicket(id);
        verify(ticketRepository).deleteById(id);
    }
}
