package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.constants.NotificationCategory;
import ar.edu.itba.pod.interfaces.NotificationCallbackHandler;
import ar.edu.itba.pod.interfaces.NotificationServicePrivateInterface;
import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.models.Flight;
import ar.edu.itba.pod.models.Ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
public class NotificationService implements NotificationServicePrivateInterface {

    private static NotificationService instance;

    private final FlightsAdminService flightsAdminService;

    private final Map<String, Map<String, NotificationCallbackHandler>> subscribedMap = new HashMap<>();

    private final static Logger logger = LoggerFactory.getLogger(NotificationService.class);

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
        subscribedMap.get(flightNumber).putIfAbsent(name, handler);
        newNotification(flightNumber, name, NotificationCategory.SUBSCRIBED);
    }

    public void newNotification(String flightNumber, String name, NotificationCategory notificationCategory) throws RemoteException {
        if (subscribedMap.containsKey(flightNumber)) {
            if (subscribedMap.get(flightNumber).containsKey(name)) {
                Ticket ticket = this.flightsAdminService.getFlight(flightNumber).getPassengerTicket(name);
                switch (notificationCategory) {
                    case SUBSCRIBED:
                        subscribedMap.get(flightNumber).get(name).subscribedNotification(logger, flightNumber,
                                flightsAdminService.getFlight(ticket.getFlightCode()).getDestination());
                        break;
                    case FLIGHT_CONFIRMED:
                        subscribedMap.get(flightNumber).get(name).flightConfirmedNotification(logger, flightNumber,
                                flightsAdminService.getFlight(ticket.getFlightCode()).getDestination(),
                                ticket.getSeatCategory().getMessage(), ticket.getSeat().getPlace());
                        break;
                    case FLIGHT_CANCELLED:
                        subscribedMap.get(flightNumber).get(name).flightCancelledNotification(logger, flightNumber,
                                flightsAdminService.getFlight(ticket.getFlightCode()).getDestination(),
                                ticket.getSeatCategory().getMessage(), ticket.getSeat().getPlace());
                        break;
                    case ASSIGNED_SEAT:
                        subscribedMap.get(flightNumber).get(name).assignedSeatNotification(logger, flightNumber,
                                flightsAdminService.getFlight(ticket.getFlightCode()).getDestination(),
                                ticket.getSeatCategory().getMessage(), ticket.getSeat().getPlace());
                        break;
                }
            }
        }
    }

    public void newNotification(String flightNumber, String name, Ticket oldTicket, NotificationCategory notificationCategory) throws RemoteException {
        if (subscribedMap.containsKey(flightNumber)) {
            if (subscribedMap.get(flightNumber).containsKey(name)) {
                Ticket ticket = this.flightsAdminService.getFlight(flightNumber).getPassengerTicket(name);
                switch (notificationCategory) {
                    case CHANGED_SEAT:
                        subscribedMap.get(flightNumber).get(name).changedSeatNotification(logger, flightNumber,
                                flightsAdminService.getFlight(ticket.getFlightCode()).getDestination(),
                                ticket.getSeatCategory().getMessage(), ticket.getSeat().getPlace(),
                                oldTicket.getSeatCategory().getMessage(), oldTicket.getSeat().getPlace());
                        break;
                    case CHANGED_TICKET:
                        subscribedMap.get(flightNumber).get(name).changedTicketNotification(logger, flightNumber,
                                flightsAdminService.getFlight(ticket.getFlightCode()).getDestination(),
                                ticket.getSeatCategory().getMessage(), ticket.getSeat().getPlace(),
                                oldTicket.getFlightCode(), flightsAdminService.getFlight(oldTicket.getFlightCode()).getDestination());
                        break;
                }
            }
        }
    }
}
