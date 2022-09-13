package ar.edu.itba.pod.models;

import ar.edu.itba.pod.constants.SeatCategory;

import java.io.Serializable;

public class SeatDto implements Serializable {
    private final SeatCategory seatCategory;
    private final boolean available;
    private final char info;
    private final String place;

    public SeatDto(SeatCategory seatCategory, boolean available, char info, String place) {
        this.seatCategory = seatCategory;
        this.available = available;
        this.info = info;
        this.place = place;
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
