package com.travel.review1.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDTO {

	private Long reviewId;
	private Long userId;
	private Long hotelId;
	private Long flightId;
	private Integer rating;
	private String comment;
	private LocalDateTime timestamp;
	private LocalDateTime createdDate;
	private String createdBy;
	private LocalDateTime updatedDate;
	private String updatedBy;

}