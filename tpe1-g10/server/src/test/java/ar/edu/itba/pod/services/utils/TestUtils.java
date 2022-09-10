package ar.edu.itba.pod.services.utils;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.Flight;
import ar.edu.itba.pod.models.RowData;
import ar.edu.itba.pod.models.Ticket;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

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

    public static List<RowData> getRowDataForFlight() {
        List<RowData> rowData = new ArrayList<>();
        rowData.add(new RowData(SeatCategory.BUSINESS, 3));
        rowData.add(new RowData(SeatCategory.BUSINESS, 3));
        rowData.add(new RowData(SeatCategory.BUSINESS, 3));
        rowData.add(new RowData(SeatCategory.PREMIUM_ECONOMY, 5));
        rowData.add(new RowData(SeatCategory.PREMIUM_ECONOMY, 5));
        rowData.add(new RowData(SeatCategory.PREMIUM_ECONOMY, 5));
        rowData.add(new RowData(SeatCategory.PREMIUM_ECONOMY, 5));
        rowData.add(new RowData(SeatCategory.ECONOMY, 7));
        rowData.add(new RowData(SeatCategory.ECONOMY, 7));
        rowData.add(new RowData(SeatCategory.ECONOMY, 7));
        rowData.add(new RowData(SeatCategory.ECONOMY, 7));
        return rowData;
    }

    public static void setSeatAvailability (Flight flight, int row, String column, boolean isAvailable) throws RemoteException {
        flight.getSeat(row, column).setAvailable(isAvailable, '*');
    }
}
