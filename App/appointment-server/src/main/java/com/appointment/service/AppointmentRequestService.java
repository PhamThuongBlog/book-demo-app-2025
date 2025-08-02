package com.appointment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appointment.dto.AppointmentDTO;
import com.appointment.dto.AppointmentRequestDTO;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.exception.UserNotFoundException;
import com.appointment.model.AppointmentRequest;
import com.appointment.model.User;
import com.appointment.repository.AppointmentRequestRepository;
import com.appointment.repository.UserRepository;

@Service
public class AppointmentRequestService {
	@Autowired
	private AppointmentRequestRepository requestRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private NotificationService notificationService;

	@Transactional
	public AppointmentRequestDTO createRequest(AppointmentRequestDTO requestDTO, String senderUsername) {
		User sender = getUserByUsernameOrThrow(senderUsername);
		User receiver = getUserByUsernameOrThrow(requestDTO.getReceiverUsername());

		if (requestRepository.existsBySenderAndReceiverAndStatus(sender, receiver,
				AppointmentRequest.RequestStatus.PENDING)) {
			throw new IllegalArgumentException("There is already a pending request between these users");
		}

		AppointmentRequest request = new AppointmentRequest();
		request.setSender(sender);
		request.setReceiver(receiver);
		request.setTitle(requestDTO.getTitle());
		request.setDescription(requestDTO.getDescription());
		request.setStartTime(requestDTO.getStartTime());
		request.setEndTime(requestDTO.getEndTime());
		request.setLocation(requestDTO.getLocation());
		request.setStatus(AppointmentRequest.RequestStatus.PENDING);

		AppointmentRequest savedRequest = requestRepository.save(request);
		notificationService.notifyNewAppointmentRequest(receiver, sender, savedRequest);
		return convertToDTO(savedRequest);
	}

	@Transactional
	public AppointmentRequestDTO respondToRequest(Long requestId, boolean accept, String responderUsername) {
		AppointmentRequest request = requestRepository.findById(requestId)
				.orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

		if (!request.getReceiver().getUsername().equals(responderUsername)) {
			throw new IllegalArgumentException("Only the receiver can respond to this request");
		}

		if (request.getStatus() != AppointmentRequest.RequestStatus.PENDING) {
			throw new IllegalArgumentException("Request has already been processed");
		}

		if (accept) {
			request.setStatus(AppointmentRequest.RequestStatus.ACCEPTED);
			AppointmentDTO appointmentDTO = buildAppointmentDTO(request);
			appointmentService.createAppointment(appointmentDTO, request.getSender());
			appointmentService.createAppointment(appointmentDTO, request.getReceiver());
			notificationService.notifyAppointmentRequestAccepted(request.getSender(), request.getReceiver());
		} else {
			request.setStatus(AppointmentRequest.RequestStatus.REJECTED);
			notificationService.notifyAppointmentRequestRejected(request.getSender(), request.getReceiver());
		}

		AppointmentRequest updatedRequest = requestRepository.save(request);
		return convertToDTO(updatedRequest);
	}

	public List<AppointmentRequestDTO> getPendingRequests(String username) {
		User user = getUserByUsernameOrThrow(username);
		return requestRepository.findByReceiverAndStatus(user, AppointmentRequest.RequestStatus.PENDING).stream()
				.map(this::convertToDTO).collect(Collectors.toList());
	}

	public List<AppointmentRequestDTO> getSentRequests(String username) {
		User user = getUserByUsernameOrThrow(username);
		return requestRepository.findBySender(user).stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private User getUserByUsernameOrThrow(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
	}

	private AppointmentDTO buildAppointmentDTO(AppointmentRequest request) {
		AppointmentDTO dto = new AppointmentDTO();
		dto.setTitle(request.getTitle());
		dto.setDescription(request.getDescription());
		dto.setStartTime(request.getStartTime());
		dto.setEndTime(request.getEndTime());
		dto.setLocation(request.getLocation());
		return dto;
	}

	private AppointmentRequestDTO convertToDTO(AppointmentRequest request) {
		AppointmentRequestDTO dto = new AppointmentRequestDTO();
		dto.setId(request.getId());
		dto.setSenderUsername(request.getSender().getUsername());
		dto.setReceiverUsername(request.getReceiver().getUsername());
		dto.setTitle(request.getTitle());
		dto.setDescription(request.getDescription());
		dto.setStartTime(request.getStartTime());
		dto.setEndTime(request.getEndTime());
		dto.setLocation(request.getLocation());
		dto.setStatus(request.getStatus().name());
		return dto;
	}
}