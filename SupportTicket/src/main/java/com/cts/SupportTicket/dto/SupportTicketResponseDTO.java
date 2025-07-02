package com.cts.SupportTicket.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketResponseDTO {
    private int ticketId;
    private int userId;
    private TicketStatus status;
    private TicketCategory ticketCategory;
    private String issue;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum TicketCategory {
        HOTEL,
        FLIGHT,
        PACKAGE,
        OTHER
    }

    public enum TicketStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED,
    }


}

