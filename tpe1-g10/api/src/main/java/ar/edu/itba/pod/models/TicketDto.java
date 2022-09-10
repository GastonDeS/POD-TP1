package ar.edu.itba.pod.models;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.SeatDto;

public class TicketDto {
    private final String name;
    private SeatCategory seatCategory;
    private SeatDto seat;
    private String flightCode;

    public TicketDto(String name, SeatCategory seatCategory, String flightCode) {
        this.name = name;
        this.seatCategory = seatCategory;
        this.flightCode = flightCode;
    }

    public String getName() {
        return name;
    }

    public SeatCategory getSeatCategory() {
        return seatCategory;
    }

    public SeatDto getSeat() {
        return seat;
    }

    public String getFlightCode() {
        return flightCode;
    }
}
