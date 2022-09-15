package server.src.test.java.ar.edu.itba.pod.services;

import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.server.services.SeatMapService;
import ar.edu.itba.pod.services.utils.TestUtils;
import ar.edu.itba.pod.models.SeatDto;
import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.PlaneData;
import ar.edu.itba.pod.server.services.SeatsAssignmentService;
import ar.edu.itba.pod.utils.SeatHelper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SeatMapServiceTest {

    private final static FlightsAdminService adminService = FlightsAdminService.getInstance();
    private final static SeatsAssignmentService seatAssignmentService = SeatsAssignmentService.getInstance();
    private final static SeatMapService service = SeatMapService.getInstance();

    private final static String PLANE = "BOEING_747";

    @BeforeEach
    public void beforeAll() throws RemoteException {
        adminService.restart();
        adminService.createPlane(PLANE, TestUtils.getPlaneDataForFlight());
        adminService.createFlight(PLANE, "AA", "JFK", TestUtils.getTickets("AA"));

        seatAssignmentService.assignSeat("AA","Sol", 10, "A");
    }

    @Test
    public void testAllSeats() throws RemoteException {
        Map<String, Map<String, SeatDto>> map = service.peekAllSeats("AA");

        Map<SeatCategory, PlaneData> mockMap = TestUtils.getPlaneDataForFlight();

        int i =0;
        for (SeatCategory key : SeatCategory.values()) {
            PlaneData value = mockMap.get(key);
            if (value == null) continue;
            for (int w =0; w < value.getRows(); w++, i++) {
                String rowS = SeatHelper.getRowFromInt(i);
                Assertions.assertTrue(map.containsKey(rowS));
                for (int j = 0; j < value.getColumns(); j++) {
                    String col = "" + (char) (65 + j);
                    Assertions.assertTrue(map.get(rowS).containsKey(col));
                    if (i+1 == 10 && j == 0) {
                        Assertions.assertEquals( 'S', map.get(rowS).get(col).getInfo());
                    }
                }
            }
        }
    }

    @Test
    public void testGetRow() throws RemoteException {

        Map<String, SeatDto> map = service.peekRowSeats("AA",10);

        map.values().forEach(seatDto -> {
            Assertions.assertEquals("10", SeatHelper.getRow(seatDto.getPlace()));
            if (SeatHelper.getColumn(seatDto.getPlace()).equals("A")) {
                Assertions.assertEquals( 'S', seatDto.getInfo());
            }
        });
    }

    @Test void testGetCategory() throws RemoteException {
        Map<String, Map<String, SeatDto>> map = service.peekCategorySeats("AA",SeatCategory.BUSINESS);

        Assertions.assertEquals(map.values().size(), TestUtils.getPlaneDataForFlight().get(SeatCategory.BUSINESS).getRows());
        map.values().forEach( stringSeatDtoMap -> {
            Assertions.assertEquals(stringSeatDtoMap.values().size(), TestUtils.getPlaneDataForFlight().get(SeatCategory.BUSINESS).getColumns());
            stringSeatDtoMap.values().forEach(seatDto -> {
                Assertions.assertEquals(SeatCategory.BUSINESS, seatDto.getSeatCategory());
            });
        });

    }
}
