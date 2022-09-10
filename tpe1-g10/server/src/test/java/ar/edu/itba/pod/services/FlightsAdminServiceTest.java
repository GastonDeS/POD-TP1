package ar.edu.itba.pod.services;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.RowData;
import ar.edu.itba.pod.models.Ticket;
import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.services.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class FlightsAdminServiceTest {

    private final FlightsAdminService flightsAdminService = FlightsAdminService.getInstance();

    private static final String PLANE_1 = "PLANE_1";
    private static final String PLANE_2 = "PLANE_2";

    @BeforeEach
    public void restartService() {
        flightsAdminService.restart();
    }

    @Test
    public void testCancelWithNoPlaceToGo() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<Ticket > tickets = TestUtils.getTickets("AA");

        flightsAdminService.createFlight(PLANE_1, "AA", "BA", tickets);
        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        Assertions.assertEquals(5, flightsAdminService.getFlight("AA").getTicketList().size());
    }

    @Test
    public void testCancelFlightAndFindNewSeats() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();

        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<Ticket > tickets = TestUtils.getTickets("AA");

        flightsAdminService.createFlight(PLANE_1, "AA", "BA", tickets);

        flightsAdminService.createFlight(PLANE_1, "AB", "BA", new ArrayList<>());

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(0, flightsAdminService.getFlight("AA").getTicketList().size());
        Assertions.assertEquals(5, flightsAdminService.getFlight("AB").getTicketList().size());

    }

    @Test
    public void testCancelFlightAndTryToMoveSeatsToAPlaneWithNoAvailableSeats() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<Ticket > tickets = TestUtils.getTickets("AA");

        flightsAdminService.createFlight(PLANE_1, "AA", "BA", tickets);

        List<RowData> rowData2 = new ArrayList<>();
        rowData2.add(new RowData(SeatCategory.BUSINESS, 1));
        flightsAdminService.createPlane(PLANE_2, rowData2);
        flightsAdminService.createFlight(PLANE_2, "AB", "BA", new ArrayList<>());

        flightsAdminService.cancelPendingFlight("AA");
        String response = flightsAdminService.findNewSeatsForCancelledFlights();

        System.out.println(response);

        // Checks that all tickets has been swapped
        Assertions.assertEquals(5, flightsAdminService.getFlight("AA").getTicketList().size());
        Assertions.assertEquals(0, flightsAdminService.getFlight("AB").getTicketList().size());
    }

    @Test
    public void testMoveTicketsTwoMultiplePlane() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<Ticket > tickets = TestUtils.getTickets("AA");

        flightsAdminService.createFlight(PLANE_1, "AA", "BA", tickets);

        List<RowData> rowData2 = new ArrayList<>();
        rowData2.add(new RowData(SeatCategory.ECONOMY, 1));
        flightsAdminService.createPlane(PLANE_2, rowData2);
        flightsAdminService.createFlight(PLANE_2, "AB", "BA", new ArrayList<>());
        flightsAdminService.createFlight(PLANE_2, "ABC", "BA", new ArrayList<>());

        flightsAdminService.createFlight(PLANE_2, "AC", "BA", new ArrayList<>());

        Assertions.assertEquals(1,flightsAdminService.getPlane(PLANE_2).getTotalSeats());

        Assertions.assertEquals(5, flightsAdminService.getFlight("AA").getTicketList().size());

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(2, flightsAdminService.getFlight("AA").getTicketList().size());
        Assertions.assertEquals(1, flightsAdminService.getFlight("AB").getTicketList().size());
        Assertions.assertEquals(1, flightsAdminService.getFlight("ABC").getTicketList().size());
        Assertions.assertEquals(1, flightsAdminService.getFlight("AC").getTicketList().size());
    }

    @Test
    public void testMoveTicketsPlaneWithNoSameCategorySeats() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<Ticket > tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "BA", tickets);

        List<RowData> rowData2 = new ArrayList<>();
        rowData2.add(new RowData(SeatCategory.BUSINESS, 1));
        flightsAdminService.createPlane(PLANE_2, rowData2);
        flightsAdminService.createFlight(PLANE_2, "AB", "BA", new ArrayList<>());

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(5, flightsAdminService.getFlight("AA").getTicketList().size());
        Assertions.assertEquals(0, flightsAdminService.getFlight("AB").getTicketList().size());
    }

}
