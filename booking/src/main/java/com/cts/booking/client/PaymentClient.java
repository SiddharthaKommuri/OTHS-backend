package com.cts.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "paymentservice")
public interface PaymentClient {

    @PutMapping("/api/payments/{paymentId}/cancel")
    void cancelPayment(@PathVariable("paymentId") Long paymentId);
}
