package ar.edu.itba.pod.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ar.edu.itba.pod.services.utils.TestUtils;
import ar.edu.itba.pod.utils.SeatHelper;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.server.models.Plane;
import ar.edu.itba.pod.server.models.Seat;
import ar.edu.itba.pod.models.PlaneData;

import java.rmi.RemoteException;
import java.util.Map;

public class PlaneTest {

    final private String PLANE_NAME = "BOEING_747";

    @Test
    public void planeCreationTest() throws RemoteException {
        Map<SeatCategory, PlaneData> planeDataMap = TestUtils.getPlaneDataForFlight();
        Plane plane = null;
        plane = new Plane(PLANE_NAME, planeDataMap);

        // Test seats name creation
        Map<String, Map<String, Seat>> seats = plane.getSeats();
        int i=0;
        for (SeatCategory value : SeatCategory.values()) {
            for (int w = 0; w < planeDataMap.get(value).getRows(); w++, i++) {
                String row = SeatHelper.getRowFromInt(i+1);
                for (int j = 0; j < planeDataMap.get(value).getRows(); j++) {
                    Assertions.assertEquals( value,seats.get(row).getOrDefault("" + (char) (65 + j), null).getSeatCategory());
                }
            }
        }

        // Test that all seats has been created
        Assertions.assertEquals(57, plane.getTotalSeats());
        // Test that the plane name has been set properly
        Assertions.assertEquals(PLANE_NAME, plane.getName());
    }

}
