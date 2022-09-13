package ar.edu.itba.pod.services.utils;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.server.models.Flight;
import ar.edu.itba.pod.models.PlaneData;
import ar.edu.itba.pod.models.TicketDto;
import ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import ar.edu.itba.pod.interfaces.SeatsAssignmentServiceInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {
    final static String planeModel = "PLANE";
    final static String firstDest = "JFK";
    final static String[] names = {"Flor", "Sol", "Gasti", "Brittu", "Juanma"};
    
    public static List<TicketDto> getTickets(String flightCode) {
        List<TicketDto> tickets = new ArrayList<>();
        tickets.add(new TicketDto("Gaston", SeatCategory.ECONOMY, flightCode));
        tickets.add(new TicketDto("Brittu", SeatCategory.PREMIUM_ECONOMY, flightCode));
        tickets.add(new TicketDto("Flor", SeatCategory.ECONOMY, flightCode));
        tickets.add(new TicketDto("Juanma", SeatCategory.ECONOMY, flightCode));
        tickets.add(new TicketDto("Sol", SeatCategory.ECONOMY, flightCode));
        return tickets;
    }

    public static Map<SeatCategory, PlaneData> getPlaneDataForFlight() {
        Map<SeatCategory, ar.edu.itba.pod.models.PlaneData> planeData = new HashMap<>();
        planeData.put(SeatCategory.BUSINESS, new ar.edu.itba.pod.models.PlaneData( 3, 3));
        planeData.put(SeatCategory.PREMIUM_ECONOMY, new ar.edu.itba.pod.models.PlaneData( 4, 5));
        planeData.put(SeatCategory.ECONOMY, new ar.edu.itba.pod.models.PlaneData( 4, 7));
        return planeData;
    }

    public static Map<SeatCategory, PlaneData> getOneSeatPlaneData(SeatCategory seatCategory) {
        Map<SeatCategory, ar.edu.itba.pod.models.PlaneData> planeData = new HashMap<>();
        planeData.put(seatCategory, new ar.edu.itba.pod.models.PlaneData( 1, 1));
        return planeData;
    }
    public static void setSeatAvailability (Flight flight, int row, String column, boolean isAvailable) throws RemoteException {
        flight.getSeat(row, column).setAvailable(isAvailable, '*');
    }


    public static void fillAdminService(FlightAdminServiceInterface flightAdminService) throws RemoteException {
        Map<SeatCategory, PlaneData> planeDataMap = new HashMap<>();
        planeDataMap.put(SeatCategory.ECONOMY, new PlaneData( 21, 3));
        flightAdminService.createPlane(planeModel, planeDataMap);

        for (int i = 0; i < 50; i++) {
            List<TicketDto> ticketDtos = new ArrayList<>();
            for (int j = 0; j < 15; j++) {
                ticketDtos.add(new TicketDto(names[j%5]+j, SeatCategory.values()[j%3], "AA"+i));
            }
            flightAdminService.createFlight(planeModel, "AA"+i, firstDest, ticketDtos);
        }
    }

    public static void assignAlotOfSeats(SeatsAssignmentServiceInterface seatsAssignmentService) {
        for (int i = 0; i < 50; i++) {
            for (int j = 0, c =0,r=0; j < 15; j++, c++) {
                try {
                    seatsAssignmentService.assignSeat("AA" + i, names[j % 5] + j, r+1, "" + (char) (65 + c));
                    if (c % 3 == 2) {
                        r++;
                        c = (c + 1) % 3;
                    }
                } catch (Exception ex ){
                    System.out.println("assign failed flight: "+"AA"+i+" c: "+(char) (65 + c)+" r: "+r);
                }
            }
        }
    }
}
