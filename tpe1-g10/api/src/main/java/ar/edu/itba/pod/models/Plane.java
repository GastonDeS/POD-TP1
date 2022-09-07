package api.src.main.java.ar.edu.itba.pod.models;


import api.src.main.java.ar.edu.itba.pod.utils.SeatHelper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plane implements Serializable {
    private final String name;
    private final List<RowData> rowDataList;
    private final int totalSeats;

    public Plane(String name, List<RowData> rowDataList) {
        this.name = name;
        if (rowDataList.size() <= 0 || rowDataList.size() > 25) throw new IllegalArgumentException();
        rowDataList.forEach(rowData -> {
            if (rowData.getColumns() <= 0) throw new IllegalArgumentException();
        });
        this.rowDataList = rowDataList;
        this.totalSeats = rowDataList.stream().map(RowData::getColumns).reduce(0, Integer::sum);
    }

    public String getName() {
        return name;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public final Map<String, Map<String, Seat>> getSeats() {
        Map<String, Map<String, Seat>> seats = new HashMap<>();
        for (int j = 0; j < rowDataList.size(); j++) {
            Map<String, Seat> rowHashMap = new HashMap<>();
            String row = ""+(j+1);
            seats.put(row, rowHashMap);
            for (int i = 0; i < rowDataList.get(j).getColumns(); i++) {
                String place = "" + (j + 1) + (char) (65 + i);
                seats.get(row).put(SeatHelper.getColumn(place), new Seat(rowDataList.get(j).getSeatCategory(), place));
            }
        }
        return seats;
    }
}
