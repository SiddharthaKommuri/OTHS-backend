package com.travel.paymentservice.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.travel.paymentservice.dto.PaymentServiceDTO;
import com.travel.paymentservice.entity.PaymentServiceEntity;
import com.travel.paymentservice.repository.PaymentServiceRepository;

@Component
public class PaymentServiceDAO {

    @Autowired
    private PaymentServiceRepository paymentServiceRepository;

    public PaymentServiceDTO insertPayment(PaymentServiceDTO paymentDto) {

        LocalDateTime paymentDate = paymentDto.getPaymentDate() != null
                ? paymentDto.getPaymentDate()
                : LocalDateTime.now();

        PaymentServiceEntity paymentEntity = PaymentServiceEntity.builder()
                .amount(paymentDto.getAmount())
                .paymentDate(paymentDate)
                .paymentMethod(paymentDto.getPaymentMethod())
                .status("Completed")
                .userId(paymentDto.getUserId())
                .createdBy(paymentDto.getUserId().toString())
                .createdDate(LocalDateTime.now())
                .updatedBy(paymentDto.getUserId().toString())
                .updatedDate(LocalDateTime.now())
                .bookingId(paymentDto.getBookingId())
                .build();

        PaymentServiceEntity savedPaymentEntity = this.paymentServiceRepository.save(paymentEntity);

        return PaymentServiceDTO.builder()
                .paymentId(savedPaymentEntity.getPaymentId())
                .amount(savedPaymentEntity.getAmount())
                .paymentDate(savedPaymentEntity.getPaymentDate())
                .paymentMethod(savedPaymentEntity.getPaymentMethod())
                .paymentStatus(savedPaymentEntity.getStatus())
                .userId(savedPaymentEntity.getUserId())
                .bookingId(savedPaymentEntity.getBookingId())
                .build();
    }

    public List<PaymentServiceEntity> getAllPayments() {
        return this.paymentServiceRepository.findAll();
    }

    public List<PaymentServiceEntity> getPaymentsByUserId(Long userId) {
        return this.paymentServiceRepository.findByUserId(userId);
    }
}
