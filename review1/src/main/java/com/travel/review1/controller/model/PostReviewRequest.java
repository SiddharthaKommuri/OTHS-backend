package com.travel.review1.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostReviewRequest {

	@NotNull(message = "User Id is required")
	private Long userId;
	private Long hotelId;
	private Long flightId;

	@NotNull(message = "Rating is required")
	private Integer rating;

	private String comment;
}
