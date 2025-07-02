package com.cts.SupportTicket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections; // Import Collections for emptyList
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cts.SupportTicket.dto.SupportTicketRequestDTO;
import com.cts.SupportTicket.dto.SupportTicketResponseDTO;
// Import the entity's TicketStatus and TicketCategory for consistent type usage
import com.cts.SupportTicket.entity.SupportTicket;
import com.cts.SupportTicket.entity.SupportTicket.TicketCategory;
import com.cts.SupportTicket.entity.SupportTicket.TicketStatus;
import com.cts.SupportTicket.exception.EntityNotFoundException;
import com.cts.SupportTicket.repository.SupportTicketRepository;
import com.cts.SupportTicket.service.SupportTicketServiceImpl;

class SupportTicketServiceImplTest {

	@Mock
	private SupportTicketRepository repository;

	@InjectMocks
	private SupportTicketServiceImpl service; // Injects mock repository into the service instance

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // Initializes mocks before each test
	}


	private SupportTicket createSampleTicket(int id, int userId, TicketStatus status, TicketCategory category, String issue, String remarks) {
		SupportTicket ticket = new SupportTicket();
		ticket.setTicketId(id);
		ticket.setUserId(userId);
		ticket.setIssue(issue);
		ticket.setStatus(status);
		ticket.setTicketCategory(category);
		ticket.setRemarks(remarks);
		ticket.setCreatedAt(LocalDateTime.now());
		// Set updated at to null or a specific time if needed for tests
		ticket.setUpdatedAt(null);
		return ticket;
	}

	@Test
	void testCreateTicket_Success() {
		SupportTicketRequestDTO requestDTO = new SupportTicketRequestDTO();
		requestDTO.setUserId(123);
		requestDTO.setIssue("Unable to login");
		requestDTO.setTicketCategory(SupportTicketRequestDTO.TicketCategory.FLIGHT);

		// Entity that would be saved by the repository
		SupportTicket savedTicket = new SupportTicket();
		savedTicket.setTicketId(1);
		savedTicket.setUserId(123);
		savedTicket.setIssue("Unable to login");
		savedTicket.setStatus(TicketStatus.OPEN); // Entity uses its own TicketStatus
		savedTicket.setTicketCategory(TicketCategory.HOTEL); // Entity uses its own TicketCategory
		savedTicket.setCreatedAt(LocalDateTime.now());
		savedTicket.setUpdatedAt(null); // Newly created tickets don't have update time

		when(repository.save(any(SupportTicket.class))).thenReturn(savedTicket);

		SupportTicketResponseDTO responseDTO = service.createTicket(requestDTO);

		assertNotNull(responseDTO);
		assertEquals(1, responseDTO.getTicketId());
		assertEquals("Unable to login", responseDTO.getIssue());
		assertEquals(123, responseDTO.getUserId());
		// Assert DTO's status, which comes from mapping
		assertEquals(SupportTicketResponseDTO.TicketStatus.OPEN, responseDTO.getStatus());
		assertEquals(SupportTicketResponseDTO.TicketCategory.HOTEL, responseDTO.getTicketCategory());

		// Verify that save was called exactly once with any SupportTicket object
		verify(repository, times(1)).save(any(SupportTicket.class));
	}

	@Test
	void testGetAllTickets_Success() {
		List<SupportTicket> tickets = Arrays.asList(
				createSampleTicket(1, 100, TicketStatus.OPEN, TicketCategory.FLIGHT, "Issue 1", null),
				createSampleTicket(2, 101, TicketStatus.IN_PROGRESS, TicketCategory.HOTEL, "Issue 2", null)
		);

		when(repository.findAll()).thenReturn(tickets);

		List<SupportTicketResponseDTO> result = service.getAllTickets();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(1, result.get(0).getTicketId());
		assertEquals(101, result.get(1).getUserId());

		verify(repository, times(1)).findAll();
	}

	@Test
	void testGetAllTickets_NoTicketsFound() {
		when(repository.findAll()).thenReturn(Collections.emptyList());

		List<SupportTicketResponseDTO> result = service.getAllTickets();

		assertNotNull(result);
		assertTrue(result.isEmpty());
		assertEquals(0, result.size());

		verify(repository, times(1)).findAll();
	}

	@Test
	void testGetTicketById_Success() throws EntityNotFoundException {
		SupportTicket ticket = createSampleTicket(1, 100, TicketStatus.OPEN, TicketCategory.OTHER, "Billing issue", null);
		when(repository.findById(1)).thenReturn(Optional.of(ticket));

		SupportTicketResponseDTO result = service.getTicketById(1);

		assertNotNull(result);
		assertEquals(1, result.getTicketId());
		assertEquals(100, result.getUserId());
		assertEquals("Billing issue", result.getIssue());
		assertEquals(SupportTicketResponseDTO.TicketStatus.OPEN, result.getStatus());

		verify(repository, times(1)).findById(1);
	}

	@Test
	void testGetTicketById_NotFound() {
		when(repository.findById(999)).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			service.getTicketById(999);
		});

		assertEquals("Ticket not found with id: 999", exception.getMessage());
		verify(repository, times(1)).findById(999);
	}

	@Test
	void testGetTicketsByUserId_Success() throws EntityNotFoundException {
		int userId = 100;
		List<SupportTicket> tickets = Arrays.asList(
				createSampleTicket(1, userId, TicketStatus.OPEN, TicketCategory.FLIGHT, "Issue for User 100", null),
				createSampleTicket(2, userId, TicketStatus.RESOLVED, TicketCategory.OTHER, "Another issue for User 100", null)
		);

		// Mock repository to return the list when findByUserId is called
		when(repository.findByUserId(userId)).thenReturn(tickets);

		List<SupportTicketResponseDTO> result = service.getTicketsByUserId(userId);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(userId, result.get(0).getUserId());
		assertEquals(userId, result.get(1).getUserId());

		// Verify that findByUserId was called twice due to your service logic:
		// First for tickets.isEmpty() check, second for the stream.
		verify(repository, times(2)).findByUserId(userId);
	}

	@Test
	void testGetTicketsByUserId_NotFound() {
		int userId = 999;
		// Mock repository to return an empty list
		when(repository.findByUserId(userId)).thenReturn(Collections.emptyList());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			service.getTicketsByUserId(userId);
		});

		assertEquals("No Tickets Found for User with ID: " + userId, exception.getMessage());

		// Verify that findByUserId was called once for the isEmpty() check
		verify(repository, times(1)).findByUserId(userId);
	}

	@Test
	void testGetTicketsByStatus_Success() {
		String statusString = "OPEN";
		TicketStatus statusEnum = TicketStatus.OPEN; // Entity enum for status

		List<SupportTicket> tickets = Arrays.asList(
				createSampleTicket(1, 100, statusEnum, TicketCategory.PACKAGE, "Open Issue 1", null),
				createSampleTicket(3, 102, statusEnum, TicketCategory.FLIGHT, "Open Issue 2", null)
		);

		when(repository.findByStatus(statusEnum)).thenReturn(tickets);

		List<SupportTicketResponseDTO> result = service.getTicketsByStatus(statusString);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.stream().allMatch(t -> t.getStatus().name().equals(statusString)));

		verify(repository, times(1)).findByStatus(statusEnum);
	}

	@Test
	void testGetTicketsByStatus_NoTicketsFoundForStatus() {
		String statusString = "RESOLVED";
		TicketStatus statusEnum = TicketStatus.RESOLVED;

		when(repository.findByStatus(statusEnum)).thenReturn(Collections.emptyList());

		List<SupportTicketResponseDTO> result = service.getTicketsByStatus(statusString);

		assertNotNull(result);
		assertTrue(result.isEmpty());
		assertEquals(0, result.size());

		verify(repository, times(1)).findByStatus(statusEnum);
	}


	@Test
	void testGetTicketsByStatus_InvalidStatusString() {
		String invalidStatus = "NON_EXISTENT_STATUS";

		// Expect IllegalArgumentException because of valueOf() for invalid enum string
		assertThrows(IllegalArgumentException.class, () -> {
			service.getTicketsByStatus(invalidStatus);
		});

		// No interaction with repository if valueOf fails
		verify(repository, never()).findByStatus(any());
	}

	@Test
	void testUpdateTicketStatusAndAgent_Success() throws EntityNotFoundException {
		int ticketId = 1;
		TicketStatus newStatus = TicketStatus.RESOLVED; // Entity enum for status
		String remarks = "Customer confirmed resolution. Ticket closed.";

		// Original ticket before update
		SupportTicket originalTicket = createSampleTicket(ticketId, 100, TicketStatus.IN_PROGRESS, TicketCategory.OTHER, "Original Issue", null);
		when(repository.findById(ticketId)).thenReturn(Optional.of(originalTicket));

		// Mock save to return the modified ticket (important for unit testing behavior)
		when(repository.save(any(SupportTicket.class))).thenAnswer(invocation -> {
			SupportTicket savedTicket = invocation.getArgument(0);
			// Simulate the update happening in the repository's save method
			// This is crucial for verifying updated fields
			assertEquals(newStatus, savedTicket.getStatus());
			assertEquals(remarks, savedTicket.getRemarks());
			assertNotNull(savedTicket.getUpdatedAt()); // updated timestamp should be set
			return savedTicket;
		});

		SupportTicketResponseDTO result = service.updateTicketStatusAndRemark(ticketId, newStatus, remarks);

		assertNotNull(result);
		assertEquals(ticketId, result.getTicketId());
		// Assert DTO's status, which comes from mapping
		assertEquals(SupportTicketResponseDTO.TicketStatus.RESOLVED, result.getStatus());
		assertEquals(remarks, result.getRemarks());
		assertNotNull(result.getUpdatedAt()); // Updated timestamp should be propagated to DTO

		verify(repository, times(1)).findById(ticketId);
		verify(repository, times(1)).save(any(SupportTicket.class));
	}

	@Test
	void testUpdateTicketStatusAndAgent_TicketNotFound() {
		int nonExistentTicketId = 999;
		TicketStatus status = TicketStatus.RESOLVED;
		String remarks = "Not found ticket";

		when(repository.findById(nonExistentTicketId)).thenReturn(Optional.empty());

		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			service.updateTicketStatusAndRemark(nonExistentTicketId, status, remarks);
		});

		assertEquals("Ticket not found with id: " + nonExistentTicketId, exception.getMessage());
		verify(repository, times(1)).findById(nonExistentTicketId);
		// Verify save was NOT called
		verify(repository, never()).save(any(SupportTicket.class));
	}
}