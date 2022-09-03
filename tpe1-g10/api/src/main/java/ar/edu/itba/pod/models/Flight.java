package api.src.main.java.ar.edu.itba.pod.models;

import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;

public class Flight {
    private final String plane;
    private final String code;
    private final String destination;
    private final FlightStatus status;

    public Flight(String plane, String code, String destination, FlightStatus status) {
        this.plane = plane;
        this.code = code;
        this.destination = destination;
        this.status = status;
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
}
