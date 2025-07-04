package com.cts.travelpackage.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cts.travelpackage.dto.TravelPackageDto;
import com.cts.travelpackage.dto.TravelPackageResponse;
import com.cts.travelpackage.entity.TravelPackage;
import com.cts.travelpackage.exception.BadRequestException;
import com.cts.travelpackage.exception.ResourceNotFoundException;
import com.cts.travelpackage.repository.PackageRepository;
import com.cts.travelpackage.service.PackageService;
import com.cts.travelpackage.service.impl.ValidationService; // Corrected import to the service package


@Service
public class PackageServiceImpl implements PackageService{

	PackageRepository packageRepository;
	ModelMapper mapper;
	private static final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);

	private final ValidationService validationService;


	public PackageServiceImpl(PackageRepository packageRepository,ModelMapper mapper,ValidationService validationService) {
		this.packageRepository = packageRepository;
		this.mapper = mapper;
		this.validationService = validationService;
	}


	/**
	 * Converts entity to DTO.
	 */
	private TravelPackageDto mapToDto(TravelPackage travelPackage) {
		return mapper.map(travelPackage, TravelPackageDto.class);
	}



	/**
	 * Converts DTO to entity.
	 */
	private TravelPackage mapToEntity(TravelPackageDto travelPackageDto) {
		return mapper.map(travelPackageDto, TravelPackage.class);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public TravelPackageDto createPackage( TravelPackageDto travelPackageDto) {

		if (travelPackageDto == null) {
			throw new BadRequestException("Package data cannot be null");
		}

		validationService.validate(travelPackageDto); // This will now validate the 'location' field as well

		TravelPackage travelPackage = mapToEntity(travelPackageDto);
		TravelPackage newTravelPackage = packageRepository.save(travelPackage);
		TravelPackageDto response = mapToDto(newTravelPackage);
		logger.info("Created new Package: {} at location: {}",newTravelPackage.getPackageId(), newTravelPackage.getLocation()); // Log location
		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TravelPackageResponse getAllPackages(int pageNo,int pageSize,String sortBy,String sortDir) {

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

		Page<TravelPackage> travelPackages = packageRepository.findAll(pageable);

		List<TravelPackage> listOfPackages = travelPackages.getContent();

		List<TravelPackageDto> content = listOfPackages.stream().map(tarvelPackage -> mapToDto(tarvelPackage)).collect(Collectors.toList());

		TravelPackageResponse response = new TravelPackageResponse();

		response.setContent(content);
		response.setTotalPages(travelPackages.getTotalPages());
		response.setTotalElements(travelPackages.getTotalElements());
		response.setPageNo(travelPackages.getNumber());
		response.setPageSize(travelPackages.getSize());
		response.setFirst(travelPackages.isFirst());
		response.setLast(travelPackages.isLast());
		logger.info("Fetched {} packages", content.size());
		return response;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public TravelPackageDto getPackageById(Long id) {
		TravelPackage travelpackage = packageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Travel Package","id",id));

		logger.info("Retrieved package with ID: {}", id);
		return mapToDto(travelpackage);
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public TravelPackageDto updatePackageById(Long id,TravelPackageDto travelPackageDto) {

		TravelPackage travelPackage = packageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("TravelPackage", "id", id));

		// Update existing fields
		travelPackage.setPackageName(travelPackageDto.getPackageName());
		travelPackage.setIncludedFlightIds(travelPackageDto.getIncludedFlightIds());
		travelPackage.setIncludedHotelIds(travelPackageDto.getIncludedHotelIds());
		travelPackage.setActivities(travelPackageDto.getActivities());
		travelPackage.setPrice(travelPackageDto.getPrice());
		travelPackage.setLocation(travelPackageDto.getLocation()); // NEW: Update location field

		TravelPackage updatedPackage = packageRepository.save(travelPackage);
		logger.info("Updated package with ID: {}", id);
		return mapToDto(updatedPackage);
	}

	/**
	 * NEW: Method to get the total count of travel packages.
	 */
	@Override // This @Override was missing in your provided code
	public Long getTotalPackageCount() {
		long totalCount = packageRepository.count();
		logger.info("Total number of packages: {}", totalCount);
		return totalCount;
	}

	/**
	 * Deletes a travel package by its ID.
	 * {@inheritDoc}
	 */
	@Override
	public void deletePackageById(Long id) {
		TravelPackage travelPackage = packageRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("TravelPackage", "id", id));
		packageRepository.delete(travelPackage);
		logger.info("Deleted package with ID: {}", id);
	}
}