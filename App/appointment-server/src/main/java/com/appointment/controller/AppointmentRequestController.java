package com.appointment.controller;

import com.appointment.dto.AppointmentRequestDTO;
import com.appointment.security.UserDetailsImpl;
import com.appointment.service.AppointmentRequestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/appointment-requests")
@PreAuthorize("hasRole('USER')")
public class AppointmentRequestController {

	@Autowired
	private AppointmentRequestService requestService;

	@PostMapping
	public ResponseEntity<AppointmentRequestDTO> createRequest(@RequestBody AppointmentRequestDTO requestDTO,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		AppointmentRequestDTO created = requestService.createRequest(requestDTO, userDetails.getUsername());
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}/respond")
	public ResponseEntity<AppointmentRequestDTO> respondToRequest(@PathVariable Long id, @RequestParam boolean accept,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(requestService.respondToRequest(id, accept, userDetails.getUsername()));
	}

	@GetMapping("/pending")
	public ResponseEntity<List<AppointmentRequestDTO>> getPendingRequests(
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(requestService.getPendingRequests(userDetails.getUsername()));
	}

	@GetMapping("/sent")
	public ResponseEntity<List<AppointmentRequestDTO>> getSentRequests(
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(requestService.getSentRequests(userDetails.getUsername()));
	}
}