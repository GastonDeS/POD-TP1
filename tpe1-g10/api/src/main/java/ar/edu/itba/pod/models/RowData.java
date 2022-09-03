package api.src.main.java.ar.edu.itba.pod.models;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;

public class RowData {
    private final SeatCategory seatCategory;
    private final int columns;

    public RowData(SeatCategory seatCategory, int columns) {
        this.seatCategory = seatCategory;
        this.columns = columns;
    }

    public SeatCategory getSeatCategory() {
        return seatCategory;
    }

    public int getColumns() {
        return columns;
    }
}
