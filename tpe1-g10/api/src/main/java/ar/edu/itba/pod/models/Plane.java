package api.src.main.java.ar.edu.itba.pod.models;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plane {
    private final String name;
    private final List<RowData> rowDataList;
    private final int totalSeats;

    public Plane(String name, List<RowData> rowDataList) {
        this.name = name;
        if (rowDataList.size() <= 0 || rowDataList.size() > 25) throw new IllegalArgumentException();
        rowDataList.forEach(rowData -> { if (rowData.getColumns() <= 0) throw new IllegalArgumentException(); });
        this.rowDataList = rowDataList;
        this.totalSeats = rowDataList.stream().map(RowData::getColumns).reduce(0, Integer::sum);
    }

    public String getName() {
        return name;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public final Map<String, Seat> getSeats() {
        Map<String, Seat> seats = new HashMap<>();
        for (int j = 0; j < rowDataList.size() ; j++) {
            for (int i = 0; i < rowDataList.get(j).getColumns(); i++) {
                String place = "" + (j + 1) + "" + Character.valueOf((char) (65 + i)).toString();
                seats.put(place, new Seat(rowDataList.get(j).getSeatCategory(), place));
            }
        }
        return seats;
    }

}
