package api.src.main.java.ar.edu.itba.pod.services;

import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;
import api.src.main.java.ar.edu.itba.pod.constants.NotificationCategory;
import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;
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
    private final NotificationService notificationService;

    public SeatsAssignmentService() {
        this.flightsAdminService = FlightsAdminService.getInstance();
        this.notificationService = NotificationService.getInstance();
    }

    public static SeatsAssignmentService getInstance() {
        if (SeatsAssignmentService.instance == null) {
            SeatsAssignmentService.instance = new SeatsAssignmentService();
        }
        return SeatsAssignmentService.instance;
    }

    public String checkEmptySeat(String flightCode, int row, String column) throws RemoteException {
        Optional<Ticket> maybeTicket = flightsAdminService.getFlight(flightCode).getTicketFromSeat(row, column);
        return maybeTicket.map(Ticket::getName).orElse(null);
    }

    public void assignSeat(String flightCode, String name, int row, String column) throws RemoteException {
        assignOrChangeSeat(flightCode, name, row, column, false);
    }

    public void changeSeat(String flightCode, String name, int row, String column) throws RemoteException {
        assignOrChangeSeat(flightCode, name, row, column, true);
    }

    public Map<SeatCategory, Map<String, Long>> getAvailableFlights(String flightCode, String name) throws RemoteException {
        Flight currentFlight = flightsAdminService.getFlight(flightCode);
        Ticket ticket = currentFlight.getPassengerTicket(name);
        if (currentFlight.getStatus().equals(FlightStatus.CONFIRMED)) throw new RemoteException("Error: flight is already confirmed");
        List<Flight> similarFlights = flightsAdminService.getFlights().values()
                .stream()
                .filter(f -> f.getDestination().equals(currentFlight.getDestination())
                        && f.getStatus().equals(FlightStatus.CONFIRMED)
                        && !f.getCode().equals(flightCode))
                .collect(Collectors.toList());
        Map<SeatCategory, Map<String, Long>> availableFlights = new HashMap<>();
        similarFlights.forEach(flight -> {
            Map<SeatCategory, Long> seatsPerCategory = flight.getAvailableSeats()
                    .stream()
                    .filter(seat -> seat.getSeatCategory().ordinal() >= ticket.getSeatCategory().ordinal())
                    .collect(Collectors.groupingBy(Seat::getSeatCategory, Collectors.counting()));
            for (SeatCategory s : SeatCategory.values()) {
                Map<String, Long> flights = availableFlights.getOrDefault(s, new HashMap<>());
                flights.put(flight.getCode(), seatsPerCategory.getOrDefault(s, 0L));
                availableFlights.put(s, flights);
            }
        });
        return availableFlights;
    }

    public void changeTicket(String name, String current, String alternative) throws RemoteException {
        Ticket ticket = flightsAdminService.getFlight(current).getPassengerTicket(name);
        Flight currentFlight = flightsAdminService.getFlight(current);
        Flight alternativeFlight = flightsAdminService.getFlight(alternative);

        // Notify changes
        notificationService.newNotification(alternative, name, ticket, NotificationCategory.CHANGED_TICKET);

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

        if (isChange) {
            // Notify changes
            notificationService.newNotification(flightCode, name, ticket, NotificationCategory.CHANGED_SEAT);
            if (ticket.getSeat() != null) ticket.getSeat().setAvailable(true, '*');
        } else {
            // Notify changes
            notificationService.newNotification(flightCode, name, NotificationCategory.CHANGED_SEAT);
        }

        seat.setAvailable(false, name.charAt(0));
        ticket.setSeat(seat);
    }

}
