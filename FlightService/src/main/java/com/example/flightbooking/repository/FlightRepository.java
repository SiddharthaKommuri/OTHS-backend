package com.example.flightbooking.repository;

import com.example.flightbooking.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
    List<Flight> findByAirlineIgnoreCase(String airline);
    List<Flight> findByDepartureIgnoreCaseAndArrivalIgnoreCase(String departure, String arrival);
    List<Flight> findByArrivalIgnoreCase(String arrival); // NEW: Method to search only by arrival
}