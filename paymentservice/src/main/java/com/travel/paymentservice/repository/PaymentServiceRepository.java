package com.travel.paymentservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travel.paymentservice.entity.PaymentServiceEntity;

public interface PaymentServiceRepository extends JpaRepository<PaymentServiceEntity, Long>{
	
	Optional<PaymentServiceEntity> findByBookingId(Long long1);
	List<PaymentServiceEntity> findByUserId(Long userId);

}
