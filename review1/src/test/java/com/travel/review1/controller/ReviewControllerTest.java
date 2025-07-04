package com.travel.review1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.review1.controller.model.PostReviewRequest;
import com.travel.review1.dto.ReviewDTO;
import com.travel.review1.service.ReviewService;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ReviewService reviewService;

	@Autowired
	private ObjectMapper objectMapper;

	private ReviewDTO reviewDTO;

	@BeforeEach
	void setUp() {
		reviewDTO = ReviewDTO.builder()
				.reviewId(1L) // Changed to Long
				.userId(10L)  // Changed to Long
				.hotelId(101L) // Changed to Long
				.rating(4)
				.comment("Nice stay")
				.createdBy("10")
				.updatedBy("10")
				.timestamp(LocalDateTime.now())
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
	}

	@Test
	void testGetAllReviews_Success() throws Exception {
		when(reviewService.getAllReviews()).thenReturn(List.of(reviewDTO));

		// Note: "/reviews" is the correct path according to @RequestMapping("/api/reviews")
		// but the test uses "/reviews". Assuming you might be testing without a base path or have a setup.
		// If your controller mapping is /api/reviews, the get path should be /api/reviews
		mockMvc.perform(get("/api/reviews")) // Corrected path to match @RequestMapping
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.reviews[0].userId").value(10L)); // Changed to Long
	}

	@Test
	void testGetHotelReviews_Success() throws Exception {
		when(reviewService.getHotelReviews(101L)).thenReturn(List.of(reviewDTO)); // Changed to Long

		mockMvc.perform(get("/api/reviews/hotel/101")) // Path still string, but controller expects Long
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.reviews[0].hotelId").value(101L)); // Changed to Long
	}

	@Test
	void testGetFlightReviews_Success() throws Exception {
		// Ensure reviewDTO has flightId if you want to assert its presence.
		// Currently, it only has hotelId, so .doesNotExist() is correct if no flightId is set.
		// For testing, let's create a reviewDTO with flightId.
		ReviewDTO flightReviewDTO = ReviewDTO.builder()
				.reviewId(2L)
				.userId(11L)
				.flightId(201L) // Set flightId for this test
				.rating(5)
				.comment("Great flight")
				.createdBy("11")
				.updatedBy("11")
				.timestamp(LocalDateTime.now())
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();

		when(reviewService.getFlightReviews(201L)).thenReturn(List.of(flightReviewDTO)); // Changed to Long

		mockMvc.perform(get("/api/reviews/flight/201")) // Path still string, but controller expects Long
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.reviews[0].flightId").value(201L)); // Changed to Long
		// Removed .doesNotExist() as we are now returning a review with flightId
	}

	@Test
	void testPostReview_Success() throws Exception {
		PostReviewRequest request = PostReviewRequest.builder()
				.userId(10L)   // Changed to Long
				.hotelId(101L) // Changed to Long
				.rating(5)
				.comment("Wonderful")
				.build();

		// Ensure the returned DTO matches the expected IDs
		ReviewDTO postedReviewDTO = ReviewDTO.builder()
				.reviewId(5L) // Example ID for a newly posted review
				.userId(10L)
				.hotelId(101L)
				.rating(5)
				.comment("Wonderful")
				.createdBy("10")
				.updatedBy("10")
				.timestamp(LocalDateTime.now())
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();

		when(reviewService.postReview(any(ReviewDTO.class))).thenReturn(postedReviewDTO); // Mocking with any(ReviewDTO.class) for flexibility

		// Note: "/review" is incorrect if your controller's @RequestMapping is "/api/reviews".
		// It should be "/api/reviews" for POST.
		mockMvc.perform(post("/api/reviews") // Corrected path to match @RequestMapping
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.review.userId").value(10L)) // Changed to Long
				.andExpect(jsonPath("$.review.hotelId").value(101L)); // Added assertion for hotelId
	}

	@Test
	void testPostReview_BothHotelAndFlightIdsProvided() throws Exception {
		PostReviewRequest request = PostReviewRequest.builder()
				.userId(10L)   // Changed to Long
				.hotelId(101L) // Changed to Long
				.flightId(202L) // Changed to Long
				.rating(4)
				.build();

		// The controller itself throws an Exception before calling reviewService, so no mocking is needed for reviewService.postReview here.
		mockMvc.perform(post("/api/reviews") // Corrected path
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest()) // Controller throws Exception which results in 500 by default, but you mapped it to 400 for validation errors, let's keep it as is if it's already implemented.
				// NOTE: Based on your `ReviewController`'s `postHotelReview`, it throws `new Exception("Please send either Flight Id or Hotel Id");`.
				// Spring's default exception handling usually converts `Exception` to a 500 Internal Server Error.
				// If you have a custom `@ExceptionHandler` for `Exception` to return 400, then `isBadRequest()` is correct.
				// Otherwise, it might be `isInternalServerError()`.
				// Let's assume you have an `@ExceptionHandler` that maps it to 400.
				.andExpect(jsonPath("$.message").value("Please send either Flight Id or Hotel Id")); // Corrected JSON path for error message
	}

	@Test
	void testPostReview_MissingRequiredField() throws Exception {
		// This test likely checks @Valid annotations on PostReviewRequest fields.
		// Assuming your PostReviewRequest has @NotNull on userId, this is correct.
		PostReviewRequest request = PostReviewRequest.builder()
				.hotelId(101L) // Changed to Long
				.rating(5)
				.build();

		mockMvc.perform(post("/api/reviews") // Corrected path
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.userId").value("User Id is required")); // Assuming this is your validation error message
	}

	// New test case for getReviewsByUserId
	@Test
	void testGetReviewsByUserId_Success() throws Exception {
		// Create a review DTO specific to a user
		ReviewDTO userReviewDTO = ReviewDTO.builder()
				.reviewId(3L)
				.userId(10L)
				.hotelId(103L)
				.rating(4)
				.comment("Good service for user 10")
				.createdBy("10")
				.updatedBy("10")
				.timestamp(LocalDateTime.now())
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();

		when(reviewService.getReviewsByUserId(10L)).thenReturn(List.of(userReviewDTO)); // Changed to Long

		mockMvc.perform(get("/api/reviews/user/10")) // Changed path and parameter to Long
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.reviews[0].userId").value(10L)) // Changed to Long
				.andExpect(jsonPath("$.reviews[0].comment").value("Good service for user 10"));
	}
}