package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.models.ChangedTicketsDto;
import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.constants.NotificationCategory;
import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import ar.edu.itba.pod.server.services.NotificationService;
import ar.edu.itba.pod.server.models.Flight;
import ar.edu.itba.pod.server.models.Plane;
import ar.edu.itba.pod.server.models.Ticket;
import ar.edu.itba.pod.models.PlaneData;
import ar.edu.itba.pod.models.TicketDto;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class FlightsAdminService implements FlightAdminServiceInterface {
    private static FlightsAdminService instance;
    private final Map<String, Plane> planes;
    private final Map<String, Flight> flights;
    private final ReentrantReadWriteLock flightsLock;
    private final ReentrantReadWriteLock planesLock;
    private NotificationService notificationService;

    private FlightsAdminService() {
        this.planes = new HashMap<>();
        this.flights = new HashMap<>();
        this.flightsLock = new ReentrantReadWriteLock();
        this.planesLock = new ReentrantReadWriteLock();
    }

    public static FlightsAdminService getInstance() {
        if (FlightsAdminService.instance == null) {
            FlightsAdminService.instance = new FlightsAdminService();
        }
        return FlightsAdminService.instance;
    }

    private void init() {
        this.notificationService = NotificationService.getInstance();
    }

    // For test purposes
    // Its not synchronized because its only used for tests
    public void restart() {
        List<String> planeModels = new ArrayList<>(this.planes.keySet());
        for (String planeModel : planeModels) {
            this.planes.remove(planeModel);
        }

        List<String> flightsList = new ArrayList<>(this.flights.keySet());
        for (String flightCode : flightsList) {
            this.flights.remove(flightCode);
        }
    }

    public Plane getPlane(String planeName) throws RemoteException {
        Plane plane;
        try {
            planesLock.readLock().lock();
            plane = this.planes.get(planeName);
        } finally {
            planesLock.readLock().lock();
        }
        if (plane == null) throw new RemoteException("Error: flight with code " + planeName + " does not exist");
        return plane;
    }

    public Flight getFlight(String code) throws RemoteException {
        Flight flight;
        try {
            flightsLock.readLock().lock();
            flight = this.flights.get(code);
            if (flight == null) throw new RemoteException("Error: flight with code " + code + " does not exist");
        } finally {
            flightsLock.readLock().unlock();
        }
        return flight;
    }

    public void createPlane(String name, Map<SeatCategory, PlaneData> planeDataMap) throws RemoteException {
        try {
            planesLock.readLock().lock();
            if (this.planes.containsKey(name)) {
                throw new RemoteException("Error: plane " + name + " already exists");
            }
        } finally {
            planesLock.readLock().unlock();
        }
        try {
            planesLock.writeLock().lock();
            Plane plane = new Plane(name, planeDataMap);
            this.planes.put(plane.getName(), plane);
        } finally {
            planesLock.writeLock().lock();
        }
    }

    public void createFlight(String planeName, String code, String destination, List<TicketDto> tickets) throws RemoteException {
        try {
            flightsLock.readLock().lock();
            if (this.flights.containsKey(code)) {
                throw new RemoteException("Error: flight " +code+ " already exists");
            }
        } finally {
            flightsLock.readLock().unlock();
        }
        Flight flight;
        try {
            planesLock.readLock().lock();
            if (planeName == null || !this.planes.containsKey(planeName))
                throw new RemoteException("Error: plane doesn't exists");
            flight = new Flight(planeName, this.planes.get(planeName).getSeats(), code, destination);
        } finally {
            planesLock.readLock().unlock();
        }
        // add synchronize
        tickets.forEach(ticketDto -> flight.addTicketToFlight(Ticket.createFromDto(ticketDto)));
        try {
            flightsLock.writeLock().lock();
            this.flights.put(flight.getCode(), flight);
        } finally {
            flightsLock.writeLock().unlock();
        }

    }

    public FlightStatus checkFlightStatus(String code) throws RemoteException {
        try {
            flightsLock.readLock().lock();
            Flight flight = getFlight(code);
            if (flight == null) throw new RemoteException("Error: flight " + code + "does not exist");
            return flight.getStatus();
        } finally {
            flightsLock.readLock().unlock();
        }
    }

    public void confirmPendingFlight(String code) throws RemoteException {
        if (notificationService == null) init();
        Flight flight;
        try {
            flightsLock.readLock().lock();
            flight = getFlight(code);
            flight.chargePendingStatus(FlightStatus.CONFIRMED);
        } finally {
            flightsLock.readLock().unlock();
        }
        notificationService.newNotification(code, flight.getTicketList(), NotificationCategory.FLIGHT_CONFIRMED);
    }

    public void cancelPendingFlight(String code) throws RemoteException {
        if (notificationService == null) init();
        Flight flight;
        try {
            flightsLock.readLock().lock();
            flight = getFlight(code);
            if (flight == null)
                throw new RemoteException("Error: flight " + code + "does not exist");
            flight.chargePendingStatus(FlightStatus.CANCELLED);
        } finally {
            flightsLock.readLock().unlock();
        }
        notificationService.newNotification(code, flight.getTicketList(), NotificationCategory.FLIGHT_CANCELLED);

    }

    public ChangedTicketsDto findNewSeatsForCancelledFlights() throws RemoteException{
        if (notificationService == null) init();
        List<Flight> cancelledFlights;
        try {
            flightsLock.readLock().lock();
            cancelledFlights = getCancelledFlights();
        } finally {
            flightsLock.readLock().lock();
        }
        int totalTickets = 0;
        List<TicketDto> notChangedTickets = new ArrayList<>();
        for (Flight flight : cancelledFlights) {
            totalTickets += flight.getTicketList().size();
            findNewSeatsForFlight(flight);
            totalTickets -= flight.getTicketList().size();
            flight.getTicketList().forEach((ticket -> {
                TicketDto ticketDto = new TicketDto(ticket.getName(), ticket.getSeatCategory(), ticket.getFlightCode());
                notChangedTickets.add(ticketDto);
            }));
        }

        return new ChangedTicketsDto(notChangedTickets, totalTickets);


    }


    public List<Flight> getAlternativeFlights(String destination, String distinctToFlightCode) {
        try {
            flightsLock.readLock().lock();
            return this.flights.values()
                    .stream()
                    .filter(f -> f.getDestination().equals(destination)
                            && !f.getStatus().equals(FlightStatus.CANCELLED)
                            && !f.getCode().equals(distinctToFlightCode))
                    .collect(Collectors.toList());
        } finally {
            flightsLock.readLock().unlock();
        }
    }

    private void findNewSeatsForFlight(Flight oldFlight) throws RemoteException {
        List<Flight> alternativeFlights;
        try {
            flightsLock.readLock().lock();
            alternativeFlights = this.flights.values().stream()
                    .filter(flight ->
                            flight.getDestination().equals(oldFlight.getDestination()) &&
                                    flight.getAvailableSeatsAmount() > 0 &&
                                    !flight.getStatus().equals(FlightStatus.CANCELLED))
                    .collect(Collectors.toList());
        } finally {
            flightsLock.readLock().unlock();
        }

        List<Ticket> economyTickets = oldFlight.getTicketList().stream().filter(ticket -> ticket.getSeatCategory() == SeatCategory.ECONOMY).sorted(Comparator.comparing(Ticket::getName)).collect(Collectors.toList());
        List<Ticket> premiumEconomyTickets = oldFlight.getTicketList().stream().filter(ticket -> ticket.getSeatCategory() == SeatCategory.PREMIUM_ECONOMY).sorted(Comparator.comparing(Ticket::getName)).collect(Collectors.toList());
        List<Ticket> businessTickets = oldFlight.getTicketList().stream().filter(ticket -> ticket.getSeatCategory() == SeatCategory.BUSINESS).sorted(Comparator.comparing(Ticket::getName)).collect(Collectors.toList());


        List<SeatCategory> seatCategories = Arrays.stream(SeatCategory.values()).sorted().collect(Collectors.toList());
        for (int i = 0; i < seatCategories.size() && businessTickets.size() > 0; i++) {
            swapTickets(seatCategories.get(i), businessTickets, alternativeFlights);
        }
        for (int i = 1; i < seatCategories.size() && premiumEconomyTickets.size() > 0; i++) {
            swapTickets(seatCategories.get(i), premiumEconomyTickets, alternativeFlights);
        }
        swapTickets(SeatCategory.ECONOMY, economyTickets, alternativeFlights);
    }

    private void swapTickets(SeatCategory seatCategory, List<Ticket> oldTickets, List<Flight> flights) {
        List<Flight> sortedFlight = flights.stream()
                .sorted(
                        Comparator.comparing(Flight::getAvailableSeatsAmount)
                                .reversed()
                                .thenComparing(Flight::getCode, Comparator.naturalOrder()))
                .collect(Collectors.toList());
        for (int j = 0; j < sortedFlight.size(); j++) {
            Flight flight = sortedFlight.get(j);
            long validSeatSize = flight.getAvailableSeatsAmountByCategory(seatCategory);
            for (int i = 0; i < validSeatSize && !oldTickets.isEmpty(); i++) {
                try {
                    String oldFlightCode = oldTickets.get(0).getFlightCode();
                    String oldDest = this.getFlight(oldFlightCode).getDestination();
                    swapTicket(oldTickets.get(0), flight, seatCategory);
                    notificationService.newNotificationChangeTicket(flight.getCode(), oldTickets.get(0).getName(), oldFlightCode, oldDest);
                    oldTickets.remove(0);
                } catch (RemoteException e) {
                    e.getCause().getMessage();
                }
            }
        };
    }


    private void swapTicket(Ticket oldTicket, Flight flight, SeatCategory seatCategory) throws RemoteException {
        this.getFlight(oldTicket.getFlightCode()).swapTicket(oldTicket, flight, seatCategory);
    }

    private List<Flight> getCancelledFlights() {
        List<Flight> flights;
        try {
            flightsLock.readLock().lock();
            flights = new ArrayList<>(this.flights.values());
        } finally {
            flightsLock.readLock().unlock();
        }
        return flights.stream().filter(flight -> flight.getStatus() == FlightStatus.CANCELLED).collect(Collectors.toList());
    }
}
