package com.appointment.controller;

import com.appointment.dto.AppointmentDTO;
import com.appointment.dto.AppointmentStatsDTO;
import com.appointment.model.User;
import com.appointment.security.UserDetailsImpl;
import com.appointment.service.AppointmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/appointments")
@PreAuthorize("hasRole('USER')")
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;

	private User getUser(UserDetailsImpl userDetails) {
		return userDetails.getUser();
	}

	@GetMapping
	public ResponseEntity<List<AppointmentDTO>> getAllAppointments(
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(appointmentService.getAllAppointmentsForUser(getUser(userDetails)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(appointmentService.getAppointmentById(id, getUser(userDetails)));
	}

	@PostMapping
	public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody AppointmentDTO dto,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(appointmentService.createAppointment(dto, getUser(userDetails)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable Long id, @RequestBody AppointmentDTO dto,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(appointmentService.updateAppointment(id, dto, getUser(userDetails)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAppointment(@PathVariable Long id,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		appointmentService.deleteAppointment(id, getUser(userDetails));
		return ResponseEntity.ok().build();
	}

	@GetMapping("/search")
	public ResponseEntity<List<AppointmentDTO>> searchAppointments(@RequestParam(required = false) String title,
			@RequestParam(required = false) String date, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		User user = getUser(userDetails);
		if (title != null) {
			return ResponseEntity.ok(appointmentService.searchAppointmentsByTitle(user, title));
		} else if (date != null) {
			return ResponseEntity.ok(appointmentService.searchAppointmentsByDate(user, parseDate(date)));
		} else {
			return ResponseEntity.ok(appointmentService.getAllAppointmentsForUser(user));
		}
	}

	@GetMapping("/search/location")
	public ResponseEntity<List<AppointmentDTO>> searchByLocation(@RequestParam String location,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(appointmentService.searchAppointmentsByLocation(getUser(userDetails), location));
	}

	@GetMapping("/reminders")
	public ResponseEntity<List<AppointmentDTO>> getUpcomingAppointmentsForReminder(
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(appointmentService.getUpcomingAppointmentsForReminder(getUser(userDetails)));
	}

	@PostMapping("/{id}/reminded")
	public ResponseEntity<?> markAsReminded(@PathVariable Long id) {
		appointmentService.markAsReminded(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/missed")
	public ResponseEntity<?> markAsMissed(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		appointmentService.markAppointmentAsMissed(id, getUser(userDetails));
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/complete")
	public ResponseEntity<?> markAsCompleted(@PathVariable Long id,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		appointmentService.markAppointmentAsCompleted(id, getUser(userDetails));
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/reschedule")
	public ResponseEntity<AppointmentDTO> rescheduleAppointment(@PathVariable Long id,
			@RequestParam String newStartTime, @RequestParam String newEndTime,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, parseDate(newStartTime),
				parseDate(newEndTime), getUser(userDetails)));
	}

	@GetMapping("/stats")
	public ResponseEntity<AppointmentStatsDTO> getAppointmentStats(
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(appointmentService.getAppointmentStats(getUser(userDetails)));
	}

	@GetMapping("/missed")
	public ResponseEntity<List<AppointmentDTO>> getMissedAppointments(
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(appointmentService.getMissedAppointments(getUser(userDetails)));
	}

	private LocalDateTime parseDate(String date) {
		try {
			return LocalDateTime.parse(date);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd'T'HH:mm:ss");
		}
	}
}