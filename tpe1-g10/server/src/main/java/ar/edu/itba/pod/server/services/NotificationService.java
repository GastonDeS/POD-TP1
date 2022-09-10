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
                String message = null;
                switch (notificationCategory) {
                    case SUBSCRIBED:
                        message =
                                "You are following flight " + flightNumber + " with destination " + flightsAdminService.getFlight(ticket.getFlightCode()).getDestination() + ".";
                        break;
                    case FLIGHT_CONFIRMED:
                        message = "Your flight " + flightNumber + " with destination " + flightsAdminService.getFlight(ticket.getFlightCode()).getDestination() + " was " +
                                "confirmed and your seat is " + ticket.getSeatCategory().getMessage() + " " + ticket.getSeat().getPlace();
                        break;
                    case FLIGHT_CANCELLED:
                        message = "Your flight " + flightNumber + " with destination " + flightsAdminService.getFlight(ticket.getFlightCode()).getDestination() + " was " +
                                "cancelled and your seat is " + ticket.getSeatCategory().getMessage() + " " + ticket.getSeat().getPlace();
                        break;
                    case ASSIGNED_SEAT:
                        message = "Your seat is " + ticket.getSeatCategory().getMessage() + " " + ticket.getSeat().getPlace() + " for " +
                                "flight " + flightNumber + " with destination " + flightsAdminService.getFlight(ticket.getFlightCode()).getDestination();
                        break;
                }
                subscribedMap.get(flightNumber).get(name).sendNotification(message, logger);
            }
        }
    }

    public void newNotification(String flightNumber, String name, Ticket oldTicket, NotificationCategory notificationCategory) throws RemoteException {
        if (subscribedMap.containsKey(flightNumber)) {
            if (subscribedMap.get(flightNumber).containsKey(name)) {
                Ticket ticket = this.flightsAdminService.getFlight(flightNumber).getPassengerTicket(name);
                String message = null;
                switch (notificationCategory) {
                    case CHANGED_SEAT:
                        message = "Your seat changed to " + ticket.getSeatCategory().getMessage() + " " + ticket.getSeat().getPlace() +
                                " from " + oldTicket.getSeatCategory().getMessage() + " " + oldTicket.getSeat().getPlace() + " for flight" +
                                " " + flightNumber + " with destination " + flightsAdminService.getFlight(ticket.getFlightCode()).getDestination();
                        break;
                    case CHANGED_TICKET:
                        message =
                                "Your ticket changed to flight " + flightNumber + " with destination " + flightsAdminService.getFlight(ticket.getFlightCode()).getDestination() + " from flight " + oldTicket.getFlightCode() + " with destination " + flightsAdminService.getFlight(oldTicket.getFlightCode()).getDestination();
                        break;
                }
                subscribedMap.get(flightNumber).get(name).sendNotification(message, logger);
            }
        }
    }
}
