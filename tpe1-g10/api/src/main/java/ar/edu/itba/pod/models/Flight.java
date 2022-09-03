package api.src.main.java.ar.edu.itba.pod.models;

import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;

import java.util.List;

public class Flight {
    private final String plane;
    private final String code;
    private final String destination;
    private final FlightStatus status;

    private final List<Ticket> ticketList;

    public Flight(String plane, String code, String destination, FlightStatus status, List<Ticket> ticketList) {
        this.plane = plane;
        this.code = code;
        this.destination = destination;
        this.status = status;
        this.ticketList = ticketList;
    }

    public String getPlane() {
        return plane;
    }

    public String getCode() {
        return code;
    }

    public String getDestination() {
        return destination;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void addTicketToFlight(Ticket ticket){
        ticketList.add(ticket);
    }
}
