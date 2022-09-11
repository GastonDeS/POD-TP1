package ar.edu.itba.pod.models;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.Seat;

import java.io.Serializable;

public class SeatDto implements Serializable {
    private final SeatCategory seatCategory;
    private final boolean available;
    private final char info;
    private final String place;

    public SeatDto(Seat seat) {
        this.seatCategory = seat.getSeatCategory();
        this.place = seat.getPlace();
        this.available = seat.isAvailable();
        this.info = seat.getInfo();
    }

    public SeatCategory getSeatCategory() {
        return seatCategory;
    }

    public boolean isAvailable() {
        return available;
    }

    public char getInfo() {
        return info;
    }

    public String getPlace() {
        return place;
    }
}
