package api.src.main.java.ar.edu.itba.pod.models;

import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Flight {
    private final Plane plane;
    private final String code;
    private final String origin;
    private final String destination;
    private FlightStatus status;

    private final List<Ticket> ticketList;
    private final Map<String, Seat> planeSeats;

    public Flight(Plane plane, String code, String origin, String destination) {
        this.plane = plane;
        this.code = code;
        this.origin = origin;
        this.destination = destination;
        this.status = FlightStatus.PENDING;
        this.ticketList = new ArrayList<>();
        this.planeSeats = plane.getSeats();
    }

    public Plane getPlane() {
        return plane;
    }

    public String getCode() {
        return code;
    }

    public String getDestination() {
        return destination;
    }

    public String getOrigin() {
        return origin;
    }

    public final FlightStatus getStatus() {
        return status;
    }

    public Map<String, Seat> getPlaneSeats() {
        return planeSeats;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public void addTicketToFlight(Ticket ticket){
        ticketList.add(ticket);
    }

    public void removeTicketFromFlight(Ticket ticket) {
        boolean removed = ticketList.remove(ticket);
        if (removed && ticket.getSeat() != null) {
            planeSeats.get(ticket.getSeat().place).setAvailable(true);
        }
    }

    public Ticket getPassengerTicket(String name) throws RemoteException {
        Optional<Ticket> ticket = ticketList.stream().filter(t -> t.getName().equals(name)).findFirst();
        if (!ticket.isPresent()) throw new RemoteException();
        return ticket.get();
    }

    public Seat getSeat(int row, String column) throws RemoteException {
        Seat seat = planeSeats.get("" + row + column);
        if (seat == null) throw new RemoteException();
        return seat;
    }

    public void assignSeatToTicket(Ticket ticket, String seatCode) {
        Seat seat = planeSeats.get(seatCode);
        if (seat.isAvailable()) {
            seat.setAvailable(false);
            ticket.setSeat(seat);
        } else
            throw new IllegalArgumentException("seat already in use");
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public List<Seat> getAvailableSeats() {
        return planeSeats.values().stream().filter(Seat::isAvailable).collect(Collectors.toList());
    }

    public int getAvailableSeatsAmount() {
        return plane.getTotalSeats() - ticketList.size();
    }
}
