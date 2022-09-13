package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.utils.SeatHelper;
import ar.edu.itba.pod.server.models.Ticket;
import ar.edu.itba.pod.server.models.Seat;
import ar.edu.itba.pod.models.FlightDto;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

public class Flight implements Serializable {
    private final String planeName;
    private final String code;
    private final String destination;
    private FlightStatus status;
    private final List<Ticket> ticketList;
    private final Map<String, Map<String, Seat>> planeSeats;

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
        return status;
    }

    public Map<String, Map<String, Seat>> getPlaneSeats() {
        return planeSeats;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public void addTicketToFlight(Ticket ticket) {
        ticketList.add(ticket);
    }

    public void removeTicketFromFlight(Ticket ticket) {
        boolean removed = ticketList.remove(ticket);
        if (removed && ticket.getSeat() != null) {
            String place = ticket.getSeat().getPlace();
            planeSeats.get(SeatHelper.getRow(place)).get(SeatHelper.getColumn(place)).setAvailable(true, '*');
        }
    }

    public Ticket getPassengerTicket(String name) throws RemoteException {
        Optional<Ticket> ticket = ticketList.stream().filter(t -> t.getName().equals(name)).findFirst();
        if (!ticket.isPresent()) throw new RemoteException("Error: no ticket found for passenger " + name);
        return ticket.get();
    }

    public Seat getSeat(int row, String column) throws RemoteException {
        String rowS =  (row) < 10 ? ("0"+(row)) :""+(row);
        Seat seat = planeSeats.get(rowS).get(column);
        if (seat == null) throw new RemoteException();
        return seat;
    }

    public void assignSeatToTicket(Ticket ticket, String seatCode) {
        Seat seat = planeSeats.get(SeatHelper.getRow(seatCode)).get(SeatHelper.getColumn(seatCode));
        if (seat.isAvailable()) {
            seat.setAvailable(false, '*');
            ticket.setSeat(seat);
        } else
            throw new IllegalArgumentException("seat already in use");
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> availableSeats = new ArrayList<>();
        planeSeats.values().forEach((map) -> map.values().stream().filter(Seat::isAvailable).forEach((availableSeats::add)));
        return availableSeats;
    }

    public int getAvailableSeatsAmount() {
        return (int) planeSeats.values().stream().map(Map::values).count() - ticketList.size();
    }

    public SeatCategory getRowCategory(String row) {
        return planeSeats.get(row).get("A").getSeatCategory();
    }

    public int getAvailableSeatsAmountByCategory(SeatCategory category) {
        int planeSeatsCount = (Integer.parseInt(planeSeats.values().stream().map((map) ->
                        map.values().stream().filter(seat -> seat.getSeatCategory() == category).count())
                        .reduce((long) 0, (acum, value) -> (long) acum + value).toString()));
        int ticketListCategoryCount = (int) ticketList.stream().filter(ticket -> ticket.getSeatCategory() == category).count();
        return planeSeatsCount - ticketListCategoryCount;
    }

    public Optional<Ticket> getTicketFromSeat(int row, String column) {
        return ticketList.stream().filter(t ->
            t.getSeat() != null && t.getSeat().getPlace().equals("" + row + column)
        ).findFirst();
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
