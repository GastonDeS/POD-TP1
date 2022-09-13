package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.SeatDto;

import java.io.Serializable;

public class Seat implements Serializable {
    private final SeatCategory seatCategory;
    private boolean available;
    private char info;
    private final String place;

    public Seat(SeatCategory seatCategory, String place) {
        this.seatCategory = seatCategory;
        this.place = place;
        this.info = '*';
        this.available = true;
    }

    public SeatDto toSeatDto() {
        return new SeatDto(this.seatCategory, this.available, this.info, this.place);
    }

    private Seat(Builder builder) {
        this.seatCategory = builder.seatCategory;
        this.place = builder.place;
        this.info = builder.info;
        this.available = builder.available;
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

    public void setAvailable(boolean available, char initialName) {
        this.info = initialName;
        this.available = available;
    }

    public char getInfo() {
        return info;
    }

    public static class Builder
    {
        private final SeatCategory seatCategory;
        private boolean available;
        private char info;
        private final String place;

        public Builder(SeatCategory seatCategory, String place) {
            this.seatCategory = seatCategory;
            this.place = place;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        public Builder info(char info) {
            this.info = info;
            return this;
        }

        public Seat build() {
            return new Seat(this);
        }
    }

}
