package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.TicketDto;
import ar.edu.itba.pod.server.models.Seat;

import java.io.Serializable;

public class Ticket implements Serializable {
    private final String name;
    private SeatCategory seatCategory;
    private Seat seat;
    private String flightCode;

    public Ticket(String name, SeatCategory seatCategory, String flightCode) {
        this.name = name;
        this.seatCategory = seatCategory;
        this.flightCode = flightCode;
    }

    public static Ticket createFromDto(TicketDto ticketDto) {
        return new Ticket(ticketDto.getName(), ticketDto.getSeatCategory(), ticketDto.getFlightCode());
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    private Ticket(Builder builder) {
        this.name = builder.name;
        this.seatCategory = builder.seatCategory;
        this.seat = builder.seat;
        this.flightCode = builder.flightCode;
    }

    public void setSeatCategory(SeatCategory seatCategory) {
        this.seatCategory = seatCategory;
    }

    public String getName() {
        return name;
    }

    public SeatCategory getSeatCategory() {
        return seatCategory;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
    public void setSeatAndUpdateSeat(Seat seat, boolean available, char info) {
        synchronized (this) {
            this.seat = seat;
            this.seat.setAvailable(available, info);
        }
    }

    public void swapTicket(String flightCode, SeatCategory seatCategory) {
        synchronized (this) {
            this.seat = null;
            this.flightCode = flightCode;
            this.seatCategory = seatCategory;
        }
    }


    public static class Builder
    {
        private final String name;
        private SeatCategory seatCategory;
        private Seat seat;
        private String flightCode;

        public Builder(String name) {
            this.name = name;
        }

        public Builder seatCategory(SeatCategory seatCategory) {
            this.seatCategory = seatCategory;
            return this;
        }

        public Builder seat(Seat seat) {
            this.seat = seat;
            return this;
        }
        public Builder flight(String flightCode) {
            this.flightCode = flightCode;
            return this;
        }

        public Ticket build() {
            return new Ticket(this);
        }
    }
}
