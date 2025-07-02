package com.cts.SupportTicket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketRequestDTO {
	private int userId;
	private String issue;
	private String remarks;
	private TicketCategory ticketCategory;

	public enum TicketCategory {
		HOTEL, FLIGHT, PACKAGE, OTHER
	}

}
