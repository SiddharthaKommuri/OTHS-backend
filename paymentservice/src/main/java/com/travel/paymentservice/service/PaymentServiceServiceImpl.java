package com.travel.paymentservice.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travel.paymentservice.dao.PaymentServiceDAO;
import com.travel.paymentservice.dto.PaymentServiceDTO;
import com.travel.paymentservice.entity.PaymentServiceEntity;
import com.travel.paymentservice.exceptions.DuplicatePaymentException;
import com.travel.paymentservice.repository.PaymentServiceRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceServiceImpl.class);

    @Autowired
    private PaymentServiceDAO paymentDao;

    @Autowired
    private PaymentServiceRepository paymentRepository;

    @Transactional
    public PaymentServiceDTO storePayment(PaymentServiceDTO paymentDto) {
        logger.info("Attempting to store payment: {}", paymentDto);

        Optional<PaymentServiceEntity> existingPayment = paymentRepository.findByBookingId(paymentDto.getBookingId());
        if (existingPayment.isPresent()) {
            throw new DuplicatePaymentException(
                "Payment already exists with this booking ID: " + paymentDto.getBookingId());
        }

        logger.info("Storing payment for bookingId: {}", paymentDto.getBookingId());
        return paymentDao.insertPayment(paymentDto);
    }

    @Transactional
    public List<PaymentServiceDTO> getAllPayments() {
        logger.info("Retrieving all payments from the database");

        List<PaymentServiceEntity> allPayments = paymentDao.getAllPayments();
        if (allPayments != null && !allPayments.isEmpty()) {
            logger.info("Found {} payments", allPayments.size());
            return allPayments.stream()
                    .map(this::mapPaymentToDTO)
                    .collect(Collectors.toList());
        }

        logger.warn("No payments found in the database");
        return Collections.emptyList();
    }

    @Transactional
    public List<PaymentServiceDTO> getPaymentsByUserId(Long userId) {
        logger.info("Retrieving payments for userId: {}", userId);

        List<PaymentServiceEntity> userPayments = paymentDao.getPaymentsByUserId(userId);
        if (userPayments != null && !userPayments.isEmpty()) {
            logger.info("Found {} payments for userId {}", userPayments.size(), userId);
            return userPayments.stream()
                    .map(this::mapPaymentToDTO)
                    .collect(Collectors.toList());
        }

        logger.warn("No payments found for userId: {}", userId);
        return Collections.emptyList();
    }

    private PaymentServiceDTO mapPaymentToDTO(PaymentServiceEntity payment) {
        return PaymentServiceDTO.builder()
                .paymentId(payment.getPaymentId())
                .userId(payment.getUserId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }


    public PaymentServiceDTO cancelPayment(Long paymentId) {
        PaymentServiceEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("cancelled");
        paymentRepository.save(payment);

        return mapPaymentToDTO(payment);
    }

}
