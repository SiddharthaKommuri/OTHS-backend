package com.cts.SupportTicket.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.SupportTicket.dto.SupportTicketRequestDTO;
import com.cts.SupportTicket.dto.SupportTicketResponseDTO;
import com.cts.SupportTicket.dto.UpdateTicketRequestDTO;
import com.cts.SupportTicket.exception.EntityNotFoundException;
import com.cts.SupportTicket.service.SupportTicketService;

@RestController
@RequestMapping("/api/support-tickets")
//@CrossOrigin
public class SupportTicketController {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketController.class);
    private final SupportTicketService service;

    public SupportTicketController(SupportTicketService service) {
        this.service = service;
    }

    // ===================== ADMIN METHODS =====================

    /**
     * Retrieves all support tickets.
     *
     * @return a list of all support tickets
     */
    @GetMapping
    public ResponseEntity<List<SupportTicketResponseDTO>> getAllTickets() {
        logger.info("Fetching all support tickets");
        List<SupportTicketResponseDTO> tickets = service.getAllTickets();
        logger.debug("Found {} tickets", tickets.size());
        return ResponseEntity.ok(tickets);
    }

    /**
     * Retrieves a support ticket by its ID.
     *
     * @param ticketId the ID of the ticket
     * @return the support ticket with the specified ID
     * @throws EntityNotFoundException if the ticket is not found
     */
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<SupportTicketResponseDTO> getTicketById(@PathVariable int ticketId)
            throws EntityNotFoundException {
        logger.info("Fetching ticket with ID={}", ticketId);
        SupportTicketResponseDTO ticket = service.getTicketById(ticketId);
        logger.debug("Fetched ticket: {}", ticket);
        return ResponseEntity.ok(ticket);
    }

    /**
     * Retrieves support tickets by their status.
     *
     * @param status the status of the tickets
     * @return a list of tickets with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SupportTicketResponseDTO>> getTicketsByStatus(
            @PathVariable SupportTicketResponseDTO.TicketStatus status) {
        logger.info("Fetching tickets with status={}", status);
        List<SupportTicketResponseDTO> tickets = service.getTicketsByStatus(status.name());
        logger.debug("Found {} tickets with status {}", tickets.size(), status);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Retrieves the total count of support tickets.
     *
     * @return the total number of tickets
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalTicketCount() {
        logger.info("Fetching total ticket count");
        long count = service.getAllTickets().size();
        logger.debug("Total ticket count: {}", count);
        return ResponseEntity.ok(count);
    }

    /**
     * Assigns an agent and status to a support ticket.
     *
     * @param ticketId the ID of the ticket
     * @param request  the request containing agent ID and status
     * @return the updated support ticket
     * @throws EntityNotFoundException if the ticket is not found
     */
    @PutMapping("/{ticketId}/update")
    public ResponseEntity<SupportTicketResponseDTO> updateStatus(@PathVariable int ticketId,
                                                                 @RequestBody UpdateTicketRequestDTO request) throws EntityNotFoundException {
        logger.info(" and status={} to ticketId={} remarks = {}",
                request.getStatus(), ticketId, request.getRemarks());
        SupportTicketResponseDTO updated = service.updateTicketStatusAndRemark(ticketId, request.getStatus(),
                request.getRemarks());
        logger.debug("Updated ticketId={} with new agent and status", ticketId);
        return ResponseEntity.ok(updated);
    }

    // ===================== USER METHODS =====================

    /**
     * Retrieves all tickets for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of tickets for the user
     * @throws EntityNotFoundException if the user is not found
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SupportTicketResponseDTO>> getUserTickets(@PathVariable int userId)
            throws EntityNotFoundException {
        logger.info("Fetching tickets for userId={}", userId);
        List<SupportTicketResponseDTO> tickets = service.getTicketsByUserId(userId);
        logger.debug("User {} has {} tickets", userId, tickets.size());
        return ResponseEntity.ok(tickets);
    }

    /**
     * Retrieves tickets for a user filtered by status.
     *
     * @param userId the ID of the user
     * @param status the status to filter by
     * @return a list of tickets for the user with the specified status
     * @throws EntityNotFoundException if the user is not found
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<SupportTicketResponseDTO>> getUserTicketsByStatus(@PathVariable int userId,
                                                                                 @PathVariable SupportTicketResponseDTO.TicketStatus status) throws EntityNotFoundException {
        logger.info("Fetching tickets for userId={} with status={}", userId, status);
        List<SupportTicketResponseDTO> filtered = service.getTicketsByUserId(userId).stream()
                .filter(t -> t.getStatus().equals(status)).toList();
        logger.debug("User {} has {} tickets with status {}", userId, filtered.size(), status);
        return ResponseEntity.ok(filtered);
    }

    /**
     * Creates a new support ticket.
     *
     * @param ticket the ticket creation request
     * @return the created support ticket
     */
    @PostMapping
    public ResponseEntity<SupportTicketResponseDTO> createTicket(@RequestBody SupportTicketRequestDTO ticket) {
        logger.info("Creating new support ticket for userId={}", ticket.getUserId());
        SupportTicketResponseDTO created = service.createTicket(ticket);
        logger.debug("Created ticket with id={}", created.getUserId());
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Retrieves the count of tickets for a specific user.
     *
     * @param userId the ID of the user
     * @return the number of tickets for the user
     * @throws EntityNotFoundException if the user is not found
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getUserTicketCount(@PathVariable int userId) throws EntityNotFoundException {
        logger.info("Fetching ticket count for userId={}", userId);
        long count = service.getTicketsByUserId(userId).size();
        logger.debug("User {} ticket count: {}", userId, count);
        return ResponseEntity.ok(count);
    }

//	// ===================== AGENT METHODS =====================
//
//	/**
//	 * Retrieves all tickets assigned to a specific agent.
//	 *
//	 * @param agentId the ID of the agent
//	 * @return a list of tickets assigned to the agent
//	 * @throws EntityNotFoundException if the agent is not found
//	 */
//	@GetMapping("/agent/{agentId}")
//	public ResponseEntity<List<SupportTicketResponseDTO>> getAgentTickets(@PathVariable int agentId)
//			throws EntityNotFoundException {
//		logger.info("Fetching tickets assigned to agentId={}", agentId);
//		List<SupportTicketResponseDTO> tickets = service.getTicketsByAssignedAgentId(agentId);
//		logger.debug("Agent {} has {} assigned tickets", agentId, tickets.size());
//		return ResponseEntity.ok(tickets);
//	}
//
//	/**
//	 * Retrieves tickets assigned to an agent filtered by status.
//	 *
//	 * @param agentId the ID of the agent
//	 * @param status  the status to filter by
//	 * @return a list of tickets for the agent with the specified status
//	 * @throws EntityNotFoundException if the agent is not found
//	 */
//	@GetMapping("/agent/{agentId}/status/{status}")
//	public ResponseEntity<List<SupportTicketResponseDTO>> getAgentTicketsByStatus(@PathVariable int agentId,
//			@PathVariable SupportTicketResponseDTO.TicketStatus status) throws EntityNotFoundException {
//		logger.info("Fetching tickets for agentId={} with status={}", agentId, status);
//		List<SupportTicketResponseDTO> filtered = service.getTicketsByAssignedAgentId(agentId).stream()
//				.filter(t -> t.getStatus().equals(status)).toList();
//		logger.debug("Agent {} has {} tickets with status {}", agentId, filtered.size(), status);
//		return ResponseEntity.ok(filtered);
//	}
//
//	/**
//	 * Retrieves the count of tickets assigned to a specific agent.
//	 *
//	 * @param agentId the ID of the agent
//	 * @return the number of tickets assigned to the agent
//	 * @throws EntityNotFoundException if the agent is not found
//	 */
//	@GetMapping("/agent/{agentId}/count")
//	public ResponseEntity<Long> getAgentTicketCount(@PathVariable int agentId) throws EntityNotFoundException {
//		logger.info("Fetching ticket count for agentId={}", agentId);
//		long count = service.getTicketsByAssignedAgentId(agentId).size();
//		logger.debug("Agent {} ticket count: {}", agentId, count);
//		return ResponseEntity.ok(count);
//	}
//
//	/**
//	 * Updates the status of a support ticket.
//	 *
//	 * @param ticketId the ID of the ticket
//	 * @param request  the request containing the new status
//	 * @return the updated support ticket
//	 * @throws EntityNotFoundException if the ticket is not found
//	 */
//	@PutMapping("/{ticketId}/status")
//	public ResponseEntity<SupportTicketResponseDTO> updateStatus(@PathVariable int ticketId,
//			@RequestBody UpdateStatusRequestDTO request) throws EntityNotFoundException {
//		logger.info("Updating status of ticketId={} to {}", ticketId, request.getStatus());
//		SupportTicketResponseDTO updated = service.updateTicketStatus(ticketId, request.getStatus(),
//				request.getRemarks());
//		logger.debug("Updated ticketId={} with new status", ticketId);
//		return ResponseEntity.ok(updated);
//	}


}
