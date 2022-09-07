package api.src.test.java.ar.edu.itba.pod;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Plane;
import api.src.main.java.ar.edu.itba.pod.models.RowData;
import api.src.main.java.ar.edu.itba.pod.models.Ticket;
import api.src.main.java.ar.edu.itba.pod.services.FlightsAdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class FlightsAdminServiceTest {

    private final FlightsAdminService flightsAdminService = FlightsAdminService.getInstance();

    @BeforeEach
    public void restartService() {
        flightsAdminService.restart();
    }

    @Test
    public void testCancelWithNoPlaceToGo() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);

        Flight flight = flightsAdminService.createFlight(plane, "AA", "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);
        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        Assertions.assertEquals(5, flight.getTicketList().size());
    }

    @Test
    public void testCancelFlightAndFindNewSeats() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();

        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);

        Flight flight2 = flightsAdminService.createFlight(plane, "AB", "mardel", "BA");

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(0, flight.getTicketList().size());
        Assertions.assertEquals(5, flight2.getTicketList().size());

        // Checks that all the tickets hasn't been seated
        flight2.getTicketList().forEach((ticket -> Assertions.assertNull(ticket.getSeat())));
    }

    @Test
    public void testCancelFlightAndTryToMoveSeatsToAPlaneWithNoAvailableSeats() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);

        Flight flight = flightsAdminService.createFlight(plane, "AA", "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);

        List<RowData> rowData2 = new ArrayList<>();
        rowData2.add(new RowData(SeatCategory.BUSINESS, 1));
        Plane plane2 = flightsAdminService.createPlane("PLANE_2", rowData2);
        Flight flight2 = flightsAdminService.createFlight(plane2, "AB", "mardel", "BA");

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(5, flight.getTicketList().size());
        Assertions.assertEquals(0, flight2.getTicketList().size());
    }

    @Test
    public void testMoveTicketsTwoMultiplePlane() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);

        Flight flight1 = flightsAdminService.createFlight(plane, "AA", "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight1);

        List<RowData> rowData2 = new ArrayList<>();
        rowData2.add(new RowData(SeatCategory.ECONOMY, 1));
        Plane plane2 = flightsAdminService.createPlane("PLANE_2", rowData2);
        Flight flight2 = flightsAdminService.createFlight(plane2, "AB", "mardel", "BA");
        Flight flight3 = flightsAdminService.createFlight(plane2, "ABC", "mardel", "BA");

        Flight flight4 = flightsAdminService.createFlight(plane2, "AC", "mardel", "BA");

        Assertions.assertEquals(1,plane2.getTotalSeats());

        Assertions.assertEquals(5, flight1.getTicketList().size());

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(2, flight1.getTicketList().size());
        Assertions.assertEquals(1, flight4.getTicketList().size());
        Assertions.assertEquals(1, flight2.getTicketList().size());
        Assertions.assertEquals(1, flight3.getTicketList().size());
    }

    @Test
    public void testMoveTicketsPlaneWithNoSameCategorySeats() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);

        List<RowData> rowData2 = new ArrayList<>();
        rowData2.add(new RowData(SeatCategory.BUSINESS, 1));
        Plane plane2 = flightsAdminService.createPlane("PLANE_2", rowData2);
        Flight flight2 = flightsAdminService.createFlight(plane2, "AB", "mardel", "BA");

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(5, flight.getTicketList().size());
        Assertions.assertEquals(0, flight2.getTicketList().size());
    }

}
