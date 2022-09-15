package ar.edu.itba.pod.models;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.SeatDto;

import java.io.Serializable;
import java.util.Optional;

public class TicketDto implements Serializable {
    private final String name;
    private final SeatCategory seatCategory;
    private final String flightCode;
    private final Optional<String> seatPlace;

    public TicketDto(String name, SeatCategory seatCategory, String flightCode) {
        this.name = name;
        this.seatCategory = seatCategory;
        this.flightCode = flightCode;
        this.seatPlace = Optional.empty();
    }

    public TicketDto(String name, SeatCategory seatCategory, String flightCode, String seatPlace) {
        this.name = name;
        this.seatCategory = seatCategory;
        this.flightCode = flightCode;
        this.seatPlace = Optional.ofNullable(seatPlace);
    }

    public Optional<String> getSeatPlace() {
        return seatPlace;
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
