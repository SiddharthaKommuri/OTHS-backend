package com.travel.paymentservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.paymentservice.dto.PaymentServiceDTO;
import com.travel.paymentservice.service.PaymentServiceServiceImpl;

@WebMvcTest(PaymentServiceController.class)
public class PaymentServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentServiceServiceImpl paymentServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPostPayment_Success() throws Exception {
        PaymentServiceRequest request = PaymentServiceRequest.builder()
                .userId(1L)
                .bookingId(2L)
                .amount(BigDecimal.valueOf(150.00))
                .paymentMethod("CreditCard")
                .paymentStatus("Completed")
                .paymentDate(LocalDateTime.now())
                .build();

        PaymentServiceDTO savedDTO = PaymentServiceDTO.builder()
                .paymentId(100L)
                .userId(1L)
                .bookingId(2L)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("Completed")
                .paymentDate(request.getPaymentDate())
                .build();

        when(paymentServiceImpl.storePayment(any())).thenReturn(savedDTO);

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentRef.paymentId").value(100));
    }

    @Test
    void testPostPayment_InternalServerError() throws Exception {
        PaymentServiceRequest request = PaymentServiceRequest.builder()
                .userId(1L)
                .bookingId(2L)
                .amount(BigDecimal.valueOf(150.00))
                .paymentMethod("CreditCard")
                .paymentStatus("Completed")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentServiceImpl.storePayment(any())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetAllPayments_Success() throws Exception {
        PaymentServiceDTO dto = PaymentServiceDTO.builder()
                .paymentId(100L)
                .userId(1L)
                .bookingId(2L)
                .amount(BigDecimal.valueOf(150.00))
                .paymentMethod("CreditCard")
                .paymentStatus("Completed")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentServiceImpl.getAllPayments()).thenReturn(List.of(dto));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments[0].paymentId").value(100));
    }

    @Test
    void testGetAllPayments_InternalServerError() throws Exception {
        when(paymentServiceImpl.getAllPayments()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetPaymentsByUserId_Success() throws Exception {
        Long userId = 1L;

        PaymentServiceDTO dto1 = PaymentServiceDTO.builder()
                .paymentId(101L)
                .userId(userId)
                .bookingId(201L)
                .amount(BigDecimal.valueOf(200.00))
                .paymentMethod("UPI")
                .paymentStatus("Completed")
                .paymentDate(LocalDateTime.now())
                .build();

        PaymentServiceDTO dto2 = PaymentServiceDTO.builder()
                .paymentId(102L)
                .userId(userId)
                .bookingId(202L)
                .amount(BigDecimal.valueOf(300.00))
                .paymentMethod("DebitCard")
                .paymentStatus("Completed")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentServiceImpl.getPaymentsByUserId(userId)).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/payments/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments.length()").value(2))
                .andExpect(jsonPath("$.payments[0].userId").value(userId.intValue()));
    }

    @Test
    void testGetPaymentsByUserId_NoPaymentsFound() throws Exception {
        Long userId = 999L;

        when(paymentServiceImpl.getPaymentsByUserId(userId)).thenReturn(List.of());

        mockMvc.perform(get("/payments/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments").isArray())
                .andExpect(jsonPath("$.payments.length()").value(0));
    }

    @Test
    void testGetPaymentsByUserId_InternalServerError() throws Exception {
        Long userId = 2L;

        when(paymentServiceImpl.getPaymentsByUserId(userId))
                .thenThrow(new RuntimeException("Service Unavailable"));

        mockMvc.perform(get("/payments/user/{userId}", userId))
                .andExpect(status().isInternalServerError());
    }
}
