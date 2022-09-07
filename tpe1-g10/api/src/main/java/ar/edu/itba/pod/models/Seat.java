package api.src.main.java.ar.edu.itba.pod.models;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;

import java.io.Serializable;

public class Seat implements Serializable {
    private final SeatCategory seatCategory;
    private boolean available;
    private final String place;

    public Seat(SeatCategory seatCategory, String place) {
        this.seatCategory = seatCategory;
        this.place = place;
        this.available = true;
    }

    public SeatCategory getSeatCategory() {
        return seatCategory;
    }

    public String getPlace() {
        return place;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
