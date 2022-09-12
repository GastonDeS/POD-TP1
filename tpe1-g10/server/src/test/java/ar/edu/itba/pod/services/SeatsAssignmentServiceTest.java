package ar.edu.itba.pod.services;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.PlaneData;
import ar.edu.itba.pod.models.Ticket;
import ar.edu.itba.pod.models.Flight;
import ar.edu.itba.pod.models.TicketDto;
import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.server.services.SeatsAssignmentService;
import ar.edu.itba.pod.services.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeatsAssignmentServiceTest {
    private final FlightsAdminService flightsAdminService = FlightsAdminService.getInstance();
    private final SeatsAssignmentService seatsAssignmentService = SeatsAssignmentService.getInstance();
    
    private static final String PLANE_1 = "PLANE_1";
    private static final String PLANE_2 = "PLANE_2";

    @BeforeEach
    public void restartService() {
        flightsAdminService.restart();
    }

    @Test
    public void testEmptySeat() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);

        Assertions.assertNull(seatsAssignmentService.checkEmptySeat("AA", 1, "A"));
    }

    @Test
    public void testNonEmptySeat() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);
        flightsAdminService.getFlight("AA").addTicketToFlight(new Ticket.Builder("Pedro")
                .seat(flightsAdminService.getFlight("AA").getSeat(10,"A")).build());

        Assertions.assertEquals("Pedro", seatsAssignmentService.checkEmptySeat("AA", 10, "A"));
    }

    @Test
    public void testAssignSeatAvailable() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);
        seatsAssignmentService.assignSeat("AA", "Brittu", 4, "A");

        Assertions.assertFalse(flightsAdminService.getFlight("AA").getSeat(4, "A").isAvailable());
    }

    @Test
    public void testAssignSeatNotAvailable() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);
        List<TicketDto> tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);
        TestUtils.setSeatAvailability(flightsAdminService.getFlight("AA"), 1, "A", false);

        Assertions.assertThrows(RemoteException.class,
                () -> seatsAssignmentService.assignSeat("AA", "Brittu", 1, "A"));
    }

    @Test
    public void testAssignSeatFlightNotPending() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");

        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);
        flightsAdminService.cancelPendingFlight("AA");

        Assertions.assertThrows(RemoteException.class,
                () -> seatsAssignmentService.assignSeat("AA", "Brittu", 1, "A"));
    }

    @Test
    public void testAssignSeatWrongCategory() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);

        Assertions.assertThrows(RemoteException.class,
                () -> seatsAssignmentService.assignSeat("AA", "Gaston", 1, "A"));
    }

    @Test
    public void testChangeSeat() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);
        seatsAssignmentService.assignSeat("AA", "Brittu", 4, "A");
        seatsAssignmentService.changeSeat("AA", "Brittu", 7, "A");

        Assertions.assertFalse(flightsAdminService.getFlight("AA").getSeat(7, "A").isAvailable());
        Assertions.assertTrue(flightsAdminService.getFlight("AA").getSeat(4, "A").isAvailable()); // TODO @linbrittany lo cambie de 4 a 1 pq no se pq mirabas la 1
    }

    @Test
    public void testGetAvailableFlights() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);
        flightsAdminService.createPlane(PLANE_2, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);
        List<TicketDto> tickets2 = TestUtils.getTickets("AA");

        flightsAdminService.createFlight(PLANE_2, "BR", "CDG", tickets2);
        seatsAssignmentService.assignSeat("BR", "Gaston", 10, "A");
        flightsAdminService.confirmPendingFlight("BR");
        Flight alternative = flightsAdminService.getFlight("BR");

        Map<SeatCategory, Map<Flight, Long>> availableFlights = seatsAssignmentService.getAvailableFlights("AA", "Brittu");

        Assertions.assertNull(availableFlights.get(SeatCategory.BUSINESS));
        Assertions.assertEquals(20L, availableFlights.get(SeatCategory.PREMIUM_ECONOMY).get(alternative));
        Assertions.assertEquals(27L, availableFlights.get(SeatCategory.ECONOMY).get(alternative));
    }

    @Test
    public void testGetAvailableFlightsAlreadyConfirmed() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");

        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);
        flightsAdminService.confirmPendingFlight("AA");

        Assertions.assertThrows(RemoteException.class,
                () -> seatsAssignmentService.getAvailableFlights("AA", "Gaston"));
    }

    @Test
    public void testChangeTicket() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);
        flightsAdminService.createPlane(PLANE_2, rowsData);
        List<TicketDto> tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);

        flightsAdminService.createFlight(PLANE_2, "BR", "CDG", new ArrayList<>());
        seatsAssignmentService.changeTicket("Brittu", "AA", "BR");

        Assertions.assertEquals(1, flightsAdminService.getFlight("BR").getTicketList().size());
        Assertions.assertEquals(4, flightsAdminService.getFlight("AA").getTicketList().size());
    }

    @Test
    public void testChangeTicketNoFlight() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "CDG", tickets);

        Assertions.assertThrows(RemoteException.class,
                () -> seatsAssignmentService.changeTicket("Brittu", "AA", "BR"));
    }
}
