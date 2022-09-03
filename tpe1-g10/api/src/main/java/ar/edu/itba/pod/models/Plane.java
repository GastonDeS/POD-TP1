package api.src.main.java.ar.edu.itba.pod.models;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;

import java.util.ArrayList;
import java.util.List;

public class Plane {
    private final String name;
    private final int fileCount;
    private final int columnCount;
    private final List<List<Seat>> seats;

    public Plane(String name, int fileCount, int columnCount) {
        this.name = name;
        this.fileCount = fileCount;
        this.columnCount = columnCount;
        this.seats = new ArrayList<>();
        for (int i = 0; i < fileCount; i++) {
            this.seats.set(i, new ArrayList<>());
        }
        //TODO create seats with categories
    }

    public String getName() {
        return name;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public List<List<Seat>> getSeats() {
        return seats;
    }
}
