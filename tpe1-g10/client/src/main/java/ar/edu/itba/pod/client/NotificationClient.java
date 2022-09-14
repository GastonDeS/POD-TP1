package ar.edu.itba.pod.client;

import ar.edu.itba.pod.interfaces.NotificationServiceInterface;
import ar.edu.itba.pod.models.NotificationCallbackHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Properties;

public class NotificationClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationClient.class);
    private static String serverAddress;
    private static String flightCode;
    private static String name;

    private static void getProperties() {
        Properties props = System.getProperties();
        serverAddress = props.getProperty("serverAddress");
        flightCode = props.getProperty("flight");
        name = props.getProperty("passenger");
    }

    private static void subscribe(NotificationServiceInterface notificationService, String flightNumber, String name) {


        NotificationCallbackHandlerImpl handler = new NotificationCallbackHandlerImpl();

        try {
            if(flightNumber == null){
                logger.error("There must be a valid flight code");
                return;
            }
            if (name == null) {
                logger.error("There must be a valid name");
                return;
            }
            notificationService.subscribe(flightNumber, name, handler);
            while (!handler.isFinished()) {

            }
        } catch (RemoteException e) {
            logger.error("An exception happened");
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Notification client starting...");

            getProperties();
            String path = "//" + serverAddress + "/notificationService";

            final NotificationServiceInterface service = (NotificationServiceInterface) Naming.lookup(path);

            subscribe(service, flightCode, name);

            System.out.println("Notification client started");
        } catch (Exception ex) {
            System.out.println("An exception happened");
            ex.printStackTrace();
        }
    }
}
