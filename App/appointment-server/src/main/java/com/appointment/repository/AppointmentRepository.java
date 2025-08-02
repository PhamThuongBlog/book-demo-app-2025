package com.appointment.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.appointment.model.Appointment;
import com.appointment.model.AppointmentStatus;
import com.appointment.model.User;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
	List<Appointment> findByUser(User user);

	List<Appointment> findByUserAndTitleContainingIgnoreCase(User user, String title);

	List<Appointment> findByUserAndStartTimeBetween(User user, LocalDateTime start, LocalDateTime end);

	List<Appointment> findByUserAndStartTimeAfterAndReminderSentFalse(User user, LocalDateTime now);

	List<Appointment> findByUserAndStatus(User user, AppointmentStatus status);

	List<Appointment> findByUserAndLocationContainingIgnoreCase(User user, String location);
}