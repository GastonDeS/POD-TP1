package api.src.main.java.ar.edu.itba.pod.services;

import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;
import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;
import api.src.main.java.ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Plane;
import api.src.main.java.ar.edu.itba.pod.models.RowData;
import api.src.main.java.ar.edu.itba.pod.models.Ticket;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class FlightsAdminService implements FlightAdminServiceInterface {
    private static FlightsAdminService instance;
    private final Map<String, Plane> planes;
    private final Map<String, Flight> flights;

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

    public Flight getFlight(String code) throws RemoteException {
        Flight flight = flights.get(code);
        if (flight == null) throw new RemoteException();
        return flight;
    }

    public Plane createPlane(String name, List<RowData> rowDataList) throws RemoteException {
        if(planes.containsKey(name)){
            throw new RemoteException();
        }
        Plane plane = new Plane(name, rowDataList);
        planes.put(plane.getName(), plane);
        return plane;
    }

    public Flight createFlight(Plane plane, String code, String origin, String destination) throws RemoteException {
        if (flights.containsKey(code)) {
            throw new RemoteException();
        }
        Flight flight = new Flight(plane, code, origin, destination);
        flights.put(flight.getCode(), flight);
        return flight;
    }

    public FlightStatus checkFlightStatus(String code) throws RemoteException {
        Flight flight = getFlight(code);
        return flight.getStatus();
    }

    public void confirmPendingFlight(String code) throws RemoteException {
        Flight flight = getFlight(code);
        if (flight.getStatus() != FlightStatus.PENDING) throw new RemoteException();
        flight.setStatus(FlightStatus.CONFIRMED);
    }

    public void cancelPendingFlight(String code) throws RemoteException {
        Flight flight = getFlight(code);
        if (flight.getStatus() != FlightStatus.PENDING) throw new RemoteException();
        flight.setStatus(FlightStatus.CANCELLED);
    }

    public String findNewSeatsForCancelledFlights() throws RemoteException {
        List<Flight> cancelledFlights = getCancelledFlights();
        Integer totalTickets = 0;
        StringBuilder response = new StringBuilder();
        for (Flight flight : cancelledFlights) {
            totalTickets += flight.getTicketList().size();
            findNewSeatsForFlight(flight);
            totalTickets -= flight.getTicketList().size();
            flight.getTicketList().forEach((ticket -> {
                response.append("Cannot find alternative flight for ").append(ticket.getName()).append(" with Ticket ").append(ticket.getFlight().getCode()).append("\n");
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

    private void findNewSeatsForFlight(Flight oldFlight) {
        List<Flight> possibleFlights = flights.values().stream()
                .filter(flight -> flight.getOrigin().equals(oldFlight.getOrigin()) &&
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

    private void swapTickets(SeatCategory seatCategory, List<Ticket> oldTickets, List<Flight> flights) {
        flights.stream().sorted(Comparator.comparing(Flight::getAvailableSeatsAmount).thenComparing(Flight::getCode)).forEach(flight -> {
            long validSeatSize = flight.getAvailableSeatsAmountByCategory(seatCategory);
            for (int i = 0; i < validSeatSize && !oldTickets.isEmpty(); i++) {
                swapTicket(oldTickets.get(0), flight, seatCategory);
                oldTickets.remove(0);
            }
        });
    }

    private void swapTicket(Ticket oldTicket, Flight flight, SeatCategory seatCategory) {
        oldTicket.getFlight().removeTicketFromFlight(oldTicket);
        oldTicket.setSeat(null);
        oldTicket.setFlight(flight);
        oldTicket.setSeatCategory(seatCategory);
        flight.addTicketToFlight(oldTicket);
    }

    private List<Flight> getCancelledFlights() {
        return flights.values().stream().filter(flight -> flight.getStatus() == FlightStatus.CANCELLED).collect(Collectors.toList());
    }
}
