package com.travel.paymentservice.controller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.travel.paymentservice.dto.PaymentServiceDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentServiceResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PaymentServiceDTO paymentRef;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PaymentServiceDTO> payments;
}
