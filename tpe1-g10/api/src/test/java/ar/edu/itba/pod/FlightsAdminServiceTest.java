package api.src.test.java.ar.edu.itba.pod;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Plane;
import api.src.main.java.ar.edu.itba.pod.models.RowData;
import api.src.main.java.ar.edu.itba.pod.services.FlightsAdminService;
import org.junit.jupiter.api.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class FlightsAdminServiceTest {

    FlightsAdminService flightsAdminService = FlightsAdminService.getInstance();


    @BeforeEach
    public void restartService() {
        flightsAdminService.restart();
    }

    @Test
    public void testCancelWithNoPlaceToGo() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = new Plane("PLANE_1", rowsData);
        Flight flight = new Flight(plane, "AA" , "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);

        flightsAdminService.addPlaneModel(plane);
        flightsAdminService.addFlight(flight);
        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        Assertions.assertEquals(5, flight.getTicketList().size());
    }

    @Test
    public void testCancelFlightAndFindNewSeats() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = new Plane("PLANE_1", rowsData);
        Flight flight = new Flight(plane, "AA" , "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);

        Flight flight2 = new Flight(plane, "AB" , "mardel", "BA");


        flightsAdminService.addPlaneModel(plane);
        flightsAdminService.addFlight(flight);
        flightsAdminService.addFlight(flight2);

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(0, flight.getTicketList().size());
        Assertions.assertEquals(5, flight2.getTicketList().size());

        // Checks that all the tickets hasn't been seated
        flight2.getTicketList().forEach((ticket -> {
            Assertions.assertNull(ticket.getSeat());
        }));
    }

    @Test
    public void testCancelFlightAndTryToMoveSeatsToAPlaneWithNoAvailableSeats() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = new Plane("PLANE_1", rowsData);
        Flight flight = new Flight(plane, "AA" , "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);

        List<RowData> rowData2 = new ArrayList<>();
        rowData2.add(new RowData(SeatCategory.BUSINESS, 1));
        Plane plane2 = new Plane("PLANE_2", rowData2);
        Flight flight2 = new Flight(plane2, "AB" , "mardel", "BA");

        flightsAdminService.addPlaneModel(plane);
        flightsAdminService.addPlaneModel(plane2);

        flightsAdminService.addFlight(flight);
        flightsAdminService.addFlight(flight2);

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(4, flight.getTicketList().size());
        Assertions.assertEquals(1, flight2.getTicketList().size());
    }

    @Test
    public void testMoveTicketsTwoMultiplePlane() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = new Plane("PLANE_1", rowsData);
        Flight flight = new Flight(plane, "AA" , "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);

        List<RowData> rowData2 = new ArrayList<>();
        rowData2.add(new RowData(SeatCategory.ECONOMY, 1));
        Plane plane2 = new Plane("PLANE_2", rowData2);
        Flight flight2 = new Flight(plane2, "AB" , "mardel", "BA");

        Flight flight3 = new Flight(plane2, "AC" , "mardel", "BA");

        flightsAdminService.addPlaneModel(plane);
        flightsAdminService.addPlaneModel(plane2);

        flightsAdminService.addFlight(flight);
        flightsAdminService.addFlight(flight2);
        flightsAdminService.addFlight(flight3);

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(3, flight.getTicketList().size());
        Assertions.assertEquals(1, flight2.getTicketList().size());
        Assertions.assertEquals(1, flight3.getTicketList().size());
    }

    @Test
    public void testMoveTicketsPlaneWithNoSameCategorySeats() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = new Plane("PLANE_1", rowsData);
        Flight flight = new Flight(plane, "AA" , "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);

        List<RowData> rowData2 = new ArrayList<>();
        rowData2.add(new RowData(SeatCategory.PREMIUM_ECONOMY, 1));
        Plane plane2 = new Plane("PLANE_2", rowData2);
        Flight flight2 = new Flight(plane2, "AB" , "mardel", "BA");

        flightsAdminService.addPlaneModel(plane);
        flightsAdminService.addPlaneModel(plane2);

        flightsAdminService.addFlight(flight);
        flightsAdminService.addFlight(flight2);

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(5, flight.getTicketList().size());
        Assertions.assertEquals(0, flight2.getTicketList().size());
    }

}