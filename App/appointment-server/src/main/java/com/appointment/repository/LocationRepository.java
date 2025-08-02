package com.appointment.repository;

import com.appointment.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
	Optional<Location> findByName(String name);

	boolean existsByName(String name);
}