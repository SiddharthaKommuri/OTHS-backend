package com.cts.SupportTicket.dto;

import com.cts.SupportTicket.entity.SupportTicket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequestDTO {
	private SupportTicket.TicketStatus status;
	private String remarks;

}
