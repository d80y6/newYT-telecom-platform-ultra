package com.yemenptc.bss.coreservice.repository;

import com.yemenptc.bss.coreservice.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
    Optional<Appointment> findByAppointmentId(String appointmentId);
    
    List<Appointment> findByCustomerId(UUID customerId);
    
    List<Appointment> findByTechnicianId(UUID technicianId);
    
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    
    List<Appointment> findByScheduledAtBetween(Instant start, Instant end);
    
    long countByStatus(Appointment.AppointmentStatus status);
}
