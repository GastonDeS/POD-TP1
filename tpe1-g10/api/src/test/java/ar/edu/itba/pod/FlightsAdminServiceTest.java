package api.src.test.java.ar.edu.itba.pod;

import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Plane;
import api.src.main.java.ar.edu.itba.pod.models.RowData;
import api.src.main.java.ar.edu.itba.pod.services.FlightsAdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.List;

public class FlightsAdminServiceTest {

    @Test
    public void testCancelWithNoPlaceToGo() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = TestUtils.getPlane(rowsData);
        Flight flight = new Flight(plane, "AA" , "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);

        FlightsAdminService flightsAdminService = FlightsAdminService.getInstance();

        flightsAdminService.addPlaneModel(plane);
        flightsAdminService.addFlight(flight);
        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        Assertions.assertEquals(5, flight.getTicketList().size());
    }

    @Test
    public void testCancelFlightAndFindNewSeats() throws RemoteException {
        List<RowData> rowsData = TestUtils.getRowDataForFlight();
        Plane plane = TestUtils.getPlane(rowsData);
        Flight flight = new Flight(plane, "AA" , "mardel", "BA");
        TestUtils.fillFlightWithPassengers(flight);

        Flight flight2 = new Flight(plane, "AB" , "mardel", "BA");

        FlightsAdminService flightsAdminService = FlightsAdminService.getInstance();

        flightsAdminService.addPlaneModel(plane);

        flightsAdminService.addFlight(flight);
        flightsAdminService.addFlight(flight2);

        flightsAdminService.cancelPendingFlight("AA");
        flightsAdminService.findNewSeatsForCancelledFlights();

        Assertions.assertEquals(0, flight.getTicketList().size());
    }

}
