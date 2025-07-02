package com.cts.SupportTicket.service;

import java.util.List;

import com.cts.SupportTicket.dto.SupportTicketRequestDTO;
import com.cts.SupportTicket.dto.SupportTicketResponseDTO;
import com.cts.SupportTicket.entity.SupportTicket;
import com.cts.SupportTicket.exception.EntityNotFoundException;

public interface SupportTicketService {
    SupportTicketResponseDTO createTicket(SupportTicketRequestDTO dto);

    List<SupportTicketResponseDTO> getAllTickets();

    SupportTicketResponseDTO getTicketById(int id) throws EntityNotFoundException;

    List<SupportTicketResponseDTO> getTicketsByUserId(int userId) throws EntityNotFoundException;

    List<SupportTicketResponseDTO> getTicketsByStatus(String status);

    SupportTicketResponseDTO updateTicketStatusAndRemark(int ticketId, SupportTicket.TicketStatus status,
                                                         String remarks) throws EntityNotFoundException;

}
