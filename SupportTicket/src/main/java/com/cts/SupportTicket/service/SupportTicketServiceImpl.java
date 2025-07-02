package com.cts.SupportTicket.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.SupportTicket.dto.SupportTicketRequestDTO;
import com.cts.SupportTicket.dto.SupportTicketResponseDTO;
import com.cts.SupportTicket.entity.SupportTicket;
import com.cts.SupportTicket.exception.EntityNotFoundException;
import com.cts.SupportTicket.repository.SupportTicketRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {
	private static final Logger logger = LoggerFactory.getLogger(SupportTicketServiceImpl.class);
	private final SupportTicketRepository repository;

	public SupportTicketServiceImpl(SupportTicketRepository repository) {
		this.repository = repository;
	}

	@Override
	public SupportTicketResponseDTO createTicket(SupportTicketRequestDTO dto) {
		logger.info("Creating new support ticket for userId: {}", dto.getUserId());
		SupportTicket ticket = new SupportTicket();
		ticket.setUserId(dto.getUserId());
		ticket.setIssue(dto.getIssue());
		ticket.setStatus(SupportTicket.TicketStatus.OPEN);
		ticket.setTicketCategory(SupportTicket.TicketCategory.valueOf(dto.getTicketCategory().name()));
		ticket.setCreatedAt(LocalDateTime.now());
		ticket.setUpdatedAt(null);
		SupportTicket savedTicket = repository.save(ticket);
		logger.debug("Support ticket created with id: {}", savedTicket.getTicketId());
		return toResponseDTO(savedTicket);
	}

	@Override
	public List<SupportTicketResponseDTO> getAllTickets() {
		logger.info("Fetching all support tickets");
		return repository.findAll().stream().map(this::toResponseDTO).toList();
	}

	@Override
	public SupportTicketResponseDTO getTicketById(int id) throws EntityNotFoundException {
		logger.info("Fetching support ticket by id: {}", id);
		return toResponseDTO(repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + id)));
	}

	@Override
	public List<SupportTicketResponseDTO> getTicketsByUserId(int userId) throws EntityNotFoundException {
		logger.info("Fetching tickets for userId: {}", userId);
		List<SupportTicket> tickets = repository.findByUserId(userId);
		if (tickets.isEmpty()) {
			throw new EntityNotFoundException("No Tickets Found for User with ID: " + userId);
		}
		return repository.findByUserId(userId).stream().map(this::toResponseDTO).toList();
	}


	@Override
	public List<SupportTicketResponseDTO> getTicketsByStatus(String status) {
		logger.info("Fetching tickets by status: {}", status);
		SupportTicket.TicketStatus ticketStatus = SupportTicket.TicketStatus.valueOf(status.toUpperCase());
		return repository.findByStatus(ticketStatus).stream().map(this::toResponseDTO).toList();
	}

	@Override
	public SupportTicketResponseDTO updateTicketStatusAndRemark(int ticketId, SupportTicket.TicketStatus status,
																String remarks) throws EntityNotFoundException {
		logger.info("Updating ticket id: {} with status: {} with remarks: {}", ticketId, status, remarks);
		SupportTicket ticket = repository.findById(ticketId)
				.orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));
		ticket.setStatus(status);
		ticket.setUpdatedAt(LocalDateTime.now());
		ticket.setRemarks(remarks);
		return toResponseDTO(repository.save(ticket));
	}


	private SupportTicketResponseDTO toResponseDTO(SupportTicket ticket) {
		SupportTicketResponseDTO dto = new SupportTicketResponseDTO();
		dto.setTicketId(ticket.getTicketId());
		dto.setUserId(ticket.getUserId());
		dto.setIssue(ticket.getIssue());
		dto.setRemarks(ticket.getRemarks());
		dto.setTicketCategory(SupportTicketResponseDTO.TicketCategory.valueOf(ticket.getTicketCategory().name()));
		dto.setStatus(SupportTicketResponseDTO.TicketStatus.valueOf(ticket.getStatus().name()));
		dto.setCreatedAt(ticket.getCreatedAt());
		dto.setUpdatedAt(ticket.getUpdatedAt());
		return dto;
	}



}
