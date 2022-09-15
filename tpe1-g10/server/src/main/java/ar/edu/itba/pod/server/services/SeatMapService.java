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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class SeatMapService implements SeatMapServiceInterface {
    private static SeatMapService instance;
    private final FlightsAdminService flightsAdminService;
    private final ReentrantReadWriteLock flightsLock;
    private final ReentrantReadWriteLock publicSeatsLock;

    public SeatMapService() {
        this.flightsAdminService = FlightsAdminService.getInstance();
        this.flightsLock = new ReentrantReadWriteLock();
        this.publicSeatsLock = new ReentrantReadWriteLock();
    }

    public static SeatMapService getInstance() {
        if (SeatMapService.instance == null)
            SeatMapService.instance = new SeatMapService();
        return SeatMapService.instance;
    }

    private Flight existsFlight(String flightCode) throws RemoteException {
        Flight flight;
        try {
            try {
                flightsLock.readLock();
                flight = flightsAdminService.getFlight(flightCode);
            } finally {
                flightsLock.readLock().lock();
            }
        } catch (RemoteException e) {
            throw new RemoteException("Error: flight with code " + flightCode + " does not exist");
        }
        return flight;
    }

    public Map<String, Map<String, SeatDto>> peekAllSeats(String flightCode) throws RemoteException {
        Flight flight;
        try {
            flightsLock.readLock();
            flight = existsFlight(flightCode);
        } finally {
            flightsLock.readLock().unlock();
        }
        return seatsToDto(flight.getPlaneSeats());
    }

    public Map<String, SeatDto> peekRowSeats(String flightCode, String rowNumber) throws RemoteException {
        Flight flight;
        try {
            flightsLock.readLock();
            flight = existsFlight(flightCode);
        } finally {
            flightsLock.readLock().unlock();
        }
        Map<String, Map<String, SeatDto>> planeMap;
        try {
            publicSeatsLock.readLock();
            planeMap = seatsToDto(flight.getPlaneSeats());
            if (planeMap.containsKey(rowNumber))
                return planeMap.get(rowNumber);
        } finally {
            publicSeatsLock.readLock().unlock();
        }
        throw new RemoteException("Error: Row number " + rowNumber + " does not exist in flight " + flightCode);
    }

    public Map<String, Map<String, SeatDto>> peekCategorySeats(String flightCode, SeatCategory category) throws RemoteException {
        Flight flight;
        try {
            flightsLock.readLock();
            flight = existsFlight(flightCode);
        } finally {
            flightsLock.readLock().unlock();
        }
        Map<String, Map<String, SeatDto>> planeMap;
        try {
            publicSeatsLock.readLock();
            planeMap = seatsToDto(flight.getPlaneSeats());
        } finally {
            publicSeatsLock.readLock().unlock();
        }
        Map<String, Map<String, SeatDto>> categoryMap = new HashMap<>();
        boolean found = false;
        try {
            publicSeatsLock.writeLock();
            for (String row : planeMap.keySet().stream().sorted().collect(Collectors.toList())) {
                if (flight.getRowCategory(row).equals(category)) {
                    found = true;
                    categoryMap.put(row, planeMap.get(row));
                } else if (found) {
                    break;
                }
            }
        } finally {
            publicSeatsLock.writeLock().unlock();
        }
        if (found)
            return categoryMap;

        throw new RemoteException("Error: Category " + category.getMessage() + " does not exist in flight " + flightCode);
    }

    private Map<String, Map<String, SeatDto>> seatsToDto(Map<String, Map<String, Seat>> seats) {
        Map<String, Map<String, SeatDto>> publicSeatsDto = new HashMap<>();
        seats.forEach((key, value) -> {
            Map<String, SeatDto> seatsDto;
            try {
                publicSeatsLock.readLock();
                seatsDto = rowSeatDtoMap(value);
            } finally {
                publicSeatsLock.readLock().unlock();
            }
            try {
                publicSeatsLock.writeLock();
                publicSeatsDto.put(key, seatsDto);
            } finally {
                publicSeatsLock.writeLock().unlock();
            }
        });
        return publicSeatsDto;
    }

    private Map<String, SeatDto> rowSeatDtoMap(Map<String, Seat> seatMap) {
        Map<String, SeatDto> rowSeatsToDto = new HashMap<>();
        seatMap.forEach((key, seat) -> {
            SeatDto seatDto = seat.toSeatDto();
            try {
                publicSeatsLock.writeLock();
                rowSeatsToDto.put(key, seatDto);
            } finally {
                publicSeatsLock.writeLock().unlock();
            }
        });
        return rowSeatsToDto;
    }


}
