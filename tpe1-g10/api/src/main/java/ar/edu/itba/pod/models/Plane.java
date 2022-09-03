package api.src.main.java.ar.edu.itba.pod.models;


import java.util.ArrayList;
import java.util.List;

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

    public final List<List<Seat>> getSeats() {
        List<List<Seat>> seats = new ArrayList<>();
        rowDataList.forEach(rowData -> {
            seats.add(new ArrayList<>());
            for (int i = 0; i < rowData.getColumns(); i++) {
                seats.get(seats.size()-1).add(new Seat(rowData.getSeatCategory(), ""+(seats.size())+""+Character.valueOf((char) (65+i)).toString()));
            }
        });
        return seats;
    }

}
