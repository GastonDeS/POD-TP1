package ar.edu.itba.pod.client;

import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import ar.edu.itba.pod.constants.ActionsFlightsAdmin;
import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.PlaneData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
        actionName = ActionsFlightsAdmin.valueOf(props.getProperty("action").toUpperCase());
        fileName = props.getProperty("Path");
        planeCode = props.getProperty("flight");
    }

    private static void cancelMethod(FlightAdminServiceInterface service, String planeCode) {
        try {
            service.cancelPendingFlight(planeCode);
        } catch (RemoteException ex) {
            ex.getMessage();
        }
        logger.info("Flight " + planeCode + " was CANCELLED");
    }

    private static void statusMethod(FlightAdminServiceInterface service, String planeCode) {
        FlightStatus flightStatus = FlightStatus.PENDING;
        try {
            flightStatus = service.checkFlightStatus(planeCode);
        } catch (RemoteException ex) {
            ex.getMessage();
        }
        logger.info("Flight" + planeCode + " new status is: " + flightStatus);
    }

    private static void confirmMethod(FlightAdminServiceInterface service, String planeCode) {
        try {
            service.confirmPendingFlight(planeCode);
        } catch (RemoteException ex) {
           ex.getMessage();
        }
        logger.info("Flight " + planeCode + " was CONFIRMED");
    }

    private static void reticketingMethod(FlightAdminServiceInterface service) {
        String answer = "";
        try {
            answer = service.findNewSeatsForCancelledFlights();
        } catch (RemoteException ex) {
            logger.error("The reticketing could not be completed");
        }
        logger.info(answer);
    }

    private static void uploadModels(FlightAdminServiceInterface service, String fileName) {
        File fileCsv = new File(fileName);
        try {
            Reader fileReader = new FileReader(fileCsv);
            BufferedReader br = new BufferedReader(fileReader);
            String line = br.readLine(); // skip first line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                Map<SeatCategory, PlaneData> planeData = parseRowData(values[1].split(","));
                service.createPlane(values[0], planeData);
            }

        } catch (Exception ex) {
            logger.error("File cannot be opened");
        }



    }

    private static Map<SeatCategory, PlaneData> parseRowData(String[] rowsData) {
        Map<SeatCategory, PlaneData> planeData = new HashMap<>();
        for (int i = 0; i < rowsData.length; i++) {
            String[] rowData = rowsData[i].split("#");
            planeData.put(SeatCategory.valueOf(rowData[0]), new PlaneData( Integer.parseInt(rowsData[1]), Integer.parseInt(rowData[2])));
        }
        return planeData;
    }

    private static void uploadFlights(FlightAdminServiceInterface service, String fileName) {

    }

    private static void callMethod(ActionsFlightsAdmin actionsFlightsAdmin, FlightAdminServiceInterface service, String fileName, String planeCode) {
        switch (actionsFlightsAdmin) {
            case CANCEL:
                cancelMethod(service, planeCode);
                break;
            case MODELS:
                uploadModels(service, fileName);
                break;
            case FLIGHTS:
                uploadFlights(service, fileName);
                break;
            case STATUS:
                statusMethod(service, planeCode);
                break;
            case CONFIRM:
                confirmMethod(service, planeCode);
                break;
            case RETICKETING:
                reticketingMethod(service);
                break;
        }
    }

    public static void main(String[] args) {
        try {
            logger.info("tpe1-g10 Client Starting ...");

            getProperties();


            final FlightAdminServiceInterface service = (FlightAdminServiceInterface) Naming.lookup("//" + serverAddress + "/flightAdminService");


            callMethod(actionName, service, fileName, planeCode);

            logger.info("client started");
        } catch (Exception ex) {
            logger.error("An exception happened");
            ex.printStackTrace();
        }
    }

}
