package ar.edu.itba.pod.models;

import ar.edu.itba.pod.constants.SeatCategory;

import java.io.Serializable;

public class RowData implements Serializable {
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
