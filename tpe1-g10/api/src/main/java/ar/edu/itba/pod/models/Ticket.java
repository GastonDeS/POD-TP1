package api.src.main.java.ar.edu.itba.pod.models;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;

public class Ticket {
    private final String name;
    private final SeatCategory seatCategory;
    private Seat seat;
    private Flight flight;

    public Ticket(String name, SeatCategory seatCategory, Flight flight) {
        this.name = name;
        this.seatCategory = seatCategory;
        this.flight = flight;
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
}
