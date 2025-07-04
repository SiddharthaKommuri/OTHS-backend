package com.travel.review1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travel.review1.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByUserId(Long userId);
	 
    List<Review> findByHotelId(Long hotelId);
 
    List<Review> findByFlightId(Long flightId);

}
