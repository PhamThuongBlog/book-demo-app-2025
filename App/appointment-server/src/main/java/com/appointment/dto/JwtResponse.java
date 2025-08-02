package com.appointment.dto;

public record JwtResponse(String token, String type, Long id, String username, String email) {
	public JwtResponse(String token, Long id, String username, String email) {
		this(token, "Bearer", id, username, email);
	}
}