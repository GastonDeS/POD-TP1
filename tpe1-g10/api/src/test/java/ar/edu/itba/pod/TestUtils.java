package api.src.test.java.ar.edu.itba.pod;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Plane;
import api.src.main.java.ar.edu.itba.pod.models.RowData;
import api.src.main.java.ar.edu.itba.pod.models.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static Flight fillFlightWithPassengers(Flight flight) {
        flight.addTicketToFlight(new Ticket("Gaston", SeatCategory.ECONOMY, flight));
        flight.addTicketToFlight(new Ticket("Brittu", SeatCategory.PREMIUM_ECONOMY, flight));
        flight.addTicketToFlight(new Ticket("Flor", SeatCategory.ECONOMY, flight));
        flight.addTicketToFlight(new Ticket("Juanma", SeatCategory.ECONOMY, flight));
        flight.addTicketToFlight(new Ticket("Sol", SeatCategory.ECONOMY, flight));
        return flight;
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
}
