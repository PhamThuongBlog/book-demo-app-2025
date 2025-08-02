package com.appointment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appointment.model.Location;
import com.appointment.repository.LocationRepository;

@Service
public class LocationService {

	@Autowired
	private LocationRepository locationRepository;

	public List<String> getAllLocations() {
		return locationRepository.findAll().stream().map(Location::getName).toList();
	}

	public Location addLocation(String name) {
		if (locationRepository.existsByName(name)) {
			throw new IllegalArgumentException("Địa điểm đã tồn tại");
		}
		return locationRepository.save(new Location(name));
	}

	public void deleteLocation(String name) {
		Location location = locationRepository.findByName(name)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa điểm"));
		locationRepository.delete(location);
	}

	public boolean updateLocation(String oldName, String newName) {
		Optional<Location> locationOpt = locationRepository.findByName(oldName);
		if (locationOpt.isEmpty())
			return false;

		Location location = locationOpt.get();
		location.setName(newName);
		locationRepository.save(location);
		return true;
	}
}