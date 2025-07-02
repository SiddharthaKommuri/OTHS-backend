package com.cts.SupportTicket;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType; // Import MediaType
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.SupportTicket.controller.SupportTicketController;
import com.cts.SupportTicket.dto.SupportTicketRequestDTO;
import com.cts.SupportTicket.dto.SupportTicketResponseDTO;
import com.cts.SupportTicket.dto.SupportTicketResponseDTO.TicketStatus; // Correct import for TicketStatus
import com.cts.SupportTicket.dto.UpdateTicketRequestDTO; // New DTO for update
import com.cts.SupportTicket.entity.SupportTicket;
import com.cts.SupportTicket.exception.GlobalExceptionHandler;
import com.cts.SupportTicket.service.SupportTicketService;
import com.fasterxml.jackson.databind.ObjectMapper; // Import ObjectMapper

@WebMvcTest(SupportTicketController.class)
// @ContextConfiguration is usually not needed with @WebMvcTest unless you have specific bean configurations
// that aren't automatically picked up. Often @WebMvcTest is sufficient.
@ContextConfiguration(classes = {SupportTicketController.class})
@Import(GlobalExceptionHandler.class) // Import your global exception handler
class SupportTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // This provides a Mockito mock for SupportTicketService in the Spring context
    private SupportTicketService service;

    @Autowired
    private ObjectMapper objectMapper; // Autowire ObjectMapper for converting objects to JSON and vice-versa

    // ===================== ADMIN METHODS TESTS =====================

    @Test
    void testGetAllTickets_Success() throws Exception {
        // Prepare mock data
        SupportTicketResponseDTO ticket1 = new SupportTicketResponseDTO();
        ticket1.setTicketId(1);
        ticket1.setUserId(100);
        ticket1.setIssue("Login issue");
        ticket1.setStatus(TicketStatus.OPEN);
        ticket1.setCreatedAt(LocalDateTime.now()); // Set created at to avoid NullPointer if used in JSON path

        SupportTicketResponseDTO ticket2 = new SupportTicketResponseDTO();
        ticket2.setTicketId(2);
        ticket2.setUserId(101);
        ticket2.setIssue("Payment issue");
        ticket2.setStatus(TicketStatus.RESOLVED);
        ticket2.setCreatedAt(LocalDateTime.now());

        List<SupportTicketResponseDTO> tickets = Arrays.asList(ticket1, ticket2);

        // Mock the service call
        Mockito.when(service.getAllTickets()).thenReturn(tickets);

        // Perform the GET request and assert
        mockMvc.perform(get("/api/support-tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].ticketId", is(1)))
                .andExpect(jsonPath("$[0].issue", is("Login issue")))
                .andExpect(jsonPath("$[1].ticketId", is(2)))
                .andExpect(jsonPath("$[1].status", is("RESOLVED")));
    }

    @Test
    void testGetAllTickets_Success_EmptyList() throws Exception {
        // Prepare mock data for an empty list
        Mockito.when(service.getAllTickets()).thenReturn(Collections.emptyList());

        // Perform the GET request and assert
        mockMvc.perform(get("/api/support-tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)))
                .andExpect(content().json("[]")); // Ensure the response is an empty JSON array
    }



    @Test
    void testGetTicketById_Success() throws Exception {
        int ticketId = 1;
        SupportTicketResponseDTO ticket = new SupportTicketResponseDTO();
        ticket.setTicketId(ticketId);
        ticket.setUserId(100);
        ticket.setIssue("Network issue");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());

        // Mock the service call
        Mockito.when(service.getTicketById(ticketId)).thenReturn(ticket);

        // Perform the GET request and assert
        mockMvc.perform(get("/api/support-tickets/ticket/{ticketId}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId", is(ticketId)))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.issue", is("Network issue")));
    }




    @Test
    void testGetTicketsByStatus_Success_MultipleTickets() throws Exception {
        TicketStatus status = TicketStatus.IN_PROGRESS;
        SupportTicketResponseDTO ticket1 = new SupportTicketResponseDTO();
        ticket1.setTicketId(1);
        ticket1.setUserId(100);
        ticket1.setIssue("Login issue");
        ticket1.setStatus(status);
        ticket1.setCreatedAt(LocalDateTime.now());

        SupportTicketResponseDTO ticket2 = new SupportTicketResponseDTO();
        ticket2.setTicketId(3);
        ticket2.setUserId(102);
        ticket2.setIssue("DB connection issue");
        ticket2.setStatus(status);
        ticket2.setCreatedAt(LocalDateTime.now());

        // Mock service to return tickets for the given status
        Mockito.when(service.getTicketsByStatus(status.name())).thenReturn(Arrays.asList(ticket1, ticket2));

        // Perform GET request and assert
        mockMvc.perform(get("/api/support-tickets/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].status", is(status.name())))
                .andExpect(jsonPath("$[1].status", is(status.name())));
    }

    @Test
    void testGetTicketsByStatus_Success_NoTicketsForStatus() throws Exception {
        TicketStatus status = TicketStatus.RESOLVED;
        // Mock service to return an empty list for the given status
        Mockito.when(service.getTicketsByStatus(status.name())).thenReturn(Collections.emptyList());

        // Perform GET request and assert
        mockMvc.perform(get("/api/support-tickets/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)))
                .andExpect(content().json("[]"));
    }




    @Test
    void testGetTotalTicketCount_Success() throws Exception {
        // Prepare mock data (tickets used only to determine size)
        List<SupportTicketResponseDTO> tickets = Arrays.asList(
                new SupportTicketResponseDTO(), new SupportTicketResponseDTO(), new SupportTicketResponseDTO()
        );
        Mockito.when(service.getAllTickets()).thenReturn(tickets);

        // Perform GET request and assert
        mockMvc.perform(get("/api/support-tickets/count"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("3"))); // Expecting the count as a string
    }

    @Test
    void testGetTotalTicketCount_Success_ZeroTickets() throws Exception {
        Mockito.when(service.getAllTickets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/support-tickets/count"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("0")));
    }



    @Test
    void testAssignAgentAndStatus_Success() throws Exception {
        int ticketId = 1;
        UpdateTicketRequestDTO updateRequest = new UpdateTicketRequestDTO();
        updateRequest.setStatus(SupportTicket.TicketStatus.RESOLVED);
        updateRequest.setRemarks("Issue resolved by Agent A.");
        // If your UpdateTicketRequestDTO includes agentId, set it here: updateRequest.setAgentId(5);

        SupportTicketResponseDTO updatedTicket = new SupportTicketResponseDTO();
        updatedTicket.setTicketId(ticketId);
        updatedTicket.setUserId(100);
        updatedTicket.setIssue("Login issue");
        updatedTicket.setStatus(TicketStatus.RESOLVED);
        updatedTicket.setRemarks("Issue resolved by Agent A.");
        updatedTicket.setCreatedAt(LocalDateTime.now()); // Set created at to avoid NullPointer
        updatedTicket.setUpdatedAt(LocalDateTime.now()); // Assuming you have an updatedAt field

        // Mock the service call for updateTicketStatusAndAgent
        Mockito.when(service.updateTicketStatusAndRemark(
                        eq(ticketId),
                        eq(SupportTicket.TicketStatus.RESOLVED), // Use SupportTicketResponseDTO.TicketStatus
                        eq("Issue resolved by Agent A."))
                )
                .thenReturn(updatedTicket);

        // Perform PUT request to the correct endpoint
        mockMvc.perform(put("/api/support-tickets/{ticketId}/assign", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))) // Convert DTO to JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId", is(ticketId)))
                .andExpect(jsonPath("$.status", is("RESOLVED")))
                .andExpect(jsonPath("$.remarks", is("Issue resolved by Agent A.")));
    }







    // ===================== USER METHODS TESTS =====================

    @Test
    void testGetUserTickets_Success() throws Exception {
        int userId = 100;
        SupportTicketResponseDTO ticket1 = new SupportTicketResponseDTO();
        ticket1.setTicketId(1);
        ticket1.setUserId(userId);
        ticket1.setIssue("Login issue");
        ticket1.setStatus(TicketStatus.IN_PROGRESS);
        ticket1.setCreatedAt(LocalDateTime.now());

        SupportTicketResponseDTO ticket2 = new SupportTicketResponseDTO();
        ticket2.setTicketId(2);
        ticket2.setUserId(userId);
        ticket2.setIssue("Password reset");
        ticket2.setStatus(TicketStatus.OPEN);
        ticket2.setCreatedAt(LocalDateTime.now());

        // Mock service call
        Mockito.when(service.getTicketsByUserId(userId)).thenReturn(Arrays.asList(ticket1, ticket2));

        // Perform GET request and assert
        mockMvc.perform(get("/api/support-tickets/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].userId", is(userId)))
                .andExpect(jsonPath("$[1].userId", is(userId)));
    }

    @Test
    void testGetUserTickets_Success_NoTicketsForUser() throws Exception {
        int userId = 999;
        // Mock service to return an empty list for the user
        Mockito.when(service.getTicketsByUserId(userId)).thenReturn(Collections.emptyList());

        // Perform GET request and assert
        mockMvc.perform(get("/api/support-tickets/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)))
                .andExpect(content().json("[]"));
    }



    @Test
    void testGetUserTicketsByStatus_Success() throws Exception {
        int userId = 100;
        TicketStatus status = TicketStatus.IN_PROGRESS;

        SupportTicketResponseDTO ticket1 = new SupportTicketResponseDTO();
        ticket1.setTicketId(1);
        ticket1.setUserId(userId);
        ticket1.setIssue("Login issue");
        ticket1.setStatus(status);
        ticket1.setCreatedAt(LocalDateTime.now());

        SupportTicketResponseDTO ticket2 = new SupportTicketResponseDTO(); // Another ticket for same user, different status
        ticket2.setTicketId(2);
        ticket2.setUserId(userId);
        ticket2.setIssue("Payment issue");
        ticket2.setStatus(TicketStatus.RESOLVED);
        ticket2.setCreatedAt(LocalDateTime.now());

        // Mock service to return all tickets for the user, then the controller filters by status
        Mockito.when(service.getTicketsByUserId(userId)).thenReturn(Arrays.asList(ticket1, ticket2));

        // Perform GET request and assert
        mockMvc.perform(get("/api/support-tickets/user/{userId}/status/{status}", userId, status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].status", is(status.name())))
                .andExpect(jsonPath("$[0].userId", is(userId)));
    }

    @Test
    void testGetUserTicketsByStatus_Success_NoMatchingStatus() throws Exception {
        int userId = 100;
        TicketStatus status = TicketStatus.RESOLVED;

        SupportTicketResponseDTO ticket = new SupportTicketResponseDTO();
        ticket.setTicketId(1);
        ticket.setUserId(userId);
        ticket.setIssue("Login issue");
        ticket.setStatus(TicketStatus.IN_PROGRESS); // Different status
        ticket.setCreatedAt(LocalDateTime.now());

        Mockito.when(service.getTicketsByUserId(userId)).thenReturn(List.of(ticket));

        // Perform GET request and assert (expecting an empty list as no tickets match status)
        mockMvc.perform(get("/api/support-tickets/user/{userId}/status/{status}", userId, status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)))
                .andExpect(content().json("[]"));
    }



    @Test
    void testCreateTicket_Success() throws Exception {
        SupportTicketRequestDTO request = new SupportTicketRequestDTO();
        request.setUserId(100);
        request.setIssue("Login issue");

        SupportTicketResponseDTO response = new SupportTicketResponseDTO();
        response.setTicketId(1);
        response.setUserId(100);
        response.setIssue("Login issue");
        response.setStatus(TicketStatus.OPEN); // Typically, a new ticket is OPEN initially
        response.setCreatedAt(LocalDateTime.now());

        // Mock the service call for createTicket
        Mockito.when(service.createTicket(any(SupportTicketRequestDTO.class))).thenReturn(response);

        // Perform POST request and assert for 201 Created
        mockMvc.perform(post("/api/support-tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Convert DTO to JSON
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId", is(1)))
                .andExpect(jsonPath("$.userId", is(100)))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.issue", is("Login issue")));
    }





    @Test
    void testGetUserTicketCount_Success() throws Exception {
        int userId = 100;
        // Prepare mock data (tickets used only to determine size)
        List<SupportTicketResponseDTO> tickets = Arrays.asList(
                new SupportTicketResponseDTO(), new SupportTicketResponseDTO(), new SupportTicketResponseDTO()
        );
        Mockito.when(service.getTicketsByUserId(userId)).thenReturn(tickets);

        // Perform GET request and assert
        mockMvc.perform(get("/api/support-tickets/user/{userId}/count", userId))
                .andExpect(status().isOk())
                .andExpect(content().string(is("3"))); // Expecting the count as a string
    }

    @Test
    void testGetUserTicketCount_Success_ZeroTickets() throws Exception {
        int userId = 100;
        Mockito.when(service.getTicketsByUserId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/support-tickets/user/{userId}/count", userId))
                .andExpect(status().isOk())
                .andExpect(content().string(is("0")));
    }


}