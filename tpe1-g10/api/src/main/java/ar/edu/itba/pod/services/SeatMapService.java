package api.src.main.java.ar.edu.itba.pod.services;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Seat;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SeatMapService {
    private static SeatMapService instance;
    private final FlightsAdminService flightsAdminService;

    public SeatMapService() {
        this.flightsAdminService = FlightsAdminService.getInstance();
    }

    public static SeatMapService getInstance() {
        if(SeatMapService.instance == null)
            SeatMapService.instance = new SeatMapService();
        return SeatMapService.instance;
    }
    private Flight existsFlight(String flightCode){
        Flight flight;
        try{
            flight = flightsAdminService.getFlight(flightCode);
        }catch (RemoteException e){
            //TODO error flight doesnt exists
            throw new RuntimeException();
        }
        return flight;
    }
    public Map<String, Map<String, Seat>> peekAllSeats(String flightCode){
        Flight flight = existsFlight(flightCode);
        return flight.getPlaneSeats();
    }

    public Map<String, Seat> peekRowSeats(String flightCode, String rowNumber){
        Flight flight = existsFlight(flightCode);
        Map<String, Map<String, Seat>> planeMap = flight.getPlaneSeats();
        if(planeMap.containsKey(rowNumber))
            return planeMap.get(rowNumber);
        //TODO tirar error no existe esa fila
        throw new RuntimeException();
    }

    public Map<String, Map<String, Seat>> peekCategorySeats(String flightCode, SeatCategory category){
        Flight flight = existsFlight(flightCode);
        Map<String, Map<String, Seat>> planeMap = flight.getPlaneSeats();
        Map<String, Map<String, Seat>> categoryMap = new HashMap<>();
        boolean found = false;
        for(String row : planeMap.keySet()){
            if(flight.getRowCategory(row).equals(category)){
                found = true;
                categoryMap.put(row, planeMap.get(row));
            }
            else if(found){
                break;
            }
        }
        if(found)
            return categoryMap;
        //TODO tirar error no existe esta categoria
        throw new RuntimeException();
    }




}
