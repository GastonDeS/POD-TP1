package ar.edu.itba.pod.models;

import java.io.Serializable;
import java.util.Map;
import ar.edu.itba.pod.constants.SeatCategory;

public class FlightDto implements Serializable {

    private final String destination;
    private final Map<SeatCategory, Map<String, Long>> seats;

    public FlightDto(String destination, Map<SeatCategory, Map<String, Long>> seats) {
        this.destination = destination;
        this.seats = seats;
    }

    public String getDestination() {
        return destination;
    }

    public Map<SeatCategory, Map<String, Long>> getSeats() {
        return seats;
    }
}
