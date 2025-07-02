package com.example.flightbooking.service;

import com.example.flightbooking.model.Flight;
import com.example.flightbooking.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightServiceImpl implements FlightService {

    private static final Logger logger = LoggerFactory.getLogger(FlightServiceImpl.class);

    @Autowired
    private FlightRepository flightRepo;

    private void ensureAdmin(String role) {
        if (!"Admin".equalsIgnoreCase(role)) {
            logger.warn("Access denied. Role '{}' attempted to manage flight details", role);
            throw new RuntimeException("Access denied: Only Admin can manage flight details");
        }
    }

    @Override
    public Flight addFlight(Flight flight, String createdByRole) {
        logger.info("Adding flight: {}", flight);
        ensureAdmin(createdByRole);
        flight.setCreatedAt(LocalDateTime.now());
        flight.setCreatedBy(createdByRole);
        Flight savedFlight = flightRepo.save(flight);
        logger.info("Flight added successfully: {}", savedFlight);
        return savedFlight;
    }

    @Override
    public Flight updateFlight(int id, Flight updated, String updaterRole) {
        logger.info("Updating flight with ID: {}", id);
        ensureAdmin(updaterRole);
        Flight f = flightRepo.findById(id).orElseThrow(() -> {
            logger.error("Flight not found with ID: {}", id);
            return new RuntimeException("Flight not found");
        });

        f.setDeparture(updated.getDeparture());
        f.setArrival(updated.getArrival());
        f.setAirline(updated.getAirline());
        f.setDepartureTime(updated.getDepartureTime());
        f.setArrivalTime(updated.getArrivalTime());
        f.setPrice(updated.getPrice());
        f.setAvailability(updated.isAvailability());
        f.setUpdatedAt(LocalDateTime.now());
        f.setUpdatedBy(updaterRole);

        Flight updatedFlight = flightRepo.save(f);
        logger.info("Flight updated successfully: {}", updatedFlight);
        return updatedFlight;
    }

    @Override
    public void deleteFlight(int id, String role) {
        logger.info("Deleting flight with ID: {}", id);
        ensureAdmin(role);
        Flight flight = flightRepo.findById(id).orElseThrow(() -> {
            logger.error("Flight not found with ID: {}", id);
            return new RuntimeException("Flight not found");
        });
        flightRepo.delete(flight);
        logger.info("Flight deleted successfully with ID: {}", id);
    }

    @Override
    public Flight getFlight(int id) {
        logger.info("Fetching flight with ID: {}", id);
        return flightRepo.findById(id).orElseThrow(() -> {
            logger.error("Flight not found with ID: {}", id);
            return new RuntimeException("Flight not found");
        });
    }

    @Override
    public List<Flight> searchFlights(String departure, String arrival) {
        logger.info("Searching flights from '{}' to '{}'", departure, arrival);
        List<Flight> flights = flightRepo.findByDepartureAndArrival(departure, arrival);
        if (flights.isEmpty()) {
            logger.warn("No flights found for route: {} -> {}", departure, arrival);
            throw new RuntimeException("No flights found for this route");
        }
        logger.info("Flights found for route: {} -> {}", departure, arrival);
        return flights;
    }

    @Override
    public List<Flight> getAllFlights() {
        logger.info("Fetching all flights");
        return flightRepo.findAll();
    }

    @Override
    public List<Flight> searchByAirline(String airline) {
        logger.info("Searching flights by airline: {}", airline);
        List<Flight> flights = flightRepo.findByAirlineIgnoreCase(airline);
        if (flights.isEmpty()) {
            logger.warn("No flights found for airline: {}", airline);
            throw new RuntimeException("No flights found for this airline");
        }
        return flights;
    }

    @Override
    public List<Flight> searchByDepartureTime(String departureTime) {
        logger.info("Searching flights by departure time: {}", departureTime);
        List<Flight> flights = flightRepo.findAll().stream()
                .filter(flight -> flight.getDepartureTime().toString().contains(departureTime))
                .collect(Collectors.toList());

        if (flights.isEmpty()) {
            logger.warn("No flights found for departure time: {}", departureTime);
            throw new RuntimeException("No flights found with this departure time");
        }
        return flights;
    }

    @Override
    public List<Flight> searchByDuration(long minMinutes, long maxMinutes) {
        logger.info("Searching flights with duration between {} and {} minutes", minMinutes, maxMinutes);
        List<Flight> flights = flightRepo.findAll().stream()
                .filter(flight -> {
                    Duration duration = Duration.between(flight.getDepartureTime(), flight.getArrivalTime());
                    long minutes = duration.toMinutes();
                    return minutes >= minMinutes && minutes <= maxMinutes;
                })
                .collect(Collectors.toList());

        if (flights.isEmpty()) {
            logger.warn("No flights found within duration {}-{} minutes", minMinutes, maxMinutes);
            throw new RuntimeException("No flights found with duration in given range");
        }
        return flights;
    }
}