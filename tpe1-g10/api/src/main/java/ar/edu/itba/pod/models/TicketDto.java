package ar.edu.itba.pod.models;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.SeatDto;

import java.io.Serializable;

public class TicketDto implements Serializable {
    private final String name;
    private SeatCategory seatCategory;
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

    public String getFlightCode() {
        return flightCode;
    }
}
