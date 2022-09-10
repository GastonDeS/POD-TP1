package ar.edu.itba.pod.models;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.Seat;
import ar.edu.itba.pod.models.Flight;

import java.io.Serializable;

public class Ticket implements Serializable {
    private final String name;
    private SeatCategory seatCategory;
    private Seat seat;
    private Flight flight;

    public Ticket(String name, SeatCategory seatCategory, Flight flight) {
        this.name = name;
        this.seatCategory = seatCategory;
        this.flight = flight;
    }

    private Ticket(Builder builder) {
        this.name = builder.name;
        this.seatCategory = builder.seatCategory;
        this.seat = builder.seat;
        this.flight = builder.flight;
    }

    public void setSeatCategory(SeatCategory seatCategory) {
        this.seatCategory = seatCategory;
    }

    public String getName() {
        return name;
    }

    public SeatCategory getSeatCategory() {
        return seatCategory;
    }

    public Seat getSeat() {
        return seat;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public static class Builder
    {
        private final String name;
        private SeatCategory seatCategory;
        private Seat seat;
        private Flight flight;

        public Builder(String name) {
            this.name = name;
        }

        public Builder seatCategory(SeatCategory seatCategory) {
            this.seatCategory = seatCategory;
            return this;
        }

        public Builder seat(Seat seat) {
            this.seat = seat;
            return this;
        }
        public Builder flight(Flight flight) {
            this.flight = flight;
            return this;
        }

        public Ticket build() {
            return new Ticket(this);
        }
    }
}
