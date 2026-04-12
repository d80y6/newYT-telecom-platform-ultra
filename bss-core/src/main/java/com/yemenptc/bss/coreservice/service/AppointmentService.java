package com.yemenptc.bss.coreservice.service;

import com.yemenptc.bss.coreservice.entity.Appointment;
import com.yemenptc.bss.coreservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Transactional
    public Appointment createAppointment(Appointment request) {
        log.info("Creating appointment for customer: {}", request.getCustomerId());

        Appointment appointment = Appointment.builder()
            .appointmentId(request.getAppointmentId() != null ? 
                request.getAppointmentId() : "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .customerId(request.getCustomerId())
            .appointmentType(request.getAppointmentType())
            .description(request.getDescription())
            .scheduledAt(request.getScheduledAt())
            .durationMinutes(request.getDurationMinutes())
            .status(Appointment.AppointmentStatus.SCHEDULED)
            .location(request.getLocation())
            .technicianId(request.getTechnicianId())
            .build();

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment created: {}", saved.getAppointmentId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Appointment getAppointment(UUID id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Appointment> listAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByCustomer(UUID customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByTechnician(UUID technicianId) {
        return appointmentRepository.findByTechnicianId(technicianId);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsInTimeRange(Instant start, Instant end) {
        return appointmentRepository.findByScheduledAtBetween(start, end);
    }

    @Transactional
    public Appointment updateAppointment(UUID id, Appointment request) {
        Appointment appointment = getAppointment(id);
        if (request.getAppointmentType() != null) appointment.setAppointmentType(request.getAppointmentType());
        if (request.getDescription() != null) appointment.setDescription(request.getDescription());
        if (request.getScheduledAt() != null) appointment.setScheduledAt(request.getScheduledAt());
        if (request.getDurationMinutes() != null) appointment.setDurationMinutes(request.getDurationMinutes());
        if (request.getLocation() != null) appointment.setLocation(request.getLocation());
        if (request.getTechnicianId() != null) appointment.setTechnicianId(request.getTechnicianId());
        if (request.getNotes() != null) appointment.setNotes(request.getNotes());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment assignTechnician(UUID id, UUID technicianId) {
        Appointment appointment = getAppointment(id);
        appointment.setTechnicianId(technicianId);
        log.info("Technician assigned to appointment: {}", appointment.getAppointmentId());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment startAppointment(UUID id) {
        Appointment appointment = getAppointment(id);
        appointment.setStatus(Appointment.AppointmentStatus.IN_PROGRESS);
        log.info("Appointment started: {}", appointment.getAppointmentId());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment completeAppointment(UUID id) {
        Appointment appointment = getAppointment(id);
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setCompletedAt(Instant.now());
        log.info("Appointment completed: {}", appointment.getAppointmentId());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment cancelAppointment(UUID id) {
        Appointment appointment = getAppointment(id);
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        log.info("Appointment cancelled: {}", appointment.getAppointmentId());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteAppointment(UUID id) {
        Appointment appointment = getAppointment(id);
        appointmentRepository.delete(appointment);
        log.info("Appointment deleted: {}", appointment.getAppointmentId());
    }
}
