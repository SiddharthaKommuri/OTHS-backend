package com.travel.review1.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist; // Import PrePersist for initial timestamp/createdDate setting
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Review") // Or "reviews" based on your preference
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Review_ID")
	private Long reviewId;

	@Column(name = "User_ID", nullable = false)
	private Long userId;

	@Column(name = "Hotel_ID")
	private Long hotelId;

	@Column(name = "Flight_ID")
	private Long flightId;

	@Column(name = "Rating")
	private Integer rating;

	@Column(name = "Comment", length = 1000)
	private String comment;


	@Column(name = "Timestamp", nullable = false, updatable = false) // Marked as not updatable
	private LocalDateTime timestamp;


	@Column(name = "Created_Date", nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "Created_By")
	private String createdBy;

	@Column(name = "Updated_Date")
	private LocalDateTime updatedDate;

	@Column(name = "Updated_By")
	private String updatedBy;
	@PrePersist
	public void prePersist() {
		if (this.createdDate == null) {
			this.createdDate = LocalDateTime.now();
		}
		if (this.timestamp == null) {
			this.timestamp = LocalDateTime.now(); // Set review timestamp on creation
		}
		if (this.updatedDate == null) { // Also set updatedDate on initial creation
			this.updatedDate = LocalDateTime.now();
		}
	}
	@PreUpdate
	public void preUpdate() {
		this.updatedDate = LocalDateTime.now();

	}
}