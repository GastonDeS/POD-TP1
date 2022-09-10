package api.src.main.java.ar.edu.itba.pod.services;

import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;
import api.src.main.java.ar.edu.itba.pod.interfaces.SeatsAssignmentServiceInterface;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Seat;
import api.src.main.java.ar.edu.itba.pod.models.Ticket;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class SeatsAssignmentService implements SeatsAssignmentServiceInterface {
    private static SeatsAssignmentService instance;
    private final FlightsAdminService flightsAdminService;

    public SeatsAssignmentService() {
        this.flightsAdminService = FlightsAdminService.getInstance();
    }

    public static SeatsAssignmentService getInstance() {
        if (SeatsAssignmentService.instance == null) {
            SeatsAssignmentService.instance = new SeatsAssignmentService();
        }
        return SeatsAssignmentService.instance;
    }

    public boolean checkEmptySeat(String flightCode, int row, String column) throws RemoteException {
        return flightsAdminService.getFlight(flightCode).getSeat(row, column).isAvailable();
    }

    public void assignSeat(String flightCode, String name, int row, String column) throws RemoteException {
        assignOrChangeSeat(flightCode, name, row, column, false);
    }

    public void changeSeat(String flightCode, String name, int row, String column) throws RemoteException {
        assignOrChangeSeat(flightCode, name, row, column, true);
    }

    public Map<String, List<Seat>> getAvailableFlights(String flightCode, String name) throws RemoteException {
        Flight currentFlight = flightsAdminService.getFlight(flightCode);
        Ticket ticket = currentFlight.getPassengerTicket(name);
        if (currentFlight.getStatus().equals(FlightStatus.CONFIRMED)) throw new RemoteException("Error: flight is already confirmed");
        List<Flight> similarFlights = flightsAdminService.getFlights().values()
                .stream()
                .filter(f -> f.getDestination().equals(currentFlight.getDestination())
                        && f.getStatus().equals(FlightStatus.CONFIRMED)
                        && !f.getCode().equals(flightCode))
                .collect(Collectors.toList());
        Map<String, List<Seat>> availableFlights = new HashMap<>();
        similarFlights.forEach(flight -> {
            List<Seat> availableSeats = flight.getAvailableSeats()
                    .stream()
                    .filter(s -> s.getSeatCategory().ordinal() >= ticket.getSeatCategory().ordinal())
                    .collect(Collectors.toList());
            availableFlights.put(flight.getCode(), availableSeats);
        });
        return availableFlights;
    }

    public void changeTicket(String name, String current, String alternative) throws RemoteException {
        Ticket ticket = flightsAdminService.getFlight(current).getPassengerTicket(name);
        Flight currentFlight = flightsAdminService.getFlight(current);
        Flight alternativeFlight = flightsAdminService.getFlight(alternative);
        currentFlight.removeTicketFromFlight(ticket);
        ticket.setSeat(null);
        ticket.setFlight(alternativeFlight);
        alternativeFlight.addTicketToFlight(ticket);
    }

    private void assignOrChangeSeat(String flightCode, String name, int row, String column, boolean isChange) throws RemoteException {
        Flight flight = flightsAdminService.getFlight(flightCode);
        Ticket ticket = flight.getPassengerTicket(name);
        Seat seat = flight.getSeat(row, column);
        if (!flight.getStatus().equals(FlightStatus.PENDING)) throw new RemoteException("Error: flight is " + flight.getStatus().name());
        if (!seat.isAvailable()) throw new RemoteException("Error: seat " + row + column + " is not available");
        if (seat.getSeatCategory().ordinal() < ticket.getSeatCategory().ordinal())
            throw new RemoteException("Error: seat " + row + column + " is not available for your category");
        if (isChange && ticket.getSeat() != null) {
            ticket.getSeat().setAvailable(true, '*');
        }
        seat.setAvailable(false, name.charAt(0));
        ticket.setSeat(seat);
    }

}
