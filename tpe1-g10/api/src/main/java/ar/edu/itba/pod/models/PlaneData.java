package ar.edu.itba.pod.models;

import ar.edu.itba.pod.constants.SeatCategory;

import java.io.Serializable;

public class PlaneData implements Serializable {
    private final int columns;
    private final int rows;

    public PlaneData(int rows, int columns) {
        this.columns = columns;
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }
}
