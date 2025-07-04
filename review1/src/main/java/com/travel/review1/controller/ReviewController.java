package com.travel.review1.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.travel.review1.controller.model.PostReviewRequest;
import com.travel.review1.controller.model.PostReviewResponse;
import com.travel.review1.controller.model.ReviewResponse;
import com.travel.review1.dto.ReviewDTO;
import com.travel.review1.service.ReviewService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Controller for handling review-related endpoints.
 */

@RestController
@RequestMapping("/api/reviews")
//@CrossOrigin // Keep this commented out if API Gateway handles CORS
public class ReviewController {
	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
	private ReviewService reviewServiceImpl;

	public ReviewController(ReviewService reviewServiceImpl) {
		this.reviewServiceImpl = reviewServiceImpl;
	}

	/**
	 * Retrieves all reviews.
	 * @return ResponseEntity containing all reviews
	 */
	@GetMapping
	public ResponseEntity<ReviewResponse> getAllReviews() {
		logger.info("Fetching all reviews");
		List<ReviewDTO> reviews = this.reviewServiceImpl.getAllReviews();
		return new ResponseEntity<ReviewResponse>(ReviewResponse.builder().reviews(reviews).build(), HttpStatus.OK);

	}

	/**
	 * Retrieves reviews for a specific hotel.
	 * @param hotelId ID of the hotel
	 * @return ResponseEntity containing hotel reviews
	 */
	@GetMapping(path = "/hotel/{hotelId}")
	public ResponseEntity<ReviewResponse> getHotelReviews(
			@NotNull(message = "Please provide hotel id as path variable") @PathVariable(name = "hotelId") Long hotelId) { // Changed to Long

		logger.info("Fetching reviews for hotelId: {}", hotelId);

		List<ReviewDTO> reviews = this.reviewServiceImpl.getHotelReviews(hotelId); // Passed Long to service

		return new ResponseEntity<ReviewResponse>(ReviewResponse.builder().reviews(reviews).build(), HttpStatus.OK);

	}

	/**
	 * Retrieves reviews for a specific flight.
	 * @param flightId ID of the flight
	 * @return ResponseEntity containing flight reviews
	 */
	@GetMapping(path = "/flight/{flightId}")
	public ResponseEntity<ReviewResponse> getFlightReviews(
			@NotNull(message = "Please provide flight id as path variable") @PathVariable(name = "flightId") Long flightId) { // Changed to Long
		logger.info("Fetching reviews for flightId: {}", flightId);
		List<ReviewDTO> reviews = this.reviewServiceImpl.getFlightReviews(flightId); // Passed Long to service

		return new ResponseEntity<ReviewResponse>(ReviewResponse.builder().reviews(reviews).build(), HttpStatus.OK);

	}

	/**
	 * Posts a new review for either a hotel or a flight.
	 *
	 * @param postReviewRequest Request body containing review details
	 * @return ResponseEntity containing the posted review
	 * @throws Exception if both hotelId and flightId are provided
	 */
	@PostMapping
	public ResponseEntity<PostReviewResponse> postHotelReview(@Valid @RequestBody PostReviewRequest postReviewRequest)
			throws Exception {
		logger.info("Posting a new review: {}", postReviewRequest);
		if ((postReviewRequest.getFlightId() != null && postReviewRequest.getHotelId() != null)) {

			logger.error("Both flightId and hotelId provided. Only one is allowed.");

			throw new Exception("Please send either Flight Id or Hotel Id");
		}
		// Assuming PostReviewRequest's hotelId and flightId are also Long, as per previous discussion
		ReviewDTO reviewDTO = ReviewDTO.builder().userId(postReviewRequest.getUserId())
				.hotelId(postReviewRequest.getHotelId()).flightId(postReviewRequest.getFlightId())
				.comment(postReviewRequest.getComment()).rating(postReviewRequest.getRating()).build();
		ReviewDTO postedReview = this.reviewServiceImpl.postReview(reviewDTO);
		return new ResponseEntity<PostReviewResponse>(PostReviewResponse.builder().review(postedReview).build(),
				HttpStatus.CREATED);
	}

	/**
	 * Retrieves reviews for a specific user.
	 * This endpoint is used by the frontend to display "My Reviews".
	 *
	 * @param userId ID of the user
	 * @return ResponseEntity containing user's reviews
	 */
	@GetMapping(path = "/user/{userId}")
	public ResponseEntity<ReviewResponse> getReviewsByUserId(
			@NotNull(message = "Please provide user id as path variable") @PathVariable(name = "userId") Long userId) {
		logger.info("Fetching reviews for userId: {}", userId);
		List<ReviewDTO> reviews = this.reviewServiceImpl.getReviewsByUserId(userId);
		return new ResponseEntity<>(ReviewResponse.builder().reviews(reviews).build(), HttpStatus.OK);
	}
}