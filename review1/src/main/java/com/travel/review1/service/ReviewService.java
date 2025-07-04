package com.travel.review1.service;

import java.util.List;

import com.travel.review1.dto.ReviewDTO;

public interface ReviewService {

	List<ReviewDTO> getAllReviews();
	List<ReviewDTO> getHotelReviews(Long hotelId);   // Changed from Integer to Long
	List<ReviewDTO> getFlightReviews(Long flightId); // Changed from Integer to Long
	ReviewDTO postReview(ReviewDTO reviewDTO);
	List<ReviewDTO> getReviewsByUserId(Long userId); // Already Long, keeping it
}