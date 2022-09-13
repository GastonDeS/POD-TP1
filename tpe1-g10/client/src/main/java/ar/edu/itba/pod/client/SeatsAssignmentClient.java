package ar.edu.itba.pod.client;

import ar.edu.itba.pod.interfaces.SeatsAssignmentServiceInterface;
import ar.edu.itba.pod.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.utils.SeatsAssignmentClientParser;
import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.FlightDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Map;

public class SeatsAssignmentClient {
    private static final Logger logger = LoggerFactory.getLogger(SeatsAssignmentClient.class);

    private static void alternativeFlights(
            SeatsAssignmentServiceInterface service,
            SeatsAssignmentClientParser parser) throws RemoteException {
        FlightDto availableFlights = service.getAvailableFlights(parser.getFlight(), parser.getPassenger());
        for (Map.Entry<SeatCategory, Map<String, Long>> entry : availableFlights.getSeats().entrySet()) {
            SeatCategory category = entry.getKey();
            for (Map.Entry<String, Long> count : entry.getValue().entrySet()) {
                System.out.println(availableFlights.getDestination() + " | " + count.getKey() + " | " + count.getValue() + " " + category);
            }
        }
    }

    private static void changeTicket(
            SeatsAssignmentServiceInterface service,
            SeatsAssignmentClientParser parser) throws RemoteException {
        service.changeTicket(parser.getPassenger(), parser.getOriginalFlight(), parser.getFlight());
    }

    private static void changeSeat(
            SeatsAssignmentServiceInterface service,
            SeatsAssignmentClientParser parser) throws RemoteException {
        service.changeSeat(parser.getFlight(), parser.getPassenger(), parser.getRow(), parser.getCol());
    }

    private static void assignSeat(
            SeatsAssignmentServiceInterface service,
            SeatsAssignmentClientParser parser) throws RemoteException {
        service.assignSeat(parser.getFlight(), parser.getPassenger(), parser.getRow(), parser.getCol());
    }

    private static void checkSeatStatus(
            SeatsAssignmentServiceInterface service,
            SeatsAssignmentClientParser parser) throws RemoteException {
        String passenger = service.checkEmptySeat(parser.getFlight(), parser.getRow(), parser.getCol());
        String status = passenger == null ? "FREE" : "ASSIGNED to " + passenger;
        System.out.println("Seat " + parser.getRow() + parser.getCol() + " is " + status);
    }

    private static void callMethodFromAction(
            SeatsAssignmentClientParser parser,
            SeatsAssignmentServiceInterface service) throws RemoteException {
        switch (parser.getAction()) {
            case STATUS:
                checkSeatStatus(service, parser);
            case ASSIGN:
                assignSeat(service, parser);
            case MOVE:
                changeSeat(service, parser);
            case ALTERNATIVES:
                alternativeFlights(service, parser);
            case CHANGE:
                changeTicket(service, parser);
        }
    }

    public static void main(String[] args) {
        try {
            logger.info("tpe1-g10 Seats Assignment Client Starting ...");

            SeatsAssignmentClientParser parser = new SeatsAssignmentClientParser();

            try {
                parser.parseArguments();
            } catch (InvalidArgumentsException e) {
                System.out.println(e.getMessage());
                return;
            }

            final SeatsAssignmentServiceInterface service = (SeatsAssignmentServiceInterface)
                    Naming.lookup("//" + parser.getServerAddress() + "/seatsAssignmentService");

            callMethodFromAction(parser, service);

        } catch (Exception ex) {
            logger.info("An exception happened");
            ex.printStackTrace();
        }
    }
}
