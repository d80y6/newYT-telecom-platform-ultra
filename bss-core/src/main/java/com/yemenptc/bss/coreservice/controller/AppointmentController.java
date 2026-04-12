package com.yemenptc.bss.coreservice.controller;

import com.yemenptc.bss.coreservice.entity.Appointment;
import com.yemenptc.bss.coreservice.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tmf-api/appointmentManagement/v5")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "TMF646 Appointment Management API")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/appointment")
    @Operation(summary = "Create appointment", description = "Creates a new appointment")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment request) {
        Appointment appointment = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @GetMapping("/appointment")
    @Operation(summary = "List appointments", description = "Retrieves all appointments with pagination")
    public ResponseEntity<Page<Appointment>> listAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Appointment> appointments = appointmentService.listAppointments(PageRequest.of(page, size));
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/appointment/{id}")
    @Operation(summary = "Get appointment", description = "Retrieves an appointment by ID")
    public ResponseEntity<Appointment> getAppointment(@PathVariable UUID id) {
        Appointment appointment = appointmentService.getAppointment(id);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/appointment/{id}")
    @Operation(summary = "Update appointment", description = "Updates an existing appointment")
    public ResponseEntity<Appointment> updateAppointment(
            @PathVariable UUID id, 
            @RequestBody Appointment request) {
        Appointment appointment = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(appointment);
    }

    @DeleteMapping("/appointment/{id}")
    @Operation(summary = "Delete appointment", description = "Deletes an appointment")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/appointment/{id}/assign")
    @Operation(summary = "Assign technician", description = "Assigns a technician to an appointment")
    public ResponseEntity<Appointment> assignTechnician(
            @PathVariable UUID id, 
            @RequestBody UUID technicianId) {
        Appointment appointment = appointmentService.assignTechnician(id, technicianId);
        return ResponseEntity.ok(appointment);
    }

    @PatchMapping("/appointment/{id}/start")
    @Operation(summary = "Start appointment", description = "Starts an appointment")
    public ResponseEntity<Appointment> startAppointment(@PathVariable UUID id) {
        Appointment appointment = appointmentService.startAppointment(id);
        return ResponseEntity.ok(appointment);
    }

    @PatchMapping("/appointment/{id}/complete")
    @Operation(summary = "Complete appointment", description = "Completes an appointment")
    public ResponseEntity<Appointment> completeAppointment(@PathVariable UUID id) {
        Appointment appointment = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(appointment);
    }

    @PatchMapping("/appointment/{id}/cancel")
    @Operation(summary = "Cancel appointment", description = "Cancels an appointment")
    public ResponseEntity<Appointment> cancelAppointment(@PathVariable UUID id) {
        Appointment appointment = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/appointment/customer/{customerId}")
    @Operation(summary = "Get appointments by customer", description = "Retrieves appointments for a customer")
    public ResponseEntity<List<Appointment>> getAppointmentsByCustomer(@PathVariable UUID customerId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(customerId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/appointment/technician/{technicianId}")
    @Operation(summary = "Get appointments by technician", description = "Retrieves appointments for a technician")
    public ResponseEntity<List<Appointment>> getAppointmentsByTechnician(@PathVariable UUID technicianId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByTechnician(technicianId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/appointment/status/{status}")
    @Operation(summary = "Get appointments by status", description = "Retrieves appointments by status")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable Appointment.AppointmentStatus status) {
        List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/appointment/range")
    @Operation(summary = "Get appointments in time range", description = "Retrieves appointments in a time range")
    public ResponseEntity<List<Appointment>> getAppointmentsInTimeRange(
            @RequestParam Instant start,
            @RequestParam Instant end) {
        List<Appointment> appointments = appointmentService.getAppointmentsInTimeRange(start, end);
        return ResponseEntity.ok(appointments);
    }
}
