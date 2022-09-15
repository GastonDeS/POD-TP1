package ar.edu.itba.pod.server.services;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.interfaces.SeatMapServiceInterface;
import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.models.SeatDto;
import ar.edu.itba.pod.server.models.Flight;
import ar.edu.itba.pod.server.models.Seat;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SeatMapService implements SeatMapServiceInterface {
    private static SeatMapService instance;
    private final FlightsAdminService flightsAdminService;

    public SeatMapService() {
        this.flightsAdminService = FlightsAdminService.getInstance();
    }

    public static SeatMapService getInstance() {
        if (SeatMapService.instance == null)
            SeatMapService.instance = new SeatMapService();
        return SeatMapService.instance;
    }

    private Flight existsFlight(String flightCode) throws RemoteException {
        Flight flight;
        try {
            flight = flightsAdminService.getFlight(flightCode);
        } catch (RemoteException e) {
            throw new RemoteException("Error: flight with code " + flightCode + " does not exist");
        }
        return flight;
    }

    public Map<String, Map<String, SeatDto>> peekAllSeats(String flightCode) throws RemoteException {
        Flight flight = existsFlight(flightCode);
        return flight.getPlaneSeatsDto();
    }

    public Map<String, SeatDto> peekRowSeats(String flightCode, String rowNumber) throws RemoteException {
        Flight flight = existsFlight(flightCode);
        Map<String, Map<String, SeatDto>> planeMap;
        planeMap = flight.getPlaneSeatsDto();
        if (planeMap.containsKey(rowNumber))
            return planeMap.get(rowNumber);
        throw new RemoteException("Error: Row number " + rowNumber + " does not exist in flight " + flightCode);
    }

    public Map<String, Map<String, SeatDto>> peekCategorySeats(String flightCode, SeatCategory category) throws RemoteException {
        Flight flight = existsFlight(flightCode);
        Map<String, Map<String, SeatDto>> planeMap = flight.getPlaneSeatsDto();
        Map<String, Map<String, SeatDto>> categoryMap = new HashMap<>();
        boolean found = false;
        for (String row : planeMap.keySet().stream().sorted().collect(Collectors.toList())) {
            if (flight.getRowCategory(row).equals(category)) {
                found = true;
                categoryMap.put(row, planeMap.get(row));
            } else if (found) {
                break;
            }
        }
        if (found)
            return categoryMap;

        throw new RemoteException("Error: Category " + category.getMessage() + " does not exist in flight " + flightCode);
    }
}
