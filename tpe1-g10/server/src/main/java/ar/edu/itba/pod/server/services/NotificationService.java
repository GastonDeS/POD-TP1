package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.constants.NotificationCategory;
import ar.edu.itba.pod.interfaces.NotificationCallbackHandler;
import ar.edu.itba.pod.interfaces.NotificationServiceInterface;
import ar.edu.itba.pod.models.NotificationCallbackHandlerImpl;
import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.server.models.Flight;
import ar.edu.itba.pod.server.models.Ticket;
import ar.edu.itba.pod.server.models.Seat;

import java.rmi.RemoteException;
import java.util.*;

public class NotificationService implements NotificationServiceInterface {

    private static NotificationService instance;

    private FlightsAdminService flightsAdminService;

    private final Map<String, Map<String, List<NotificationCallbackHandler>>> subscribedMap = new HashMap<>();

    public NotificationService() {
        this.flightsAdminService = FlightsAdminService.getInstance();
    }

    public static NotificationService getInstance() {
        if (NotificationService.instance == null) {
            NotificationService.instance = new NotificationService();
        }
        return NotificationService.instance;
    }

    public void subscribe(String flightNumber, String name, NotificationCallbackHandler handler) throws RemoteException {
        Flight flight;
        flight = this.flightsAdminService.getFlight(flightNumber);
        if (flight.getPassengerTicket(name) == null) {
            throw new RemoteException("Error: no ticket found for passenger " + name);
        }
        if (flight.getStatus() == FlightStatus.CONFIRMED) {
            throw new RemoteException("Error: flight with code " + flightNumber + " is already confirmed");
        }
        subscribedMap.putIfAbsent(flightNumber, new HashMap<>());
        subscribedMap.get(flightNumber).putIfAbsent(name, new ArrayList<>());
        subscribedMap.get(flightNumber).get(name).add(new NotificationCallbackHandlerImpl());
        newNotification(flightNumber, name, NotificationCategory.SUBSCRIBED);
    }

    public void newNotification(String flightNumber, String name, NotificationCategory notificationCategory) throws RemoteException {
        if (subscribedMap.containsKey(flightNumber)) {
            if (subscribedMap.get(flightNumber).containsKey(name)) {
                Ticket ticket = this.flightsAdminService.getFlight(flightNumber).getPassengerTicket(name);
                switch (notificationCategory) {
                    case SUBSCRIBED:
                        for (NotificationCallbackHandler handler : subscribedMap.get(flightNumber).get(name)) {
                            handler.subscribedNotification(flightNumber,
                                    flightsAdminService.getFlight(ticket.getFlightCode()).getDestination());
                        }
                        break;
                    case ASSIGNED_SEAT:
                        for (NotificationCallbackHandler handler : subscribedMap.get(flightNumber).get(name)) {
                            handler.assignedSeatNotification(flightNumber,
                                    flightsAdminService.getFlight(ticket.getFlightCode()).getDestination(),
                                    ticket.getSeatCategory().getMessage(), ticket.getSeat().getPlace());
                        }
                        break;
                }
            }
        }
    }

    public void newNotificationChangeTicket(String flightNumber, String name, String oldFlightNumber, String oldDestination) throws RemoteException {
        if (subscribedMap.containsKey(oldFlightNumber)) {
            if (subscribedMap.get(oldFlightNumber).containsKey(name)) {
                Ticket ticket = this.flightsAdminService.getFlight(flightNumber).getPassengerTicket(name);
                for (NotificationCallbackHandler handler : subscribedMap.get(oldFlightNumber).get(name)) {
                    handler.changedTicketNotification(flightNumber,
                            flightsAdminService.getFlight(ticket.getFlightCode()).getDestination(), oldFlightNumber, oldDestination);
                }
                subscribedMap.putIfAbsent(flightNumber, new HashMap<>());
                subscribedMap.get(flightNumber).putIfAbsent(name, subscribedMap.get(oldFlightNumber).get(name));
                subscribedMap.get(oldFlightNumber).remove(name);
            }
        }
    }

    public void newNotificationChangeSeat(String flightNumber, String name, String oldSeatCategory, String oldPlace) throws RemoteException {
        if (subscribedMap.containsKey(flightNumber)) {
            if (subscribedMap.get(flightNumber).containsKey(name)) {
                Ticket ticket = this.flightsAdminService.getFlight(flightNumber).getPassengerTicket(name);
                for (NotificationCallbackHandler handler : subscribedMap.get(flightNumber).get(name)) {
                    handler.changedSeatNotification(flightNumber,
                            flightsAdminService.getFlight(ticket.getFlightCode()).getDestination(),
                            ticket.getSeatCategory().getMessage(), ticket.getSeat().getPlace(),
                            oldSeatCategory, oldPlace);
                }
            }
        }
    }

    public void newNotification(String flightNumber, List<Ticket> ticketList, NotificationCategory notificationCategory) throws RemoteException {
        if (subscribedMap.containsKey(flightNumber)) {
            for (Ticket ticket : ticketList) {
                if (subscribedMap.get(flightNumber).containsKey(ticket.getName())) {
                    String name = ticket.getName();
                    Optional<Seat> seat = Optional.ofNullable(ticket.getSeat());
                    switch (notificationCategory) {
                        case FLIGHT_CONFIRMED:
                            for (NotificationCallbackHandler handler : subscribedMap.get(flightNumber).get(name)) {
                                handler.flightCancelledNotification(flightNumber,
                                        flightsAdminService.getFlight(ticket.getFlightCode()).getDestination(),
                                        ticket.getSeatCategory().getMessage(), seat.map(Seat::getPlace).orElse(null));
                                handler.finish();
                            }
                            subscribedMap.get(flightNumber).remove(name);
                            break;
                        case FLIGHT_CANCELLED:
                            for (NotificationCallbackHandler handler : subscribedMap.get(flightNumber).get(name)) {
                                handler.flightCancelledNotification(flightNumber,
                                        flightsAdminService.getFlight(ticket.getFlightCode()).getDestination(),
                                        ticket.getSeatCategory().getMessage(), seat.map(Seat::getPlace).orElse(null));
                            }
                            break;
                    }
                }
            }
            if (subscribedMap.get(flightNumber).isEmpty()) {
                subscribedMap.remove(flightNumber);
            }
        }
    }
}
