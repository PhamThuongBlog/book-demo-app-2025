package com.appointment.controller;

import com.appointment.dto.LocationDTO;
import com.appointment.model.Location;
import com.appointment.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/locations")
public class LocationController {

	@Autowired
	private LocationService locationService;

	@GetMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<List<String>> getLocations() {
		return ResponseEntity.ok(locationService.getAllLocations());
	}

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Location> addLocation(@RequestBody LocationDTO locationDTO) {
		Location created = locationService.addLocation(locationDTO.getName());
		return ResponseEntity.status(201).body(created);
	}

	@PutMapping("/{oldName}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> updateLocation(@PathVariable String oldName, @RequestBody LocationDTO dto) {
		return locationService.updateLocation(oldName, dto.getName())
				? ResponseEntity.ok("Cập nhật địa điểm thành công")
				: ResponseEntity.badRequest().body("Không tìm thấy địa điểm cần cập nhật");
	}

	@DeleteMapping("/{name}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> deleteLocation(@PathVariable String name) {
		locationService.deleteLocation(name);
		return ResponseEntity.noContent().build();
	}
}