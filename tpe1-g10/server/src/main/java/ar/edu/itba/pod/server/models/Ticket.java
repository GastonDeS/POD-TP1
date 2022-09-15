package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.TicketDto;
import ar.edu.itba.pod.server.models.Seat;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Ticket implements Serializable {
    private final String name;
    private SeatCategory seatCategory;
    private Seat seat;
    private String flightCode;
    private final ReentrantReadWriteLock seatCategoryLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock flightCodeLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock seatLock = new ReentrantReadWriteLock();

    public Ticket(String name, SeatCategory seatCategory, String flightCode) {
        this.name = name;
        this.seatCategory = seatCategory;
        this.flightCode = flightCode;
    }

    public static Ticket createFromDto(TicketDto ticketDto) {
        return new Ticket(ticketDto.getName(), ticketDto.getSeatCategory(), ticketDto.getFlightCode());
    }

    public  String getFlightCode() {
        try {
            flightCodeLock.readLock().lock();
            return flightCode;
        } finally {
            flightCodeLock.readLock().unlock();
        }
    }

    public String getName() {
        return name;
    }

    public SeatCategory getSeatCategory() {
        try {
            seatCategoryLock.readLock().lock();
            return seatCategory;
        } finally {
            seatCategoryLock.readLock().unlock();
        }
    }

    public Seat getSeat() {
        try {
            seatLock.readLock().lock();
            return seat;
        } finally {
            seatLock.readLock().unlock();
        }
    }

    public void setSeatAndUpdateSeat(Seat newSeat, boolean available, char info) {
        try {
            seatLock.writeLock().lock();
            if (this.seat != null) this.seat.setAvailable(true, '*');
            this.seat = newSeat;
            if (newSeat != null) this.seat.setAvailable(available, info);
        } finally {
            seatLock.writeLock().unlock();
        }
    }

    public void swapTicket(String flightCode, SeatCategory seatCategory) {
        try {
            flightCodeLock.writeLock().lock();
            seatCategoryLock.writeLock().lock();
            this.setSeatAndUpdateSeat(null, true, '*');
            this.flightCode = flightCode;
            this.seatCategory = seatCategory;
        } finally {
            seatCategoryLock.writeLock().unlock();
            flightCodeLock.writeLock().unlock();
        }
    }

    private Ticket(Builder builder) {
        this.name = builder.name;
        this.seatCategory = builder.seatCategory;
        this.seat = builder.seat;
        this.flightCode = builder.flightCode;
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
