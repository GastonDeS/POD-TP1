package api.src.main.java.ar.edu.itba.pod.services;

import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;
import api.src.main.java.ar.edu.itba.pod.constants.NotificationCategory;
import api.src.main.java.ar.edu.itba.pod.interfaces.NotificationCallbackHandler;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Seat;
import api.src.main.java.ar.edu.itba.pod.models.Ticket;
import api.src.main.java.ar.edu.itba.pod.utils.Pair;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class NotificationService {

    private static NotificationService instance;

    private FlightsAdminService flightsAdminService;

    private Map<String, Map<String, NotificationCallbackHandler>> subscribedMap = new HashMap<>();

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
        try {
            flight = this.flightsAdminService.getFlight(flightNumber);
        } catch (RemoteException e) {
            return;
        }
//        Ticket ticket;
//        try {
//            ticket = flight.getPassengerTicket(name);
//        } catch (RemoteException e) {
//            return;
//        }
        if (flight.getStatus() == FlightStatus.CONFIRMED) {
            return;
        }
        subscribedMap.putIfAbsent(flightNumber, new HashMap<>());
        subscribedMap.get(flightNumber).putIfAbsent(name, handler);
        newNotification(flightNumber, name, NotificationCategory.SUBSCRIBED);
    }

    public void newNotification(String flightNumber, String name, NotificationCategory notificationCategory) throws RemoteException {
        if (subscribedMap.containsKey(flightNumber)) {
            if (subscribedMap.get(flightNumber).containsKey(name)) {
                Ticket ticket = this.flightsAdminService.getFlight(flightNumber).getPassengerTicket(name);
                subscribedMap.get(flightNumber).get(name).sendNotification(flightNumber, ticket.getFlight().getDestination(),
                        ticket.getSeatCategory(), ticket.getSeat().getPlace(), notificationCategory.getMessage());
            }
        }
    }

    public void newNotificationUpdate(String flightNumber, String name, Ticket oldTicket, NotificationCategory notificationCategory) throws RemoteException {
        if (subscribedMap.containsKey(flightNumber)) {
            if (subscribedMap.get(flightNumber).containsKey(name)) {
                Ticket ticket = this.flightsAdminService.getFlight(flightNumber).getPassengerTicket(name);
                subscribedMap.get(flightNumber).get(name).sendNotificationUpdate(flightNumber, ticket.getFlight().getDestination(),
                        ticket.getSeatCategory(), ticket.getSeat().getPlace(), oldTicket.getSeatCategory(),
                        oldTicket.getSeat().getPlace(), notificationCategory.getMessage());
            }
        }
    }
}
