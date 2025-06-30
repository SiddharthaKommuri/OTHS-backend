package com.travel.paymentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.travel.paymentservice.dao.PaymentServiceDAO;
import com.travel.paymentservice.dto.PaymentServiceDTO;
import com.travel.paymentservice.entity.PaymentServiceEntity;
import com.travel.paymentservice.exceptions.DuplicatePaymentException;
import com.travel.paymentservice.repository.PaymentServiceRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceServiceImplTest {

    @Mock
    private PaymentServiceDAO paymentServiceDAO;

    @Mock
    private PaymentServiceRepository paymentServiceRepository;

    @InjectMocks
    private PaymentServiceServiceImpl paymentService;

    @Test
    void testStorePaymentSuccess() {
        PaymentServiceDTO dto = PaymentServiceDTO.builder()
                .userId(1L)
                .bookingId(101L)
                .amount(BigDecimal.valueOf(100.00))
                .paymentMethod("Credit Card")
                .paymentStatus("Completed")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentServiceRepository.findByBookingId(101L)).thenReturn(Optional.empty());
        when(paymentServiceDAO.insertPayment(dto)).thenReturn(dto);

        PaymentServiceDTO result = paymentService.storePayment(dto);

        assertNotNull(result);
        assertEquals(101L, result.getBookingId());
        assertEquals(dto.getAmount(), result.getAmount());
        assertEquals(dto.getPaymentMethod(), result.getPaymentMethod());
        assertEquals(dto.getPaymentStatus(), result.getPaymentStatus());
    }

    @Test
    void testStorePaymentDuplicate() {
        PaymentServiceDTO dto = PaymentServiceDTO.builder()
                .userId(1L)
                .bookingId(101L)
                .amount(BigDecimal.valueOf(100.00))
                .paymentMethod("Credit Card")
                .paymentStatus("Completed")
                .paymentDate(LocalDateTime.now())
                .build();

        PaymentServiceEntity existingPayment = PaymentServiceEntity.builder()
                .paymentId(1L)
                .bookingId(101L)
                .build();

        when(paymentServiceRepository.findByBookingId(101L)).thenReturn(Optional.of(existingPayment));

        DuplicatePaymentException ex = assertThrows(DuplicatePaymentException.class, () -> {
            paymentService.storePayment(dto);
        });

        assertEquals("Payment already exists with this booking ID: 101", ex.getMessage());
    }

    @Test
    void testStorePaymentUnexpectedException() {
        PaymentServiceDTO dto = PaymentServiceDTO.builder()
                .userId(1L)
                .bookingId(101L)
                .amount(BigDecimal.valueOf(100.00))
                .paymentMethod("Credit Card")
                .paymentStatus("Completed")
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentServiceRepository.findByBookingId(101L)).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.storePayment(dto);
        });

        assertEquals("DB error", exception.getMessage());
    }

    @Test
    void testGetAllPaymentsSuccess() {
        List<PaymentServiceEntity> mockList = List.of(
                PaymentServiceEntity.builder().paymentId(1L).userId(1L).bookingId(101L).amount(BigDecimal.valueOf(100)).paymentMethod("UPI").status("Completed").paymentDate(LocalDateTime.now()).build(),
                PaymentServiceEntity.builder().paymentId(2L).userId(2L).bookingId(102L).amount(BigDecimal.valueOf(200)).paymentMethod("Card").status("Completed").paymentDate(LocalDateTime.now()).build()
        );

        when(paymentServiceDAO.getAllPayments()).thenReturn(mockList);

        List<PaymentServiceDTO> result = paymentService.getAllPayments();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getBookingId());
    }

    @Test
    void testGetAllPaymentsEmpty() {
        when(paymentServiceDAO.getAllPayments()).thenReturn(List.of());

        List<PaymentServiceDTO> result = paymentService.getAllPayments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPaymentsByUserIdSuccess() {
        Long userId = 10L;

        List<PaymentServiceEntity> mockPayments = List.of(
                PaymentServiceEntity.builder().paymentId(1L).userId(userId).bookingId(201L).amount(BigDecimal.valueOf(300)).paymentMethod("Wallet").status("Completed").paymentDate(LocalDateTime.now()).build()
        );

        when(paymentServiceDAO.getPaymentsByUserId(userId)).thenReturn(mockPayments);

        List<PaymentServiceDTO> result = paymentService.getPaymentsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(201L, result.get(0).getBookingId());
    }

    @Test
    void testGetPaymentsByUserIdEmptyList() {
        when(paymentServiceDAO.getPaymentsByUserId(20L)).thenReturn(List.of());

        List<PaymentServiceDTO> result = paymentService.getPaymentsByUserId(20L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPaymentsByUserIdException() {
        when(paymentServiceDAO.getPaymentsByUserId(30L)).thenThrow(new RuntimeException("Failed to fetch"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            paymentService.getPaymentsByUserId(30L);
        });

        assertEquals("Failed to fetch", ex.getMessage());
    }
}
