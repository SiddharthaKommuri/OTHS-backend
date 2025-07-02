package com.example.flightbooking.service;

import com.example.flightbooking.model.Flight;
import java.util.List;

public interface FlightService {

    Flight addFlight(Flight flight, String createdByRole);

    Flight updateFlight(int id, Flight updated, String updaterRole);

    void deleteFlight(int id, String role);

    Flight getFlight(int id);

    List<Flight> searchFlights(String departure, String arrival);


    List<Flight> getAllFlights();

    List<Flight> searchByAirline(String airline);

    List<Flight> searchByDepartureTime(String departureTime);

    List<Flight> searchByDuration(long minMinutes, long maxMinutes);
}