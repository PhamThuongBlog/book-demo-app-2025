package com.appointment.service;

import com.appointment.model.AppointmentRequest;
import com.appointment.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	public void notifyNewAppointmentRequest(User receiver, User sender, AppointmentRequest request) {
		logger.info("New appointment request from {} to {} at {}", sender.getUsername(), receiver.getUsername(),
				request.getStartTime());
	}

	public void notifyAppointmentRequestAccepted(User sender, User receiver) {
		logger.info("Appointment request from {} accepted by {}", sender.getUsername(), receiver.getUsername());
	}

	public void notifyAppointmentRequestRejected(User sender, User receiver) {
		logger.info("Appointment request from {} rejected by {}", sender.getUsername(), receiver.getUsername());
	}

	public void notifyUpcomingAppointment(User user, String appointmentTitle, LocalDateTime startTime) {
		logger.info("Upcoming appointment for {}: {} at {}", user.getUsername(), appointmentTitle, startTime);
	}
}