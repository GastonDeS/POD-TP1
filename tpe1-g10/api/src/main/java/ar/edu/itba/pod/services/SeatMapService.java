package ar.edu.itba.pod.services;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.interfaces.SeatMapServiceInterface;
import ar.edu.itba.pod.services.FlightsAdminService;
import ar.edu.itba.pod.models.Flight;
import ar.edu.itba.pod.models.Seat;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SeatMapService implements SeatMapServiceInterface {
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
    private Flight existsFlight(String flightCode) throws RemoteException{
        Flight flight;
        try{
            flight = flightsAdminService.getFlight(flightCode);
        }catch (RemoteException e){
            throw new RemoteException("Error: flight with code " + flightCode + " does not exist");    }
        return flight;
    }
    public Map<String, Map<String, Seat>> peekAllSeats(String flightCode) throws RemoteException {
        Flight flight = existsFlight(flightCode);
        return flight.getPlaneSeats();
    }

    public Map<String, Seat> peekRowSeats(String flightCode, String rowNumber) throws RemoteException {
        Flight flight = existsFlight(flightCode);
        Map<String, Map<String, Seat>> planeMap = flight.getPlaneSeats();
        if(planeMap.containsKey(rowNumber))
            return planeMap.get(rowNumber);
        throw new RemoteException("Error: Row number " + rowNumber + " does not exist in flight " + flightCode);
    }

    public Map<String, Map<String, Seat>> peekCategorySeats(String flightCode, SeatCategory category) throws RemoteException {
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

        throw new RemoteException("Error: Category " + category.getMessage() + " does not exist in flight " + flightCode);
    }




}
