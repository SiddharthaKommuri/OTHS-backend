package com.travel.paymentservice.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.travel.paymentservice.dto.PaymentServiceDTO;
import com.travel.paymentservice.entity.PaymentServiceEntity;
import com.travel.paymentservice.repository.PaymentServiceRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceDAOTest {

    @InjectMocks
    private PaymentServiceDAO paymentDAO;

    @Mock
    private PaymentServiceRepository paymentRepository;

    @Test
    public void testInsertPayment() {
        PaymentServiceDTO dto = PaymentServiceDTO.builder()
                .amount(BigDecimal.valueOf(100.00))
                .paymentDate(LocalDateTime.now())
                .paymentMethod("Credit Card")
                .paymentStatus("Success")
                .userId(1L)
                .bookingId(101L)
                .build();

        PaymentServiceEntity entity = PaymentServiceEntity.builder()
                .paymentId(1L)
                .amount(dto.getAmount())
                .paymentDate(dto.getPaymentDate())
                .paymentMethod(dto.getPaymentMethod())
                .status(dto.getPaymentStatus())
                .userId(dto.getUserId())
                .bookingId(dto.getBookingId())
                .createdBy("1")
                .createdDate(LocalDateTime.now())
                .updatedBy("1")
                .updatedDate(LocalDateTime.now())
                .build();

        when(paymentRepository.save(any(PaymentServiceEntity.class))).thenReturn(entity);

        PaymentServiceDTO result = paymentDAO.insertPayment(dto);

        assertNotNull(result);
        assertEquals(dto.getAmount(), result.getAmount());
        assertEquals(dto.getUserId(), result.getUserId());
        assertEquals(dto.getBookingId(), result.getBookingId());
    }

    @Test
    public void testGetAllPayments() {
        List<PaymentServiceEntity> payments = List.of(
                PaymentServiceEntity.builder().paymentId(1L).amount(BigDecimal.valueOf(100.00)).build(),
                PaymentServiceEntity.builder().paymentId(2L).amount(BigDecimal.valueOf(200.00)).build()
        );

        when(paymentRepository.findAll()).thenReturn(payments);

        List<PaymentServiceEntity> result = paymentDAO.getAllPayments();

        assertEquals(2, result.size());
        assertEquals(BigDecimal.valueOf(100.00), result.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(200.00), result.get(1).getAmount());
    }
}
