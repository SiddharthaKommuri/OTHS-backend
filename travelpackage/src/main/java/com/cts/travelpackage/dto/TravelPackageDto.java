package com.cts.travelpackage.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import jakarta.validation.constraints.*; // Ensure all validation annotations are imported

@Data
public class TravelPackageDto {

	private Long packageId;

	@NotBlank(message = "Package name cannot be blank")
	private String packageName;

	@NotBlank(message = "Location cannot be blank") // NEW: Add validation for location
	private String location; // NEW: Add location field

	@NotEmpty(message = "Included hotel IDs cannot be empty")
	private List<Long> includedHotelIds;

	@NotEmpty(message = "Included flight IDs cannot be empty")
	private List<Long> includedFlightIds;

	@NotEmpty(message = "Activities list cannot be empty")
	private List<String> activities;

	@NotNull(message = "Price cannot be null")
	@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
	private BigDecimal price;

	private String createdBy;// This is the user ID of the agent who created the package
	private String createdAt;
	private String status;
}