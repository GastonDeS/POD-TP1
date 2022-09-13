package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.constants.NotificationCategory;
import ar.edu.itba.pod.interfaces.SeatsAssignmentServiceInterface;
import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.server.services.NotificationService;
import ar.edu.itba.pod.models.AvailableFlightDto;
import ar.edu.itba.pod.server.models.Flight;
import ar.edu.itba.pod.server.models.Seat;
import ar.edu.itba.pod.server.models.Ticket;
import ar.edu.itba.pod.constants.SeatCategory;

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

    public AvailableFlightDto getAvailableFlights(String flightCode, String name) throws RemoteException {
        Flight currentFlight = flightsAdminService.getFlight(flightCode);
        Ticket ticket = currentFlight.getPassengerTicket(name);
        if (currentFlight.getStatus().equals(FlightStatus.CONFIRMED)) throw new RemoteException("Error: flight is already confirmed");

        List<Flight> similarFlights = flightsAdminService.getFlights().values()
                .stream()
                .filter(f -> f.getDestination().equals(currentFlight.getDestination())
                        && !f.getStatus().equals(FlightStatus.CANCELLED)
                        && !f.getCode().equals(flightCode))
                .collect(Collectors.toList());


        Map<SeatCategory, Map<String, Long>> availableFlights = new HashMap<>();
        similarFlights.forEach(flight -> {
            for (SeatCategory s : SeatCategory.values()) {
                if (s.ordinal() >= ticket.getSeatCategory().ordinal()) {
                    long count = flight.getAvailableSeats().stream().filter(seat -> seat.getSeatCategory() == s).count();
                    if (count > 0) {
                        Map<String, Long> flights = availableFlights.getOrDefault(s, new HashMap<>());
                        flights.put(flight.getCode(), count);
                        availableFlights.put(s, flights);
                    }
                }
            }
        });
        return new AvailableFlightDto(currentFlight.getDestination(), availableFlights);
    }

    public void changeTicket(String name, String current, String alternative) throws RemoteException {
        Ticket ticket = flightsAdminService.getFlight(current).getPassengerTicket(name);
        Flight currentFlight = flightsAdminService.getFlight(current);
        Flight alternativeFlight = flightsAdminService.getFlight(alternative);

        currentFlight.swapTicket(ticket, alternativeFlight, ticket.getSeatCategory());

        // Notify changes
        notificationService.newNotificationChangeTicket(alternative, name, current, currentFlight.getDestination());
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
            notificationService.newNotificationChangeSeat(flightCode, name, ticket.getSeat().getSeatCategory().getMessage(), ticket.getSeat().getPlace());
            if (ticket.getSeat() != null) ticket.getSeat().setAvailable(true, '*');
        } else {
            notificationService.newNotification(flightCode, name, NotificationCategory.CHANGED_SEAT);
        }

        ticket.setSeatAndUpdateSeat(seat, false, name.charAt(0));
    }

}
