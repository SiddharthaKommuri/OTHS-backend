package com.travel.paymentservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.travel.paymentservice.dto.PaymentServiceDTO;
import com.travel.paymentservice.controller.PaymentServiceRequest;
import com.travel.paymentservice.service.PaymentServiceServiceImpl;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/payments")
public class PaymentServiceController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceController.class);

    @Autowired
    private PaymentServiceServiceImpl paymentService;

    @PostMapping
    public ResponseEntity<PaymentServiceResponse> postPayment(@Valid @RequestBody PaymentServiceRequest paymentRequest) {
               PaymentServiceDTO dto = PaymentServiceDTO.builder()
                .userId(paymentRequest.getUserId())
                .bookingId(paymentRequest.getBookingId())
                .amount(paymentRequest.getAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .paymentStatus(paymentRequest.getPaymentStatus())
                .paymentDate(paymentRequest.getPaymentDate() != null ? paymentRequest.getPaymentDate() : LocalDateTime.now())
                .build();

        PaymentServiceDTO savedPayment = paymentService.storePayment(dto);

        logger.info("Payment stored successfully with ID: {}", savedPayment.getPaymentId());

        return new ResponseEntity<>(
                PaymentServiceResponse.builder().paymentRef(savedPayment).build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<PaymentServiceResponse> getAllPayments() {
        logger.info("Fetching all payments");
        List<PaymentServiceDTO> payments = paymentService.getAllPayments();
        return new ResponseEntity<>(
                PaymentServiceResponse.builder().payments(payments).build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PaymentServiceResponse> getPaymentsByUserId(@PathVariable Long userId) {
        logger.info("Fetching payments for userId: {}", userId);
        List<PaymentServiceDTO> payments = paymentService.getPaymentsByUserId(userId);
        return new ResponseEntity<>(
                PaymentServiceResponse.builder().payments(payments).build(),
                HttpStatus.OK
        );
    }

    @PutMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentServiceDTO> cancelPayment(@PathVariable Long paymentId) {

        return ResponseEntity.ok(paymentService.cancelPayment(paymentId));
    }
}
