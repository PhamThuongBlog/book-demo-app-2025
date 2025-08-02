package com.appointment.dto;

public class AppointmentStatsDTO {
	private long totalAppointments;
	private long completedAppointments;
	private long missedAppointments;
	private long cancelledAppointments;

	public long getTotalAppointments() {
		return totalAppointments;
	}

	public void setTotalAppointments(long totalAppointments) {
		this.totalAppointments = totalAppointments;
	}

	public long getCompletedAppointments() {
		return completedAppointments;
	}

	public void setCompletedAppointments(long completedAppointments) {
		this.completedAppointments = completedAppointments;
	}

	public long getMissedAppointments() {
		return missedAppointments;
	}

	public void setMissedAppointments(long missedAppointments) {
		this.missedAppointments = missedAppointments;
	}

	public long getCancelledAppointments() {
		return cancelledAppointments;
	}

	public void setCancelledAppointments(long cancelledAppointments) {
		this.cancelledAppointments = cancelledAppointments;
	}
}