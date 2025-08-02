package com.appointment.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.appointment.dto.JwtResponse;
import com.appointment.dto.LoginRequest;
import com.appointment.dto.SignupRequest;
import com.appointment.security.UserDetailsImpl;
import com.appointment.security.jwt.JwtUtils;
import com.appointment.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

			return ResponseEntity
					.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail()));

		} catch (Exception ex) {
			return ResponseEntity.status(401).body("Đăng nhập thất bại: Tên đăng nhập hoặc mật khẩu không đúng.");
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		try {
			userService.registerUser(signUpRequest);
			return ResponseEntity.ok("Đăng ký người dùng thành công!");
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body("Lỗi đăng ký: " + ex.getMessage());
		}
	}
}
