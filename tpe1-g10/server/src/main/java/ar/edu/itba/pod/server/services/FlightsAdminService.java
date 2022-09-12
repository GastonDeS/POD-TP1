package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.constants.NotificationCategory;
import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import ar.edu.itba.pod.server.services.NotificationService;
import ar.edu.itba.pod.models.Flight;
import ar.edu.itba.pod.models.Plane;
import ar.edu.itba.pod.models.Ticket;
import ar.edu.itba.pod.models.PlaneData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class FlightsAdminService implements FlightAdminServiceInterface {
    private static final Logger logger = LoggerFactory.getLogger(FlightsAdminService.class);
    private static FlightsAdminService instance;
    private final Map<String, Plane> planes;
    private final Map<String, Flight> flights;
    private final NotificationService notificationService;

    private FlightsAdminService() {
        this.planes = new HashMap<>();
        this.flights = new HashMap<>();
        this.notificationService = NotificationService.getInstance();
    }

    public static FlightsAdminService getInstance() {
        if (FlightsAdminService.instance == null) {
            FlightsAdminService.instance = new FlightsAdminService();
        }
        return FlightsAdminService.instance;
    }

    // For test purposes
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
        Plane plane = planes.get(planeName);
        if (plane == null) throw new RemoteException("Error: flight with code " + planeName + " does not exist");
        return plane;
    }
    public Flight getFlight(String code) throws RemoteException {
        Flight flight = flights.get(code);
        if (flight == null) throw new RemoteException("Error: flight with code " + code + " does not exist");
        return flight;
    }

    public void createPlane(String name, Map<SeatCategory, PlaneData> planeDataMap) throws RemoteException {
        if(planes.containsKey(name)){
            throw new RemoteException("Error: plane " +name+ " already exists");
        }
        Plane plane = new Plane(name, planeDataMap);
        planes.put(plane.getName(), plane);
        logger.info("plane created");
    }

    // TODO tickets may not need to be created when a code and the code can be added here
    public void createFlight(String planeName, String code, String destination, List<Ticket> tickets) throws RemoteException {
        if (flights.containsKey(code)) {
            throw new RemoteException("Error: flight " +code+ " already exists");
        }
        if (planeName == null || !planes.containsKey(planeName))
            throw new RemoteException("Error: plane doesn't exists");
        Flight flight = new Flight(planeName, planes.get(planeName).getSeats(), code, destination);
        tickets.forEach(flight::addTicketToFlight);
        flights.put(flight.getCode(), flight);
        logger.info("flightAdded: "+code+" "+tickets.size());
    }

    public FlightStatus checkFlightStatus(String code) throws RemoteException {
        Flight flight = getFlight(code);
        if (flight == null) throw new RemoteException("Error: flight "+ code + "does not exist");
        return flight.getStatus();
    }

    public void confirmPendingFlight(String code) throws RemoteException {
        Flight flight = getFlight(code);
        if (flight.getStatus() != FlightStatus.PENDING) throw new RemoteException("Error: flight " + code + "is "+ flight.getStatus());
        flight.setStatus(FlightStatus.CONFIRMED);
        for (Ticket ticket : flight.getTicketList()) {
          notificationService.newNotification(code, ticket.getName(), NotificationCategory.FLIGHT_CONFIRMED);
        }
    }

    public void cancelPendingFlight(String code) throws RemoteException {
        Flight flight = getFlight(code);
        if( flight == null)
            throw new RemoteException("Error: flight " + code + "does not exist");
        if (flight.getStatus() != FlightStatus.PENDING) throw new RemoteException("Error: flight " + code + "is "+ flight.getStatus());
        flight.setStatus(FlightStatus.CANCELLED);
        for (Ticket ticket : flight.getTicketList()) {
            notificationService.newNotification(code, ticket.getName(), NotificationCategory.FLIGHT_CANCELLED);
        }
    }

    // TODO Change we need a way to not return everything via String
    // TODO add notification
    public String findNewSeatsForCancelledFlights() throws RemoteException{
        List<Flight> cancelledFlights = getCancelledFlights();
        int totalTickets = 0;
        StringBuilder response = new StringBuilder();
        for (Flight flight : cancelledFlights) {
            totalTickets += flight.getTicketList().size();
            findNewSeatsForFlight(flight);
            totalTickets -= flight.getTicketList().size();
            flight.getTicketList().forEach((ticket -> {
                response.append("Cannot find alternative flight for ").append(ticket.getName()).append(" with Ticket ").append(ticket).append("\n");
            }));
        }
        response.insert(0,totalTickets+" tickets were changed\n");

        return response.toString();
    }

    public Map<String, Plane> getPlanes() {
        return planes;
    }

    public Map<String, Flight> getFlights() {
        return flights;
    }

    private void findNewSeatsForFlight(Flight oldFlight) throws RemoteException {
        List<Flight> possibleFlights = flights.values().stream()
                .filter(flight ->
                        flight.getDestination().equals(oldFlight.getDestination()) &&
                        flight.getAvailableSeatsAmount() > 0 &&
                        flight.getStatus() != FlightStatus.CANCELLED)
                .collect(Collectors.toList());

        List<Ticket> economyTickets = oldFlight.getTicketList().stream().filter(ticket -> ticket.getSeatCategory() == SeatCategory.ECONOMY).sorted(Comparator.comparing(Ticket::getName)).collect(Collectors.toList());
        List<Ticket> premiumEconomyTickets = oldFlight.getTicketList().stream().filter(ticket -> ticket.getSeatCategory() == SeatCategory.PREMIUM_ECONOMY).sorted(Comparator.comparing(Ticket::getName)).collect(Collectors.toList());
        List<Ticket> businessTickets = oldFlight.getTicketList().stream().filter(ticket -> ticket.getSeatCategory() == SeatCategory.BUSINESS).sorted(Comparator.comparing(Ticket::getName)).collect(Collectors.toList());


        List<SeatCategory> seatCategories = Arrays.stream(SeatCategory.values()).sorted().collect(Collectors.toList());
        for (int i =0 ; i < seatCategories.size() && businessTickets.size() > 0 ; i++) {
            swapTickets(seatCategories.get(i), businessTickets, possibleFlights);
        }
        for (int i = 1  ; i < seatCategories.size() && premiumEconomyTickets.size() > 0 ; i++) {
            swapTickets(seatCategories.get(i), premiumEconomyTickets, possibleFlights);
        }
        swapTickets(SeatCategory.ECONOMY, economyTickets, possibleFlights);
    }

    private void swapTickets(SeatCategory seatCategory, List<Ticket> oldTickets, List<Flight> flights) throws RemoteException {
        flights.stream().sorted(Comparator.comparing(Flight::getAvailableSeatsAmount).thenComparing(Flight::getCode)).forEach(flight -> {
            long validSeatSize = flight.getAvailableSeatsAmountByCategory(seatCategory);
            for (int i = 0; i < validSeatSize && !oldTickets.isEmpty(); i++) {
                try {
                    swapTicket(oldTickets.get(0), flight, seatCategory);
                    oldTickets.remove(0);
                    notificationService.newNotification(flight.getCode(), oldTickets.get(0).getName(), NotificationCategory.CHANGED_TICKET);
                } catch (RemoteException e) {
                    logger.error("Fail to swap ticket "+ oldTickets.get(0).getName() +" for Flight "+oldTickets.get(0).getFlightCode());
                }
            }
        });
    }


    // TODO test this again
    private void swapTicket(Ticket oldTicket, Flight flight, SeatCategory seatCategory) throws RemoteException {
        this.getFlight(oldTicket.getFlightCode()).removeTicketFromFlight(oldTicket);
        oldTicket.setSeat(null);
        oldTicket.setFlightCode(flight.getCode());
        oldTicket.setSeatCategory(seatCategory);
        flight.addTicketToFlight(oldTicket);
    }

    private List<Flight> getCancelledFlights() {
        return flights.values().stream().filter(flight -> flight.getStatus() == FlightStatus.CANCELLED).collect(Collectors.toList());
    }
}
