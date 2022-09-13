package ar.edu.itba.pod.models;
import ar.edu.itba.pod.models.TicketDto;

import java.util.List;

public class ChangedTicketsDto {

    private List<TicketDto> ticketDtoList;
    private int ticketsChangedAmount;

    public ChangedTicketsDto(List<TicketDto> ticketDtoList, int ticketsChangedAmount) {
        this.ticketDtoList = ticketDtoList;
        this.ticketsChangedAmount = ticketsChangedAmount;
    }

    public List<TicketDto> getTicketDtoList() {
        return ticketDtoList;
    }

    public int getTicketsChangedAmount() {
        return ticketsChangedAmount;
    }
}
