package ar.edu.itba.pod.models;

import ar.edu.itba.pod.models.Plane;
import ar.edu.itba.pod.models.RowData;
import ar.edu.itba.pod.models.Seat;
import ar.edu.itba.pod.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class PlaneTest {

    final private String PLANE_NAME = "BOEING_747";

    @Test
    public void planeCreationTest() {
        List<RowData> rowDataList = TestUtils.getRowDataForFlight();
        Plane plane = new Plane(PLANE_NAME, rowDataList);

        // Test seats name creation
        Map<String, Map<String, Seat>> seats = plane.getSeats();
        for (int j = 0 ; j < rowDataList.size() ; j++) {
            for (int i = 0; i < rowDataList.get(j).getColumns(); i++) {
                Assertions.assertNotNull(seats.get(""+(j+1)).getOrDefault(""+(char) (65 + i), null));
            }
        }

        // Test that all seats has been created
        Assertions.assertEquals(57, plane.getTotalSeats());
        // Test that the plane name has been set properly
        Assertions.assertEquals(PLANE_NAME, plane.getName());
    }

}
