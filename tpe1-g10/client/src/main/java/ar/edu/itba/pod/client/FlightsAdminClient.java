package ar.edu.itba.pod.client;

import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import ar.edu.itba.pod.constants.ActionsFlightsAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Properties;

public class FlightsAdminClient {

    private static Logger logger = LoggerFactory.getLogger(FlightsAdminClient.class);

    private static String serverAddress;
    private static ActionsFlightsAdmin actionName;
    private static String fileName;
    private static String planeCode;

    private static void getProperties() {
        Properties props = System.getProperties();
        serverAddress = System.getProperty("serverAddress");
        actionName = ActionsFlightsAdmin.valueOf(props.getProperty("action").toUpperCase());
        fileName = System.getProperty("Path");
        planeCode = System.getProperty("flight");

        logger.info("Server address: " + serverAddress);
        logger.info("Action Name: " + actionName);
        logger.info("file Name: " + fileName);
        logger.info("Plane Code: " + planeCode);
    }

    private static void cancelMethod(FlightAdminServiceInterface service, String planeCode) {
        try {
            service.cancelPendingFlight(planeCode);
        } catch (RemoteException ex) {
            ex.getMessage();
        }
        System.out.println("Flight" + planeCode + "was CANCELLED");
    }

    private static void statusMethod(FlightAdminServiceInterface service, String planeCode) {
        FlightStatus flightStatus = FlightStatus.PENDING;
        try {
            flightStatus = service.checkFlightStatus(planeCode);
        } catch (RemoteException ex) {
            ex.getMessage();
        }
        System.out.println("Flight" + planeCode + "new status is: " + flightStatus);
    }

    private static void confirmMethod(FlightAdminServiceInterface service, String planeCode) {
        try {
            service.confirmPendingFlight(planeCode);
        } catch (RemoteException ex) {
           ex.getMessage();
        }
        System.out.println("Flight" + planeCode + "was CONFIRMED");
    }

    private static void reticketingMethod(FlightAdminServiceInterface service) {
        String answer = "";
        try {
            answer = service.findNewSeatsForCancelledFlights();
        } catch (RemoteException ex) {
            System.out.println("The reticketing could not be completed");
        }
        System.out.println(answer);
    }

    private static void callMethod(ActionsFlightsAdmin actionsFlightsAdmin, FlightAdminServiceInterface service, String fileName, String planeCode) {
        switch (actionsFlightsAdmin) {
            case CANCEL:
                cancelMethod(service, planeCode);
                break;
            case MODELS:
                break;
            case FLIGHTS:
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
            System.out.println("tpe1-g10 Client Starting ...");

            getProperties();


            final FlightAdminServiceInterface service = (FlightAdminServiceInterface) Naming.lookup("//" + serverAddress + "/flightAdminService");


            callMethod(actionName, service, fileName, planeCode);

            System.out.println("client started");
        } catch (Exception ex) {
            System.out.println("An exception happened");
            ex.printStackTrace();
        }
    }

}
