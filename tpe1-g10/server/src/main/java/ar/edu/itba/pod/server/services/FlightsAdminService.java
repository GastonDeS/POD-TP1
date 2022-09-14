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
    private NotificationService notificationService;
    private final String reassignLock = ""; // Lock for reassign cancelled flights

    private FlightsAdminService() {
        this.planes = new HashMap<>();
        this.flights = new HashMap<>();
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

    public void createFlight(String planeName, String code, String destination, List<TicketDto> tickets) throws RemoteException {
        if (flights.containsKey(code)) {
            throw new RemoteException("Error: flight " +code+ " already exists");
        }
        if (planeName == null || !planes.containsKey(planeName))
            throw new RemoteException("Error: plane doesn't exists");
        Flight flight = new Flight(planeName, planes.get(planeName).getSeats(), code, destination);
        tickets.forEach(ticketDto -> flight.addTicketToFlight(Ticket.createFromDto(ticketDto)));
        flights.put(flight.getCode(), flight);
        logger.info("flightAdded: "+code+" "+tickets.size());
    }

    public FlightStatus checkFlightStatus(String code) throws RemoteException {
        Flight flight = getFlight(code);
        if (flight == null) throw new RemoteException("Error: flight "+ code + "does not exist");
        return flight.getStatus();
    }

    public void confirmPendingFlight(String code) throws RemoteException {
        if (notificationService == null) init();
        Flight flight = getFlight(code);
        flight.chargePendingStatus(FlightStatus.CONFIRMED);
        notificationService.newNotification(code, flight.getTicketList(), NotificationCategory.FLIGHT_CONFIRMED);
    }

    public void cancelPendingFlight(String code) throws RemoteException {
        if (notificationService == null) init();
        Flight flight = getFlight(code);
        if( flight == null)
            throw new RemoteException("Error: flight " + code + "does not exist");
        flight.chargePendingStatus(FlightStatus.CANCELLED);
        notificationService.newNotification(code, flight.getTicketList(), NotificationCategory.FLIGHT_CANCELLED);
    }

    public ChangedTicketsDto findNewSeatsForCancelledFlights() throws RemoteException{
        if (notificationService == null) init();
        synchronized (reassignLock) {
            List<Flight> cancelledFlights = getCancelledFlights();
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
                                !flight.getStatus().equals(FlightStatus.CANCELLED))
                .collect(Collectors.toList());

        List<Ticket> economyTickets = oldFlight.getTicketList().stream().filter(ticket -> ticket.getSeatCategory() == SeatCategory.ECONOMY).sorted(Comparator.comparing(Ticket::getName)).collect(Collectors.toList());
        List<Ticket> premiumEconomyTickets = oldFlight.getTicketList().stream().filter(ticket -> ticket.getSeatCategory() == SeatCategory.PREMIUM_ECONOMY).sorted(Comparator.comparing(Ticket::getName)).collect(Collectors.toList());
        List<Ticket> businessTickets = oldFlight.getTicketList().stream().filter(ticket -> ticket.getSeatCategory() == SeatCategory.BUSINESS).sorted(Comparator.comparing(Ticket::getName)).collect(Collectors.toList());


        List<SeatCategory> seatCategories = Arrays.stream(SeatCategory.values()).sorted().collect(Collectors.toList());
        for (int i = 0; i < seatCategories.size() && businessTickets.size() > 0; i++) {
            try {
                swapTickets(seatCategories.get(i), businessTickets, possibleFlights);
            } catch (Exception ex) {
                ex.getCause().getMessage();
            }
        }
        for (int i = 1; i < seatCategories.size() && premiumEconomyTickets.size() > 0; i++) {
            swapTickets(seatCategories.get(i), premiumEconomyTickets, possibleFlights);
        }
        swapTickets(SeatCategory.ECONOMY, economyTickets, possibleFlights);
    }

    private void swapTickets(SeatCategory seatCategory, List<Ticket> oldTickets, List<Flight> flights) {
        List<Flight> sortedFlight = flights.stream().sorted(Comparator.comparing(Flight::getAvailableSeatsAmount).reversed().thenComparing(Flight::getCode, Comparator.naturalOrder())).collect(Collectors.toList());
        for (int j = 0; j < sortedFlight.size(); j++) {
            Flight flight = sortedFlight.get(j);
            long validSeatSize = flight.getAvailableSeatsAmountByCategory(seatCategory);
            for (int i = 0; i < validSeatSize && !oldTickets.isEmpty(); i++) {
                try {
                    if ("Flor0".equals(oldTickets.get(0).getName()))
                        System.out.println("flor: "+oldTickets.get(0).getFlightCode()+" "+oldTickets.get(0).getSeatCategory().getMessage());

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
        return flights.values().stream().filter(flight -> flight.getStatus() == FlightStatus.CANCELLED).collect(Collectors.toList());
    }
}
