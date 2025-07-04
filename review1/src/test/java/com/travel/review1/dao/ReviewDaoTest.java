package com.travel.review1.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.travel.review1.entity.Review;
import com.travel.review1.repository.ReviewRepository;

public class ReviewDaoTest {

	@InjectMocks
	private ReviewDao reviewDao;

	@Mock
	private ReviewRepository reviewRepository;

	private Review sampleReview;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		sampleReview = Review.builder()
				.reviewId(1L)   // Changed to Long
				.userId(10L)    // Changed to Long
				.hotelId(100L)  // Changed to Long
				.rating(5)
				.comment("Excellent stay")
				.timestamp(LocalDateTime.now())
				.createdDate(LocalDateTime.now())
				.updatedDate(LocalDateTime.now())
				.createdBy("10")
				.updatedBy("10")
				.build();
	}

	@Test
	void testSaveReview() {
		when(reviewRepository.save(sampleReview)).thenReturn(sampleReview);
		Review saved = reviewDao.saveReview(sampleReview);
		assertNotNull(saved);
		assertEquals(sampleReview.getReviewId(), saved.getReviewId());
	}

	@Test
	void testGetReviewById_Found() {
		when(reviewRepository.findById(1L)).thenReturn(Optional.of(sampleReview)); // Changed to Long
		Optional<Review> result = reviewDao.getReviewById(1L); // Changed to Long
		assertTrue(result.isPresent());
		assertEquals(10L, result.get().getUserId()); // Changed to Long
	}

	@Test
	void testGetReviewById_NotFound() {
		when(reviewRepository.findById(99L)).thenReturn(Optional.empty()); // Changed to Long
		Optional<Review> result = reviewDao.getReviewById(99L); // Changed to Long
		assertTrue(result.isEmpty());
	}

	@Test
	void testGetAllReviews() {
		when(reviewRepository.findAll()).thenReturn(Arrays.asList(sampleReview));
		List<Review> result = reviewDao.getAllReviews();
		assertEquals(1, result.size());
	}

	@Test
	void testGetReviewsByUserId() {
		when(reviewRepository.findByUserId(10L)).thenReturn(List.of(sampleReview)); // Changed to Long
		List<Review> result = reviewDao.getReviewsByUserId(10L); // Changed to Long
		assertEquals(1, result.size());
	}

	@Test
	void testGetReviewsByHotelId() {
		when(reviewRepository.findByHotelId(100L)).thenReturn(List.of(sampleReview)); // Changed to Long
		List<Review> result = reviewDao.getReviewsByHotelId(100L); // Changed to Long
		assertEquals(1, result.size());
	}

	@Test
	void testGetReviewsByFlightId() {
		// Assuming a flight ID of 200L for testing purposes
		when(reviewRepository.findByFlightId(200L)).thenReturn(List.of(sampleReview)); // Changed to Long
		List<Review> result = reviewDao.getReviewsByFlightId(200L); // Changed to Long
		assertEquals(1, result.size());
	}

	@Test
	void testExistsById_True() {
		when(reviewRepository.existsById(1L)).thenReturn(true); // Changed to Long
		assertTrue(reviewDao.existsById(1L)); // Changed to Long
	}

	@Test
	void testExistsById_False() {
		when(reviewRepository.existsById(2L)).thenReturn(false); // Changed to Long
		assertFalse(reviewDao.existsById(2L)); // Changed to Long
	}
}