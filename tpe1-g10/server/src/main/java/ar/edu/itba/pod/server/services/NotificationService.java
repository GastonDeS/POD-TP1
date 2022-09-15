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
import ar.edu.itba.pod.models.TicketDto;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NotificationService implements NotificationServiceInterface {

    private static NotificationService instance;
    private final FlightsAdminService flightsAdminService;
    private final Map<String, Map<String, List<NotificationCallbackHandler>>> subscribers = new HashMap<>();
    private final ExecutorService executor;
    private final ReentrantReadWriteLock suscribersLock;

    public NotificationService() {
        this.flightsAdminService = FlightsAdminService.getInstance();
        this.executor = Executors.newFixedThreadPool(5);
        this.suscribersLock = new ReentrantReadWriteLock(true);
    }

    public boolean awaitTermination() throws RemoteException {
        try {
            executor.shutdown();
            return executor.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            throw new RemoteException("InterruptedException: " + ex.getMessage());
        }
    }

    public static NotificationService getInstance() {
        if (NotificationService.instance == null) {
            NotificationService.instance = new NotificationService();
        }
        return NotificationService.instance;
    }

    public void subscribe(String flightNumber, String name, NotificationCallbackHandler handler) throws RemoteException {
        Flight flight = this.flightsAdminService.getFlight(flightNumber);
        if (flight.getPassengerTicket(name) == null) {
            throw new RemoteException("Error: no ticket found for passenger " + name);
        }
        if (flight.getStatus() == FlightStatus.CONFIRMED) {
            throw new RemoteException("Error: flight with code " + flightNumber + " is already confirmed");
        }
        try {
            suscribersLock.writeLock().lock();
            subscribers.putIfAbsent(flightNumber, new HashMap<>());
            List<NotificationCallbackHandler> handlers = new ArrayList<>();
            if (subscribers.get(flightNumber).containsKey(name)) {
                handlers = subscribers.get(flightNumber).get(name);
            }
            handlers.add(handler);
            subscribers.get(flightNumber).put(name, handlers);
        } finally {
            suscribersLock.writeLock().unlock();
        }

        newNotification(flightNumber, name, NotificationCategory.SUBSCRIBED);
    }

    public void newNotification(String flightNumber, String name, NotificationCategory notificationCategory) throws RemoteException {
        try {
            suscribersLock.readLock().lock();
            if (subscribers.containsKey(flightNumber)) {
                if (subscribers.get(flightNumber).containsKey(name)) {
                    Ticket ticket = this.flightsAdminService.getFlight(flightNumber).getPassengerTicket(name);
                    switch (notificationCategory) {
                        case SUBSCRIBED:
                            sendSubscribedNotification(flightNumber, name);
                            break;
                        case ASSIGNED_SEAT:
                            sendSeatAssignedNotification(flightNumber, name, ticket);
                            break;
                    }
                }
            }
        } finally {
            suscribersLock.readLock().unlock();
        }
    }

    public void newNotificationChangeTicket(String flightNumber, String name, String oldFlightNumber, String destination) throws RemoteException {
        try {
            suscribersLock.writeLock().lock();
            if (subscribers.containsKey(oldFlightNumber)) {
                if (subscribers.get(oldFlightNumber).containsKey(name)) {
                    sendChangedTicketNotification(flightNumber, name, oldFlightNumber, destination);
                    subscribers.putIfAbsent(flightNumber, new HashMap<>());
                    subscribers.get(flightNumber).putIfAbsent(name, subscribers.get(oldFlightNumber).get(name));
                    subscribers.get(oldFlightNumber).remove(name);
                }
            }
        } finally {
            suscribersLock.writeLock().unlock();
        }
    }

    public void newNotificationChangeSeat(String flightNumber, String name, String oldSeatCategory, String oldPlace) throws RemoteException {
        try {
            suscribersLock.readLock().lock();
            if (subscribers.containsKey(flightNumber)) {
                if (subscribers.get(flightNumber).containsKey(name)) {
                    Ticket ticket = this.flightsAdminService.getFlight(flightNumber).getPassengerTicket(name);
                    sendChangedSeatNotification(flightNumber, ticket, oldSeatCategory, oldPlace);
                }
            }
        } finally {
            suscribersLock.readLock().unlock();
        }
    }

    public void newNotification(String flightNumber, List<TicketDto> ticketList, NotificationCategory notificationCategory) throws RemoteException {
        try {
            suscribersLock.readLock().lock();
            if (subscribers.containsKey(flightNumber)) {
                for (TicketDto ticket : ticketList) {
                    if (subscribers.get(flightNumber).containsKey(ticket.getName())) {
                        String seat = ticket.getSeatPlace().isPresent() ? ticket.getSeatPlace().get() : null;
                        switch (notificationCategory) {
                            case FLIGHT_CONFIRMED:
                                sendFlightConfirmedNotification(flightNumber, ticket, seat);
                                break;
                            case FLIGHT_CANCELLED:
                                sendFlightCancelledNotification(flightNumber, ticket, seat);
                                break;
                        }
                    }
                }
            }
        } finally {
            suscribersLock.readLock().unlock();
        }
    }

    private void sendSubscribedNotification(String flightNumber, String name) {
        executor.submit(() -> {
            try {
                List<NotificationCallbackHandler> toNotify;
                try {
                    suscribersLock.readLock().lock();
                    toNotify = subscribers.get(flightNumber).get(name);
                } finally {
                    suscribersLock.readLock().unlock();
                }
                for (NotificationCallbackHandler handler : toNotify) {
                    handler.subscribedNotification(flightNumber, flightsAdminService.getFlight(flightNumber).getDestination());
                }
            } catch (RemoteException ignored) {
            }
        });
    }

    private void sendSeatAssignedNotification(String flightNumber, String name, Ticket ticket) {
        executor.submit(() -> {
            try {
                List<NotificationCallbackHandler> toNotify;
                try {
                    suscribersLock.readLock().lock();
                    toNotify = subscribers.get(flightNumber).get(name);
                } finally {
                    suscribersLock.readLock().unlock();
                }
                for (NotificationCallbackHandler handler : toNotify) {
                    handler.assignedSeatNotification(flightNumber,
                            flightsAdminService.getFlight(flightNumber).getDestination(),
                            ticket.getSeatCategory().getMessage(), ticket.getSeat().getPlace());
                }
            } catch (RemoteException ignored) {
            }
        });
    }

    private void sendChangedTicketNotification(String flightNumber, String name, String oldFlightNumber, String destination) {
        executor.submit(() -> {
            try {
                List<NotificationCallbackHandler> toNotify;
                try {
                    suscribersLock.readLock().lock();
                    toNotify = subscribers.get(flightNumber).get(name);
                } finally {
                    suscribersLock.readLock().unlock();
                }
                for (NotificationCallbackHandler handler : toNotify) {
                    handler.changedTicketNotification(flightNumber,
                            destination, oldFlightNumber, destination);
                }
            } catch (RemoteException ignored) {
            }
        });
    }

    private void sendChangedSeatNotification(String flightNumber, Ticket ticket, String oldSeatCategory, String oldPlace) {
        executor.submit(() -> {
            try {
                List<NotificationCallbackHandler> toNotify;
                try {
                    suscribersLock.readLock().lock();
                    toNotify = subscribers.get(flightNumber).get(ticket.getName());
                } finally {
                    suscribersLock.readLock().unlock();
                }
                for (NotificationCallbackHandler handler : toNotify) {
                    handler.changedSeatNotification(flightNumber,
                            flightsAdminService.getFlight(flightNumber).getDestination(),
                            ticket.getSeatCategory().getMessage(), ticket.getSeat().getPlace(),
                            oldSeatCategory, oldPlace);
                }
            } catch (RemoteException ignored) {
            }
        });
    }

    private void sendFlightConfirmedNotification(String flightNumber, TicketDto ticket, String place) {
        executor.submit(() -> {
            try {
                List<NotificationCallbackHandler> toNotify;
                try {
                    suscribersLock.readLock().lock();
                    toNotify = subscribers.get(flightNumber).get(ticket.getName());
                } finally {
                    suscribersLock.readLock().unlock();
                }
                for (NotificationCallbackHandler handler : toNotify) {
                    handler.flightConfirmedNotification(flightNumber,
                            flightsAdminService.getFlight(flightNumber).getDestination(),
                            ticket.getSeatCategory().getMessage(), place);
                    handler.finish();
                }
                try {
                    suscribersLock.writeLock().lock();
                    subscribers.get(flightNumber).remove(ticket.getName());
                    if (subscribers.get(flightNumber).isEmpty()) {
                        subscribers.remove(flightNumber);
                    }
                } finally {
                    suscribersLock.writeLock().unlock();
                }

            } catch (RemoteException ignored) {
            }
        });
    }

    private void sendFlightCancelledNotification(String flightNumber, TicketDto ticket, String place) {
        executor.submit(() -> {
            try {
                List<NotificationCallbackHandler> toNotify;
                try {
                    suscribersLock.readLock().lock();
                    toNotify = subscribers.get(flightNumber).get(ticket.getName());
                } finally {
                    suscribersLock.readLock().unlock();
                }
                for (NotificationCallbackHandler handler : toNotify) {
                    handler.flightCancelledNotification(flightNumber,
                            flightsAdminService.getFlight(flightNumber).getDestination(),
                            ticket.getSeatCategory().getMessage(), place);
                }
            } catch (RemoteException ignored) {
            }
        });
    }
}
