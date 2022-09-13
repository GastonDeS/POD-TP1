package ar.edu.itba.pod.services;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.PlaneData;
import ar.edu.itba.pod.models.TicketDto;
import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.services.utils.TestUtils;
import ar.edu.itba.pod.models.ChangedTicketsDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");

        flightsAdminService.createFlight(PLANE_1, "AA", "BA", tickets);
        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        Assertions.assertEquals(5, flightsAdminService.getFlight("AA").getTicketList().size());
    }

    @Test
    public void testCancelFlightAndFindNewSeats() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();

        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");

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
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");

        flightsAdminService.createFlight(PLANE_1, "AA", "BA", tickets);

        flightsAdminService.createPlane(PLANE_2, TestUtils.getOneSeatPlaneData(SeatCategory.BUSINESS));
        flightsAdminService.createFlight(PLANE_2, "AB", "BA", new ArrayList<>());

        flightsAdminService.cancelPendingFlight("AA");
        ChangedTicketsDto response = flightsAdminService.findNewSeatsForCancelledFlights();


        // Checks that all tickets has been swapped
        Assertions.assertEquals(5, flightsAdminService.getFlight("AA").getTicketList().size());
        Assertions.assertEquals(0, flightsAdminService.getFlight("AB").getTicketList().size());
    }

    @Test
    public void testMoveTicketsTwoMultiplePlane() throws RemoteException {
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");

        flightsAdminService.createFlight(PLANE_1, "AA", "BA", tickets);

        flightsAdminService.createPlane(PLANE_2, TestUtils.getOneSeatPlaneData(SeatCategory.ECONOMY));
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
        Map<SeatCategory, PlaneData> rowsData = TestUtils.getPlaneDataForFlight();
        flightsAdminService.createPlane(PLANE_1, rowsData);

        List<TicketDto> tickets = TestUtils.getTickets("AA");
        flightsAdminService.createFlight(PLANE_1, "AA", "BA", tickets);

        flightsAdminService.createPlane(PLANE_2, TestUtils.getOneSeatPlaneData(SeatCategory.BUSINESS));
        flightsAdminService.createFlight(PLANE_2, "AB", "BA", new ArrayList<>());

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        // Checks that all tickets has been swapped
        Assertions.assertEquals(5, flightsAdminService.getFlight("AA").getTicketList().size());
        Assertions.assertEquals(0, flightsAdminService.getFlight("AB").getTicketList().size());
    }

}
