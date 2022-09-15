package ar.edu.itba.pod.client;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.interfaces.SeatsAssignmentServiceInterface;
import ar.edu.itba.pod.models.AvailableFlightDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ar.edu.itba.pod.constants.ActionsSeatsAssignment;
import ar.edu.itba.pod.exceptions.InvalidArgumentsException;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;

public class SeatsAssignmentClient {
    private static String serverAddress;
    private static ActionsSeatsAssignment action;
    private static String flight;
    private static String passenger;
    private static Integer row;
    private static String col;
    private static String originalFlight;
    private static final Logger logger = LoggerFactory.getLogger(SeatsAssignmentClient.class);

    private static void printError(String message){
        logger.error(message);
    }

    private static void getProperties() {
        Properties props = System.getProperties();
        serverAddress = props.getProperty("serverAddress");
        if (props.containsKey("action")) {
            action = ActionsSeatsAssignment.fromString(props.getProperty("action"));
        } else {
            action = null;
        }
        flight = props.getProperty("flight");
        passenger = props.getProperty("passenger");
        if (props.containsKey("row")) {
            row = Integer.valueOf(props.getProperty("row"));
        }
        col = props.getProperty("col");
        originalFlight = props.getProperty("originalFlight");
    }

    private static void alternativeFlights(SeatsAssignmentServiceInterface service) {
        try {
            AvailableFlightDto availableFlights = service.getAvailableFlights(flight, passenger);
            for (Map.Entry<SeatCategory, Map<String, Long>> entry : availableFlights.getSeats().entrySet()) {
                SeatCategory category = entry.getKey();
                for (Map.Entry<String, Long> count : entry.getValue().entrySet()) {
                    logger.info(availableFlights.getDestination() + " | " + count.getKey() + " | " + count.getValue() + " " + category);
                }
            }
        } catch (RemoteException ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

    private static void changeTicket(SeatsAssignmentServiceInterface service) {
        try {
            service.changeTicket(passenger,originalFlight, flight);
        } catch (RemoteException ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

    private static void changeSeat(SeatsAssignmentServiceInterface service) {
        try {
            service.changeSeat(flight, passenger, row, col);
        } catch (RemoteException ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

    private static void assignSeat(SeatsAssignmentServiceInterface service) {
        try {
            service.assignSeat(flight, passenger, row, col);
        } catch (RemoteException ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

    private static void checkSeatStatus(
            SeatsAssignmentServiceInterface service) {
        try {
            String passenger = service.checkEmptySeat(flight, row, col);
            String status = passenger == null ? "FREE" : "ASSIGNED to " + passenger;
            logger.info("Seat " + row + col + " is " + status);
        } catch (RemoteException ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

    private static void callMethodFromAction(
            SeatsAssignmentServiceInterface service) {
        switch (action) {
            case STATUS:
                if (flight == null) {
                    printError("There must be a valid flight code");
                    break;
                }
                if( row == null) {
                    printError("There must be a valid row");
                    break;
                }
                if (col == null) {
                    printError("There must be a valid column");
                    break;
                }
                checkSeatStatus(service);
                break;
            case ASSIGN:
                if (flight == null) {
                    printError("There must be a valid flight code");
                    break;
                }
                if( passenger == null) {
                    printError("There must be a valid passenger");
                    break;
                }
                if( row == null) {
                    printError("There must be a valid row");
                    break;
                }
                if (col == null) {
                    printError("There must be a valid column");
                    break;
                }
                assignSeat(service);
                break;
            case MOVE:
                if (flight == null) {
                    printError("There must be a valid flight code");
                    break;
                }
                if( passenger == null) {
                    printError("There must be a valid passenger");
                    break;
                }
                if( row == null) {
                    printError("There must be a valid row");
                    break;
                }
                if (col == null) {
                    printError("There must be a valid column");
                    break;
                }
                changeSeat(service);
                break;
            case ALTERNATIVES:
                if (flight == null) {
                    printError("There must be a valid flight code");
                    break;
                }
                if( passenger == null) {
                    printError("There must be a valid passenger");
                    break;
                }
                alternativeFlights(service);
                break;
            case CHANGE:
                if (flight == null) {
                    printError("There must be a valid flight code");
                    break;
                }
                if( passenger == null) {
                    printError("There must be a valid passenger");
                    break;
                }
                if( originalFlight == null) {
                    printError("There must be a valid original flight");
                    break;
                }
                changeTicket(service);
                break;
            default:
                logger.error("Please enter a valid action");
        }
    }

    public static void main(String[] args) {
        try {
            logger.info("tpe1-g10 Seats Assignment Client Starting ...");

            getProperties();

            if (serverAddress == null) {
                logger.error("Please enter a valid server address");
                return;
            }

            if (action == null) {
                logger.error("Please enter a valid action");
                return;
            }

            final SeatsAssignmentServiceInterface service = (SeatsAssignmentServiceInterface)
                    Naming.lookup("//" + serverAddress + "/seatsAssignmentService");

            callMethodFromAction(service);

        } catch (Exception ex) {
            logger.error(ex.getCause().getMessage());
        }
    }
}
