package com.example.flightbooking.controller;

import com.example.flightbooking.model.Flight;
import com.example.flightbooking.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);

    @Autowired
    private FlightService flightService;

    @PostMapping
    @Operation(summary = "Add Flight")
    public Flight addFlight(@RequestBody Flight flight, @RequestParam String role) {
        logger.info("Received request to add flight");
        return flightService.addFlight(flight, role);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Flight")
    public Flight updateFlight(@PathVariable int id, @RequestBody Flight updated, @RequestParam String role) {
        logger.info("Received request to update flight with ID: {}", id);
        return flightService.updateFlight(id, updated, role);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Flight")
    public void deleteFlight(@PathVariable int id, @RequestParam String role) {
        logger.info("Received request to delete flight with ID: {}", id);
        flightService.deleteFlight(id, role);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Flight by ID")
    public Flight getFlightById(@PathVariable int id) {
        logger.info("Fetching flight with ID: {}", id);
        return flightService.getFlight(id);
    }

    @GetMapping
    @Operation(summary = "Get All Flights")
    public List<Flight> getAllFlights() {
        logger.info("Fetching all flights");
        return flightService.getAllFlights();
    }

    @GetMapping("/search")
    @Operation(summary = "Search Flights by Route")
    public List<Flight> searchFlights(@RequestParam String departure, @RequestParam String arrival) {
        logger.info("Searching flights from {} to {}", departure, arrival);
        return flightService.searchFlights(departure, arrival);
    }

    @GetMapping("/airline")
    @Operation(summary = "Search Flights by Airline")
    public List<Flight> searchByAirline(@RequestParam String airline) {
        logger.info("Searching flights by airline: {}", airline);
        return flightService.searchByAirline(airline);
    }

    @GetMapping("/departure-time")
    @Operation(summary = "Search Flights by Departure Time")
    public List<Flight> searchByDepartureTime(@RequestParam String departureTime) {
        logger.info("Searching flights by departure time: {}", departureTime);
        return flightService.searchByDepartureTime(departureTime);
    }

    @GetMapping("/duration")
    @Operation(summary = "Search Flights by Duration")
    public List<Flight> searchByDuration(@RequestParam long minMinutes, @RequestParam long maxMinutes) {
        logger.info("Searching flights with duration between {} and {} minutes", minMinutes, maxMinutes);
        return flightService.searchByDuration(minMinutes, maxMinutes);
    }
}