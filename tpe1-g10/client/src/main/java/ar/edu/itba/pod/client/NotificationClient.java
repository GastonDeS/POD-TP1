package ar.edu.itba.pod.client;

import ar.edu.itba.pod.interfaces.NotificationServiceInterface;
import ar.edu.itba.pod.interfaces.NotificationCallbackHandler;
import ar.edu.itba.pod.models.NotificationCallbackHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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

        logger.info("Server address: " + serverAddress);
        logger.info("Flight code: " + flightCode);
        logger.info("Passenger name: " + name);
    }

    private static void subscribe(NotificationServiceInterface notificationService, String flightNumber, String name) throws RemoteException {
        NotificationCallbackHandlerImpl handler = new NotificationCallbackHandlerImpl();
        UnicastRemoteObject.exportObject(handler, 0);
        notificationService.subscribe(flightNumber, name, handler);

        try {
            while (!handler.sleep()) {

            }
        } catch (InterruptedException ex) {
            logger.error("ups! interrupedException");
        }

    }

    public static void main(String[] args) {
        try {
            System.out.println("Notification client starting...");

            getProperties();
            String path = "//" + serverAddress + "/notificationService";

            final NotificationServiceInterface service = (NotificationServiceInterface) Naming.lookup(path);

            subscribe(service, flightCode, name);

            System.out.println("Notification client finished");
            System.exit(0);
        } catch (Exception ex) {
            System.out.println("An exception happened");
            ex.printStackTrace();
        }
    }
}
