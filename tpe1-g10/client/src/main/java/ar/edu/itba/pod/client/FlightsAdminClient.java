package ar.edu.itba.pod.client;

import ar.edu.itba.pod.constants.ActionsFlightsAdmin;
import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import ar.edu.itba.pod.models.AdminClientResponse;
import ar.edu.itba.pod.models.PlaneData;
import ar.edu.itba.pod.models.TicketDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;

public class FlightsAdminClient {

    private static final Logger logger = LoggerFactory.getLogger(FlightsAdminClient.class);
    private static String serverAddress;
    private static ActionsFlightsAdmin actionName;
    private static String fileName;
    private static String planeCode;

    private static void getProperties() {
        Properties props = System.getProperties();
        serverAddress = props.getProperty("serverAddress");
        if(props.containsKey("action")) {
            actionName = ActionsFlightsAdmin.valueOf(props.getProperty("action").toUpperCase());
        } else {
            actionName = null;
        }
        fileName = props.getProperty("inPath");
        planeCode = props.getProperty("flight");
    }

    private static void cancelMethod(FlightAdminServiceInterface service, String planeCode) {
        try {
            service.cancelPendingFlight(planeCode);
            logger.info("Flight " + planeCode + " was CANCELLED");

        } catch (RemoteException ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

    private static void statusMethod(FlightAdminServiceInterface service, String planeCode) {
        FlightStatus flightStatus;
        try {
            flightStatus = service.checkFlightStatus(planeCode);
            logger.info("Flight " + planeCode + " is " + flightStatus+".");
        } catch (RemoteException ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

    private static void confirmMethod(FlightAdminServiceInterface service, String planeCode) {
        try {
            service.confirmPendingFlight(planeCode);
            logger.info("Flight " + planeCode + " was CONFIRMED");
        } catch (RemoteException ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

    private static void reticketingMethod(FlightAdminServiceInterface service) {
        AdminClientResponse<TicketDto> ticketDto;
        try {
            ticketDto = service.findNewSeatsForCancelledFlights();
        } catch (RemoteException ex) {
            logger.error("The reticketing could not be completed");
            return;
        }
        logger.info(ticketDto.getSuccessAmount() + " tickets were changed.");
        ticketDto.getErrorList().forEach(ticket -> {
            logger.info("Cannot find alternative flight for " + ticket.getName() + " with Ticket " + ticket.getFlightCode());

        });
    }

    private static void uploadModels(FlightAdminServiceInterface service, String fileName) {
        File fileCsv = new File(fileName);
        try {
            Reader fileReader = new FileReader(fileCsv);
            BufferedReader br = new BufferedReader(fileReader);
            String line = br.readLine(); // skip first line
            int modelsAdded =0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                Map<SeatCategory, PlaneData> planeData = parseRowData(values[1].split(","));
                try {
                    service.createPlane(values[0], planeData);
                    modelsAdded++;
                } catch (RemoteException rex) {
                    logger.info("Cannot add model "+values[0]+".");
                }
            }
            logger.info(modelsAdded+ " models added.");
        } catch (Exception ex) {
            logger.error("File cannot be opened");
        }
    }

    private static Map<SeatCategory, PlaneData> parseRowData(String[] plane) {
        Map<SeatCategory, PlaneData> planeData = new HashMap<>();
        for (int i = 0; i < plane.length; i++) {
            String[] categoryData = plane[i].split("#");
            planeData.put(SeatCategory.valueOf(categoryData[0]), new PlaneData(Integer.parseInt(categoryData[1]), Integer.parseInt(categoryData[2])));
        }
        return planeData;
    }


    private static void uploadFlights(FlightAdminServiceInterface service, String fileName) {
        File fileCsv = new File(fileName);
        try {
            Reader fileReader = new FileReader(fileCsv);
            BufferedReader br = new BufferedReader(fileReader);
            String line = br.readLine(); // skip first line
            int flightAddedCount = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                String[] ticketsString = values.length > 3 ? values[3].split(",") : new String[]{};
                List<TicketDto> tickets = parseTickets(ticketsString, values[1]);
                try {
                    service.createFlight(values[0], values[1], values[2], tickets);
                    flightAddedCount++;
                } catch (RemoteException ex) {
                    logger.info("Cannot add flight "+values[1]+".");
                }
            }
            logger.info(flightAddedCount+" flights added.");
        } catch (Exception ex) {
            logger.error("File: " + fileName + " cannot be opened or was invalid");
        }
    }


    private static List<TicketDto> parseTickets(String[] tickets, String flightCode) {
        List<TicketDto> ticketList = new ArrayList<>();
        for (int i = 0; i < tickets.length; i++) {
            String[] ticketData = tickets[i].split("#");
            ticketList.add(new TicketDto(ticketData[1], SeatCategory.valueOf(ticketData[0]), flightCode));
        }
        return ticketList;
    }


    private static void callMethod(ActionsFlightsAdmin actionsFlightsAdmin, FlightAdminServiceInterface service, String fileName, String planeCode) throws RemoteException {
        switch (actionsFlightsAdmin) {
            case CANCEL:
                if (planeCode == null) {
                    logger.error("There must be a valid flight code");
                    break;
                }
                cancelMethod(service, planeCode);
                break;
            case MODELS:
                if (fileName == null) {
                    logger.error("There must be a valid filename");
                    break;
                }
                uploadModels(service, fileName);
                break;
            case FLIGHTS:
                if (fileName == null) {
                    logger.error("There must be a valid filename");
                    break;
                }
                uploadFlights(service, fileName);
                break;
            case STATUS:
                if (planeCode == null) {
                    logger.error("There must be a valid flight code");
                    break;
                }
                statusMethod(service, planeCode);
                break;
            case CONFIRM:
                if (planeCode == null) {
                    logger.error("There must be a valid flight code");
                    break;
                }
                confirmMethod(service, planeCode);
                break;
            case RETICKETING:
                reticketingMethod(service);
                break;
            default:
                logger.error("Please enter a valid action");
        }
    }

    public static void main(String[] args) {
        try {
            logger.info("tpe1-g10 Client Starting ...");

            getProperties();

            if(actionName == null){
                logger.error("Please enter a valid action");
                return;
            }

            if (serverAddress == null) {
                logger.error("The server address must be valid");
                return;
            }

            if (fileName != null && planeCode != null) {
                logger.error("You cannot consult for a file and a flight code at the same time");
                return;
            }

            final FlightAdminServiceInterface service = (FlightAdminServiceInterface) Naming.lookup("//" + serverAddress + "/flightAdminService");

            logger.info("client started");

            callMethod(actionName, service, fileName, planeCode);

        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

}
