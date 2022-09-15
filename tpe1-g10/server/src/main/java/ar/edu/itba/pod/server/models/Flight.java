package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.utils.SeatHelper;
import ar.edu.itba.pod.server.models.Ticket;
import ar.edu.itba.pod.server.models.Seat;
import ar.edu.itba.pod.models.SeatDto;
import ar.edu.itba.pod.models.TicketDto;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class Flight implements Serializable {
    private final String planeName;
    private final String code;
    private final String destination;
    private FlightStatus status;
    private final ReentrantReadWriteLock statusLock = new ReentrantReadWriteLock();
    private final List<Ticket> ticketList;
    private final ReentrantReadWriteLock ticketListLock = new ReentrantReadWriteLock();
    private final Map<String, Map<String, Seat>> planeSeats; // we don't synchronized because its only changed in the constructor

    public Flight(String planeName, Map<String, Map<String, Seat>> planeSeats, String code, String destination) {
        this.planeName = planeName;
        this.code = code;
        this.destination = destination;
        this.status = FlightStatus.PENDING;
        this.ticketList = new ArrayList<>();
        this.planeSeats = planeSeats;
    }

    public String getPlane() {
        return planeName;
    }

    public String getCode() {
        return code;
    }

    public String getDestination() {
        return destination;
    }

    public final FlightStatus getStatus() {
        try {
            statusLock.readLock().lock();
            return status;
        } finally {
            statusLock.readLock().unlock();
        }
    }

    public Map<String, Map<String, SeatDto>> getPlaneSeatsDto() {
        Map<String, Map<String, SeatDto>> publicSeatsDto = new HashMap<>();
        for (Map.Entry<String, Map<String, Seat>> entry : this.planeSeats.entrySet()) {
            String key = entry.getKey();
            Map<String, Seat> value = entry.getValue();
            Map<String, SeatDto> seatsDto;
            seatsDto = rowSeatDtoMap(value);
            publicSeatsDto.put(key, seatsDto);
        }
        return publicSeatsDto;
    }

    private Map<String, SeatDto> rowSeatDtoMap(Map<String, Seat> seatMap) {
        Map<String, SeatDto> rowSeatsToDto = new HashMap<>();
        for (Map.Entry<String, Seat> entry : seatMap.entrySet()) {
            String key = entry.getKey();
            Seat seat = entry.getValue();
            SeatDto seatDto = seat.toSeatDto();
            rowSeatsToDto.put(key, seatDto);
        }
        return rowSeatsToDto;
    }

    public void chargePendingStatus(FlightStatus flightStatus) throws RemoteException {
        try {
            statusLock.writeLock().lock();
            if (this.status != FlightStatus.PENDING) throw new RemoteException("Error: flight " + code + " is "+ this.status);
            this.status = flightStatus;
        } finally {
             statusLock.writeLock().unlock();
         }
    }

    public void addTicketToFlight(Ticket ticket) {
        try {
            ticketListLock.writeLock().lock();
            ticketList.add(ticket);
        } finally {
            ticketListLock.writeLock().unlock();
        }
    }

    public void swapTicket(Ticket oldTicket, Flight newFlight, SeatCategory seatCategory) {
        boolean removed;
        try {
            ticketListLock.writeLock().lock();
            removed = ticketList.remove(oldTicket);
        } finally {
            ticketListLock.writeLock().unlock();
        }
        if (removed && oldTicket.getSeat() != null) {
            String place = oldTicket.getSeat().getPlace();
            planeSeats.get(SeatHelper.getRow(place)).get(SeatHelper.getColumn(place)).setAvailable(true, '*');
        }
        oldTicket.swapTicket(newFlight.getCode(), seatCategory); // TODO check sync
        newFlight.addTicketToFlight(oldTicket);
    }

    public Ticket getPassengerTicket(String name) throws RemoteException {
        try {
            ticketListLock.readLock().lock();
            Optional<Ticket> ticket = ticketList.stream().filter(t -> t.getName().equals(name)).findFirst();
            if (!ticket.isPresent()) throw new RemoteException("Error: no ticket found for passenger " + name);
            return ticket.get();
        } finally {
            ticketListLock.readLock().unlock();
        }
    }

    public Seat getSeat(int row, String column) throws RemoteException {
        String rowS = SeatHelper.getRowFromInt(row);
        Map<String, Seat> seatMap = planeSeats.get(rowS);
        if (seatMap == null) throw new RemoteException("Seat doesn't exists row: "+rowS+ " col: "+column);
        Seat seat = seatMap.get(column);

        if (seat == null) throw new RemoteException("Seat doesn't exists row: "+rowS+" col: "+column);
        return seat;
    }

    public List<TicketDto> getTicketDtoList() {
        try {
            ticketListLock.readLock().lock();
            List<TicketDto> ticketDtoList = new ArrayList<>();
            for (Ticket ticket : ticketList) {
                TicketDto ticketDto = new TicketDto(ticket.getName(), ticket.getSeatCategory(), ticket.getFlightCode(), ticket.getSeat() != null ? ticket.getSeat().getPlace() : null);
                ticketDtoList.add(ticketDto);
            }
            return ticketDtoList;
        } finally {
            ticketListLock.readLock().unlock();
        }
    }

    public List<Ticket> getTicketListByCategorySortedByName(SeatCategory seatCategory) {
        try {
            ticketListLock.readLock().lock();
            return this.ticketList.stream().filter(ticket -> ticket.getSeatCategory() == seatCategory).sorted(Comparator.comparing(Ticket::getName)).collect(Collectors.toList());
        } finally {
            ticketListLock.readLock().unlock();
        }
    }

    public int getTicketListSize() {
        try {
            ticketListLock.readLock().lock();
            return ticketList.size();
        } finally {
            ticketListLock.readLock().unlock();
        }
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> availableSeats = new ArrayList<>();
        planeSeats.values().forEach((map) -> map.values().stream().filter(Seat::isAvailable).forEach((availableSeats::add)));
        return availableSeats;
    }

    public int getAvailableSeatsAmount() {
        try {
            ticketListLock.readLock().lock();
            return (int) planeSeats.values().stream().map(Map::values).count() - ticketList.size();
        } finally {
            ticketListLock.readLock().unlock();
        }
    }

    public SeatCategory getRowCategory(String row) {
        return planeSeats.get(row).get("A").getSeatCategory();
    }

    public int getAvailableSeatsAmountByCategory(SeatCategory category) {
        int planeSeatsCount = (Integer.parseInt(planeSeats.values().stream().map((map) ->
                        map.values().stream().filter(seat -> seat.getSeatCategory() == category).count())
                        .reduce((long) 0, (acum, value) -> (long) acum + value).toString()));
        int ticketListCategoryCount;
        try {
            ticketListLock.readLock().lock();
            ticketListCategoryCount = (int) ticketList.stream().filter(ticket -> ticket.getSeatCategory() == category).count();
        } finally {
            ticketListLock.readLock().unlock();
        }
        return planeSeatsCount - ticketListCategoryCount;
    }

    public Optional<Ticket> getTicketFromSeat(int row, String column) {
        try {
            ticketListLock.readLock().lock();
            return ticketList.stream().filter(t ->
                    t.getSeat() != null && t.getSeat().getPlace().equals("" + row + column)
            ).findFirst();
        } finally {
            ticketListLock.readLock().unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return planeName.equals(flight.planeName) && code.equals(flight.code) && destination.equals(flight.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planeName, code, destination);
    }
}
