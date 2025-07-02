package com.cts.SupportTicket.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SupportTicket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ticketId;

	@Column(nullable = false)
	private int userId;

	@Column(nullable = false, length = 1000)
	private String issue;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TicketCategory ticketCategory;

	//@Column(nullable = false, length = 100)
	private String remarks;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TicketStatus status = TicketStatus.OPEN;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	private LocalDateTime updatedAt;

	public enum TicketStatus {
		OPEN,
		IN_PROGRESS,
		RESOLVED,
	}

	public enum TicketCategory {
		HOTEL,
		FLIGHT,
		PACKAGE,
		OTHER
	}


}
