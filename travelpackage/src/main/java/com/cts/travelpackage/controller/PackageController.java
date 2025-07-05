package com.cts.travelpackage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.travelpackage.dto.TravelPackageDto;
import com.cts.travelpackage.dto.TravelPackageResponse;
import com.cts.travelpackage.service.PackageService;
import com.cts.travelpackage.util.AppConstants; // Assuming this is defined

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/packages")
@Slf4j
//@CrossOrigin
public class PackageController {

	private PackageService packageService;

	public PackageController( PackageService packageService) {
		this.packageService = packageService;
	}


	/**
	 * Creates a new travel package.
	 * Includes packageName, location, and price in the request body.
	 *
	 * @param travelPackageDto the travel package details
	 * @return the created travel package
	 */
	@PostMapping
	public ResponseEntity<TravelPackageDto> createPackage( @RequestBody @Valid TravelPackageDto travelPackageDto) {

		log.info("Received request to create a new travel package with name: '{}' and location: '{}'", travelPackageDto.getPackageName(), travelPackageDto.getLocation()); // Log location
		return new ResponseEntity<>(packageService.createPackage(travelPackageDto),HttpStatus.CREATED);

	}


	/**
	 * Retrieves all travel packages with pagination and sorting.
	 *
	 * @param pageNo   the page number
	 * @param pageSize the page size
	 * @param sortBy   the field to sort by
	 * @param sortDir  the sort direction (asc/desc)
	 * @return paginated list of travel packages
	 */
	@GetMapping
	public ResponseEntity<TravelPackageResponse> getAllPackages(
			@RequestParam(value="pageNo",defaultValue=AppConstants.DEFAULT_PAGE_NUMBER,required=false) int pageNo,
			@RequestParam(value="pageSize",defaultValue=AppConstants.DEFAULT_PAGE_SIZE,required=false) int pageSize,
			@RequestParam(value="sortBy", defaultValue=AppConstants.DEFAULT_SORT_BY,required = false) String sortBy,
			@RequestParam(value="sortDir", defaultValue=AppConstants.DEFAULT_SORT_DIR,required = false) String sortDir

	){
		log.info("Fetching all travel packages - page: {}, size: {}, sortBy: {}, sortDir: {}",pageNo, pageSize, sortBy, sortDir);
		return new ResponseEntity<>(packageService.getAllPackages(pageNo,pageSize,sortBy,sortDir),HttpStatus.OK);

	}


	/**
	 * Retrieves a travel package by its ID.
	 *
	 * @param id the package ID
	 * @return the travel package
	 */
	@GetMapping("/{id}")
	public ResponseEntity<TravelPackageDto> getPackageById(@PathVariable(name="id") Long id) {

		log.info("Fetching travel package with ID: {}", id);

		return ResponseEntity.ok(packageService.getPackageById(id));
	}


	/**
	 * Updates a travel package by its ID.
	 * The request body should include the updated package details, including location.
	 *
	 * @param id               the package ID
	 * @param travelPackageDto the updated package details
	 * @return the updated travel package
	 */
	@PutMapping("/{id}")
	public ResponseEntity<TravelPackageDto> updatePackageById(@PathVariable(name="id") Long id,@RequestBody @Valid TravelPackageDto travelPackageDto) {

		log.info("Updating travel package with ID: {}", id);

		TravelPackageDto response = packageService.updatePackageById(id, travelPackageDto);
		return ResponseEntity.ok(response);
	}

	/**
	 * Retrieves the total count of travel packages.
	 *
	 * @return the total count of packages
	 */
	@GetMapping("/total") // NEW Endpoint
	public ResponseEntity<Long> getTotalPackages() {
		log.info("Received request to get total number of travel packages");
		Long totalCount = packageService.getTotalPackageCount();
		return ResponseEntity.ok(totalCount);
	}

	/**
	 * Deletes a travel package by its ID.
	 *
	 * @param id the package ID
	 * @return a response indicating success or failure
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePackageById(@PathVariable(name = "id") Long id) {
		log.info("Received request to delete travel package with ID: {}", id);
		packageService.deletePackageById(id);
		return new ResponseEntity<>("Travel Package deleted successfully!", HttpStatus.OK);
	}
}