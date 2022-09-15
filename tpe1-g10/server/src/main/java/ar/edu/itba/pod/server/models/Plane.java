package ar.edu.itba.pod.server.models;


import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.PlaneData;
import ar.edu.itba.pod.server.models.Seat;
import ar.edu.itba.pod.utils.SeatHelper;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plane implements Serializable {
    private final String name;
    private final Map<SeatCategory, PlaneData> planeDataMap;
    private final int totalSeats;

    public Plane(String name, Map<SeatCategory, PlaneData> planeDataMap) throws RemoteException {
        this.name = name;
        List<PlaneData> planeDataList = new ArrayList<>(planeDataMap.values());
        int rows = 0;
        int totalSeats = 0;
        for (PlaneData planeData : planeDataList) {
            if (planeData.getColumns() <= 0) throw new RemoteException("The columns has to have at least one Seat");
            if (planeData.getRows() < 0) throw new RemoteException("A SeatCategory rows amount cannot be negative");
            rows += 1;
            totalSeats += planeData.getRows() * planeData.getColumns();
        }
        if (rows <= 0 || rows > 25) throw new RemoteException("The row amount has to be between 0 and 25");


        this.planeDataMap = planeDataMap;
        this.totalSeats = totalSeats;
    }

    public String getName() {
        return name;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public final Map<String, Map<String, Seat>> getSeats() {
        Map<String, Map<String, Seat>> seats = new HashMap<>();
        int i =0;
        for (SeatCategory key : SeatCategory.values()) {
            PlaneData value = planeDataMap.get(key);
            if (value == null) continue;
            for (int w =0; w < value.getRows(); w++, i++) {
                String row = SeatHelper.getRowFromInt(i+1);
                seats.put(row, new HashMap<>());
                for (int j = 0; j < value.getColumns(); j++) {
                    String place = row + (char) (65 + j);
                    seats.get(row).put("" + (char) (65 + j), new Seat(key, place));
                }
            }
        }
        return seats;
    }

}
