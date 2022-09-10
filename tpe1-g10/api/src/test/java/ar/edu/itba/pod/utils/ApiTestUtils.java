package ar.edu.itba.pod.utils;

import ar.edu.itba.pod.models.PlaneData;
import ar.edu.itba.pod.constants.SeatCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiTestUtils {
    public static Map<SeatCategory, PlaneData> getRowDataForFlight() {
        Map<SeatCategory, PlaneData> planeData = new HashMap<>();
        planeData.put(SeatCategory.BUSINESS, new PlaneData( 3, 3));
        planeData.put(SeatCategory.PREMIUM_ECONOMY, new PlaneData( 4, 5));
        planeData.put(SeatCategory.ECONOMY, new PlaneData( 4, 7));
        return planeData;
    }
}
