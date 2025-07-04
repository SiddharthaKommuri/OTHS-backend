package com.travel.review1.reviewService; // Consider renaming this package to com.travel.review1.service if it's for service tests

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.travel.review1.dao.ReviewDao;
import com.travel.review1.dto.ReviewDTO;
import com.travel.review1.entity.Review;
import com.travel.review1.service.ReviewServiceImpl; // Corrected import

public class ReviewServiceImplTest {

	@InjectMocks
	private ReviewServiceImpl reviewService;

	@Mock
	private ReviewDao reviewDao;

	private Review sampleReview;
	private ReviewDTO sampleDTO;
	private ReviewDTO sampleFlightDTO; // Added for flight-specific test

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		// Sample Review Entity (for mocking DAO responses)
		sampleReview = Review.builder()
				.reviewId(1L)   // Changed to Long
				.userId(10L)    // Changed to Long
				.hotelId(101L)  // Changed to Long
				.rating(5)
				.comment("Great hotel")
				.createdBy("10")
				.updatedBy("10")
				.timestamp(LocalDateTime.now())
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();

		// Sample Review DTO (for input and expected output)
		sampleDTO = ReviewDTO.builder()
				.reviewId(1L)   // Changed to Long
				.userId(10L)    // Changed to Long
				.hotelId(101L)  // Changed to Long
				.rating(5)
				.comment("Great hotel")
				.createdBy("10")
				.updatedBy("10")
				.timestamp(sampleReview.getTimestamp())
				.createdDate(sampleReview.getCreatedDate())
				.updatedDate(sampleReview.getUpdatedDate())
				.build();

		// Sample Flight Review DTO (for flight-specific tests if needed)
		sampleFlightDTO = ReviewDTO.builder()
				.reviewId(2L)
				.userId(11L)
				.flightId(201L)
				.rating(4)
				.comment("Good flight")
				.createdBy("11")
				.updatedBy("11")
				.timestamp(LocalDateTime.now())
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
	}

	@Test
	public void testGetAllReviews_Success() {
		when(reviewDao.getAllReviews()).thenReturn(List.of(sampleReview));
		List<ReviewDTO> result = reviewService.getAllReviews();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(sampleDTO.getUserId(), result.get(0).getUserId());
		assertEquals(sampleDTO.getReviewId(), result.get(0).getReviewId()); // Added more assertions
		verify(reviewDao, times(1)).getAllReviews();
	}

	@Test
	public void testGetAllReviews_EmptyList() {
		when(reviewDao.getAllReviews()).thenReturn(Collections.emptyList());
		List<ReviewDTO> result = reviewService.getAllReviews();
		assertNull(result); // Service currently returns null for empty. Consider returning empty list.
		verify(reviewDao).getAllReviews();
	}

	@Test
	public void testGetHotelReviews_Success() {
		when(reviewDao.getReviewsByHotelId(101L)).thenReturn(List.of(sampleReview)); // Changed to Long
		List<ReviewDTO> result = reviewService.getHotelReviews(101L); // Changed to Long
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(sampleDTO.getHotelId(), result.get(0).getHotelId()); // Assert hotelId
		verify(reviewDao).getReviewsByHotelId(101L); // Changed to Long
	}

	@Test
	public void testGetHotelReviews_Empty() {
		when(reviewDao.getReviewsByHotelId(101L)).thenReturn(Collections.emptyList()); // Changed to Long
		List<ReviewDTO> result = reviewService.getHotelReviews(101L); // Changed to Long
		assertNull(result); // Service currently returns null for empty.
	}

	@Test
	public void testPostReview_Success() {
		when(reviewDao.saveReview(any(Review.class))).thenReturn(sampleReview); // Specify type for any()
		ReviewDTO result = reviewService.postReview(sampleDTO);

		assertNotNull(result);
		assertEquals(sampleDTO.getRating(), result.getRating());
		assertEquals(sampleDTO.getUserId(), result.getUserId()); // Assert userId
		assertEquals(sampleDTO.getHotelId(), result.getHotelId()); // Assert hotelId
		verify(reviewDao).saveReview(any(Review.class)); // Specify type for any()
	}

	@Test
	public void testGetFlightReviews_Success() {
		// Ensure sampleFlightDTO is used or a review with flightId is created for this test
		Review reviewWithFlight = Review.builder()
				.reviewId(2L).userId(11L).flightId(201L).rating(4).comment("Flight review")
				.createdBy("11").updatedBy("11")
				.timestamp(LocalDateTime.now()).createdDate(LocalDateTime.now()).updatedDate(LocalDateTime.now())
				.build();
		ReviewDTO dtoWithFlight = ReviewDTO.builder()
				.reviewId(2L).userId(11L).flightId(201L).rating(4).comment("Flight review")
				.createdBy("11").updatedBy("11")
				.timestamp(reviewWithFlight.getTimestamp()).createdDate(reviewWithFlight.getCreatedDate()).updatedDate(reviewWithFlight.getUpdatedDate())
				.build();

		when(reviewDao.getReviewsByFlightId(201L)).thenReturn(List.of(reviewWithFlight)); // Changed to Long
		List<ReviewDTO> result = reviewService.getFlightReviews(201L); // Changed to Long
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(dtoWithFlight.getFlightId(), result.get(0).getFlightId()); // Assert flightId
		verify(reviewDao).getReviewsByFlightId(201L); // Changed to Long
	}

	@Test
	public void testGetFlightReviews_Empty() {
		when(reviewDao.getReviewsByFlightId(201L)).thenReturn(Collections.emptyList()); // Changed to Long
		List<ReviewDTO> result = reviewService.getFlightReviews(201L); // Changed to Long
		assertNull(result); // Service currently returns null for empty.
	}

	@Test
	public void testPostReview_Exception() {
		when(reviewDao.saveReview(any(Review.class))).thenThrow(new RuntimeException("DB error")); // Specify type for any()
		assertThrows(RuntimeException.class, () -> reviewService.postReview(sampleDTO));
	}

	@Test
	public void testGetReviewsByUserId_Success() {
		// Create a review entity for a specific user
		Review userReview = Review.builder()
				.reviewId(3L)
				.userId(10L)
				.hotelId(105L)
				.rating(3)
				.comment("User 10's review")
				.createdBy("10")
				.updatedBy("10")
				.timestamp(LocalDateTime.now())
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.build();
		ReviewDTO userReviewDTO = ReviewDTO.builder()
				.reviewId(3L)
				.userId(10L)
				.hotelId(105L)
				.rating(3)
				.comment("User 10's review")
				.createdBy("10")
				.updatedBy("10")
				.timestamp(userReview.getTimestamp())
				.createdDate(userReview.getCreatedDate())
				.updatedDate(userReview.getUpdatedDate())
				.build();


		when(reviewDao.getReviewsByUserId(10L)).thenReturn(List.of(userReview)); // Mock DAO for user ID
		List<ReviewDTO> result = reviewService.getReviewsByUserId(10L); // Call service method

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(userReviewDTO.getUserId(), result.get(0).getUserId());
		assertEquals(userReviewDTO.getComment(), result.get(0).getComment());
		verify(reviewDao, times(1)).getReviewsByUserId(10L); // Verify DAO method call
	}

	@Test
	public void testGetReviewsByUserId_EmptyList() {
		when(reviewDao.getReviewsByUserId(999L)).thenReturn(Collections.emptyList()); // Mock empty list for non-existent user
		List<ReviewDTO> result = reviewService.getReviewsByUserId(999L);
		assertNull(result); // Service currently returns null for empty.
		verify(reviewDao, times(1)).getReviewsByUserId(999L);
	}
}