package com.appointment.service;

import com.appointment.dto.AppointmentDTO;
import com.appointment.dto.AppointmentStatsDTO;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.model.Appointment;
import com.appointment.model.AppointmentStatus;
import com.appointment.model.User;
import com.appointment.repository.AppointmentRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
	@Autowired
	private AppointmentRepository appointmentRepository;

	public List<AppointmentDTO> getAllAppointmentsForUser(User user) {
		autoMarkMissedAppointments(user);
		return appointmentRepository.findByUser(user).stream().map(this::convertToDto).collect(Collectors.toList());
	}

	public AppointmentDTO getAppointmentById(Long id, User user) {
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		if (!appointment.getUser().getId().equals(user.getId())) {
			throw new ResourceNotFoundException("Appointment not found for this user");
		}

		return convertToDto(appointment);
	}

	public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO, User user) {
		Appointment appointment = new Appointment();
		appointment.setTitle(appointmentDTO.getTitle());
		appointment.setDescription(appointmentDTO.getDescription());
		appointment.setStartTime(appointmentDTO.getStartTime());
		appointment.setEndTime(appointmentDTO.getEndTime());
		appointment.setLocation(appointmentDTO.getLocation());
		appointment.setUser(user);
		appointment.setStatus(AppointmentStatus.SCHEDULED);

		Appointment savedAppointment = appointmentRepository.save(appointment);
		return convertToDto(savedAppointment);
	}

	public AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO, User user) {
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		if (!appointment.getUser().getId().equals(user.getId())) {
			throw new ResourceNotFoundException("Appointment not found for this user");
		}

		appointment.setTitle(appointmentDTO.getTitle());
		appointment.setDescription(appointmentDTO.getDescription());
		appointment.setStartTime(appointmentDTO.getStartTime());
		appointment.setEndTime(appointmentDTO.getEndTime());
		appointment.setLocation(appointmentDTO.getLocation());

		Appointment updatedAppointment = appointmentRepository.save(appointment);
		return convertToDto(updatedAppointment);
	}

	public void deleteAppointment(Long id, User user) {
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		if (!appointment.getUser().getId().equals(user.getId())) {
			throw new ResourceNotFoundException("Appointment not found for this user");
		}

		appointmentRepository.delete(appointment);
	}

	public List<AppointmentDTO> searchAppointmentsByTitle(User user, String title) {
		autoMarkMissedAppointments(user);
		return appointmentRepository.findByUserAndTitleContainingIgnoreCase(user, title).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	public List<AppointmentDTO> searchAppointmentsByDate(User user, LocalDateTime date) {
		autoMarkMissedAppointments(user);
		LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

		return appointmentRepository.findByUserAndStartTimeBetween(user, startOfDay, endOfDay).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	public List<AppointmentDTO> getUpcomingAppointmentsForReminder(User user) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime soon = now.plusMinutes(30);

		return appointmentRepository.findByUserAndStartTimeAfterAndReminderSentFalse(user, now).stream()
				.filter(app -> app.getStartTime().isBefore(soon)).map(this::convertToDto).collect(Collectors.toList());
	}

	public void markAsReminded(Long appointmentId) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		appointment.setReminderSent(true);
		appointmentRepository.save(appointment);
	}

	@Transactional
	public void markAppointmentAsMissed(Long id, User user) {
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		if (!appointment.getUser().getId().equals(user.getId())) {
			throw new ResourceNotFoundException("Appointment not found for this user");
		}

		appointment.setStatus(AppointmentStatus.MISSED);
		appointmentRepository.save(appointment);
	}

	@Transactional
	public void markAppointmentAsCompleted(Long id, User user) {
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		if (!appointment.getUser().getId().equals(user.getId())) {
			throw new ResourceNotFoundException("Appointment not found for this user");
		}

		appointment.setStatus(AppointmentStatus.COMPLETED);
		appointmentRepository.save(appointment);
	}

	@Transactional
	public AppointmentDTO rescheduleAppointment(Long id, LocalDateTime newStartTime, LocalDateTime newEndTime,
			User user) {
		Appointment appointment = appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		if (!appointment.getUser().getId().equals(user.getId())) {
			throw new ResourceNotFoundException("Appointment not found for this user");
		}

		appointment.setStartTime(newStartTime);
		appointment.setEndTime(newEndTime);
		appointment.setStatus(AppointmentStatus.SCHEDULED);
		appointment.setReminderSent(false);

		Appointment updatedAppointment = appointmentRepository.save(appointment);
		return convertToDto(updatedAppointment);
	}

	@Transactional
	public List<String> getAllLocations(User user) {
		return appointmentRepository.findByUser(user).stream().map(Appointment::getLocation).filter(Objects::nonNull)
				.distinct().collect(Collectors.toList());
	}

	@Transactional
	public List<AppointmentDTO> searchAppointmentsByLocation(User user, String location) {
		autoMarkMissedAppointments(user);
		return appointmentRepository.findByUserAndLocationContainingIgnoreCase(user, location).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}

	@Transactional
	public AppointmentStatsDTO getAppointmentStats(User user) {
		autoMarkMissedAppointments(user);
		List<Appointment> appointments = appointmentRepository.findByUser(user);

		AppointmentStatsDTO stats = new AppointmentStatsDTO();
		stats.setTotalAppointments(appointments.size());
		stats.setCompletedAppointments(
				appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count());
		stats.setMissedAppointments(
				appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.MISSED).count());
		stats.setCancelledAppointments(
				appointments.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count());

		return stats;
	}

	private AppointmentDTO convertToDto(Appointment appointment) {
		AppointmentDTO dto = new AppointmentDTO();
		dto.setId(appointment.getId());
		dto.setTitle(appointment.getTitle());
		dto.setDescription(appointment.getDescription());
		dto.setStartTime(appointment.getStartTime());
		dto.setEndTime(appointment.getEndTime());
		dto.setLocation(appointment.getLocation());
		dto.setStatus(appointment.getStatus().name());
		return dto;
	}

	private void autoMarkMissedAppointments(User user) {
		LocalDateTime now = LocalDateTime.now();
		List<Appointment> appointments = appointmentRepository.findByUser(user);
		for (Appointment appointment : appointments) {
			if (appointment.getStatus() == AppointmentStatus.SCHEDULED && appointment.getEndTime().isBefore(now)) {
				appointment.setStatus(AppointmentStatus.MISSED);
				appointmentRepository.save(appointment);
			}
		}
	}

	public List<AppointmentDTO> getMissedAppointments(User user) {
		return appointmentRepository.findByUserAndStatus(user, AppointmentStatus.MISSED).stream()
				.map(this::convertToDto).collect(Collectors.toList());
	}
}
