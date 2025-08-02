package com.appointment.repository;

import com.appointment.model.AppointmentRequest;
import com.appointment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, Long> {
	List<AppointmentRequest> findByReceiverAndStatus(User receiver, AppointmentRequest.RequestStatus status);

	List<AppointmentRequest> findBySender(User sender);

	boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, AppointmentRequest.RequestStatus status);
}