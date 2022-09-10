package ar.edu.itba.pod.services.utils;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.Flight;
import ar.edu.itba.pod.models.Ticket;
import ar.edu.itba.pod.models.PlaneData;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {
    
    public static List<Ticket> getTickets(String flightCode) {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(new Ticket("Gaston", SeatCategory.ECONOMY, flightCode));
        tickets.add(new Ticket("Brittu", SeatCategory.PREMIUM_ECONOMY, flightCode));
        tickets.add(new Ticket("Flor", SeatCategory.ECONOMY, flightCode));
        tickets.add(new Ticket("Juanma", SeatCategory.ECONOMY, flightCode));
        tickets.add(new Ticket("Sol", SeatCategory.ECONOMY, flightCode));
        return tickets;
    }

    public static Map<SeatCategory, PlaneData> getPlaneDataForFlight() {
        Map<SeatCategory, ar.edu.itba.pod.models.PlaneData> planeData = new HashMap<>();
        planeData.put(SeatCategory.BUSINESS, new ar.edu.itba.pod.models.PlaneData( 3, 3));
        planeData.put(SeatCategory.PREMIUM_ECONOMY, new ar.edu.itba.pod.models.PlaneData( 4, 5));
        planeData.put(SeatCategory.ECONOMY, new ar.edu.itba.pod.models.PlaneData( 4, 7));
        return planeData;
    }

    public static Map<SeatCategory, PlaneData> getOneSeatPlaneData(SeatCategory seatCategory) {
        Map<SeatCategory, ar.edu.itba.pod.models.PlaneData> planeData = new HashMap<>();
        planeData.put(seatCategory, new ar.edu.itba.pod.models.PlaneData( 1, 1));
        return planeData;
    }
    public static void setSeatAvailability (Flight flight, int row, String column, boolean isAvailable) throws RemoteException {
        flight.getSeat(row, column).setAvailable(isAvailable, '*');
    }
}
