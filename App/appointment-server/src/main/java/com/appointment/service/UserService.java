package com.appointment.service;

import com.appointment.dto.SignupRequest;
import com.appointment.exception.UserAlreadyExistsException;
import com.appointment.model.User;
import com.appointment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User registerUser(SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			throw new UserAlreadyExistsException("Username is already taken!");
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new UserAlreadyExistsException("Email is already in use!");
		}

		if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
			throw new IllegalArgumentException("Passwords do not match");
		}

		String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encodedPassword);
		return userRepository.save(user);
	}
}