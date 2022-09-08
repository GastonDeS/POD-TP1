package api.src.main.java.ar.edu.itba.pod.interfaces;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;
import api.src.main.java.ar.edu.itba.pod.models.Seat;

import java.rmi.Remote;
import java.util.Map;

public interface SeatMapServiceInterface extends Remote {

    public Map<String, Map<String, Seat>> peekAllSeats(String flightCode);
    public Map<String, Seat> peekRowSeats(String flightCode, String rowNumber);
    public Map<String, Map<String, Seat>> peekCategorySeats(String flightCode, SeatCategory category);
    }
