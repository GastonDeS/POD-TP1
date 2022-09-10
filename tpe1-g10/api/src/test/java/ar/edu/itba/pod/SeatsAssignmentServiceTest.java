package api.src.test.java.ar.edu.itba.pod;

import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Plane;
import api.src.main.java.ar.edu.itba.pod.models.RowData;
import api.src.main.java.ar.edu.itba.pod.models.Seat;
import api.src.main.java.ar.edu.itba.pod.services.FlightsAdminService;
import api.src.main.java.ar.edu.itba.pod.services.SeatsAssignmentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class SeatsAssignmentServiceTest {
    private final FlightsAdminService flightsAdminService = FlightsAdminService.getInstance();
    private final SeatsAssignmentService seatsAssignmentService = SeatsAssignmentService.getInstance();

    @BeforeEach
    public void restartService() {
        flightsAdminService.restart();
    }

    @Test
    public void testEmptySeat() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");

        TestUtils.fillFlightWithPassengers(flight);

        Assertions.assertTrue(seatsAssignmentService.checkEmptySeat(flight.getCode(), 1, "A"));
    }

    @Test
    public void testNonEmptySeat() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");

        TestUtils.fillFlightWithPassengers(flight);
        TestUtils.setSeatAvailability(flight, 1, "A", false);

        Assertions.assertFalse(seatsAssignmentService.checkEmptySeat(flight.getCode(), 1, "A"));
    }

    @Test
    public void testAssignSeatAvailable() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");
        TestUtils.fillFlightWithPassengers(flight);
        seatsAssignmentService.assignSeat(flight.getCode(), "Brittu", 4, "A");

        Assertions.assertFalse(flight.getSeat(4, "A").isAvailable());
    }

    @Test
    public void testAssignSeatNotAvailable() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");
        TestUtils.fillFlightWithPassengers(flight);
        TestUtils.setSeatAvailability(flight, 1, "A", false);

        Assertions.assertThrows(RemoteException.class,
                () -> seatsAssignmentService.assignSeat(flight.getCode(), "Brittu", 1, "A"));
    }

    @Test
    public void testAssignSeatFlightNotPending() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");
        flight.setStatus(FlightStatus.CANCELLED);
        TestUtils.fillFlightWithPassengers(flight);

        Assertions.assertThrows(RemoteException.class,
                () -> seatsAssignmentService.assignSeat(flight.getCode(), "Brittu", 1, "A"));
    }

    @Test
    public void testAssignSeatWrongCategory() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");
        TestUtils.fillFlightWithPassengers(flight);

        Assertions.assertThrows(RemoteException.class,
                () -> seatsAssignmentService.assignSeat(flight.getCode(), "Gaston", 1, "A"));
    }

    @Test
    public void testChangeSeat() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");
        TestUtils.fillFlightWithPassengers(flight);
        seatsAssignmentService.assignSeat(flight.getCode(), "Brittu", 4, "A");
        seatsAssignmentService.changeSeat(flight.getCode(), "Brittu", 7, "A");

        Assertions.assertFalse(flight.getSeat(7, "A").isAvailable());
        Assertions.assertTrue(flight.getSeat(1, "A").isAvailable());
    }

    @Test
    public void testGetAvailableFlights() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Plane alternativePlane = flightsAdminService.createPlane("PLANE_2", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");
        Flight alternativeFlight = flightsAdminService.createFlight(alternativePlane, "BR", "EZE", "CDG");
        alternativeFlight.setStatus(FlightStatus.CONFIRMED);
        TestUtils.fillFlightWithPassengers(flight);
        Map<String, List<Seat>> availableFlights = seatsAssignmentService.getAvailableFlights(flight.getCode(), "Gaston");

        Assertions.assertEquals(1, availableFlights.size());
        Assertions.assertEquals(28, availableFlights.get(alternativeFlight.getCode()).size());
    }

    @Test
    public void testGetAvailableFlightsAlreadyConfirmed() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");
        flight.setStatus(FlightStatus.CONFIRMED);

        Assertions.assertThrows(RemoteException.class,
                () -> seatsAssignmentService.getAvailableFlights(flight.getCode(), "Gaston"));
    }

    @Test
    public void testChangeTicket() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Plane alternativePlane = flightsAdminService.createPlane("PLANE_2", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");
        Flight alternativeFlight = flightsAdminService.createFlight(alternativePlane, "BR", "EZE", "CDG");
        TestUtils.fillFlightWithPassengers(flight);
        seatsAssignmentService.changeTicket("Brittu", flight.getCode(), alternativeFlight.getCode());

        Assertions.assertEquals(1, alternativeFlight.getTicketList().size());
        Assertions.assertEquals(4, flight.getTicketList().size());
    }

    @Test
    public void testChangeTicketNoFlight() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = flightsAdminService.createPlane("PLANE_1", rowsData);
        Flight flight = flightsAdminService.createFlight(plane, "AA", "EZE", "CDG");
        TestUtils.fillFlightWithPassengers(flight);

        Assertions.assertThrows(RemoteException.class,
                () -> seatsAssignmentService.changeTicket("Brittu", flight.getCode(), "BR"));
    }
}
