package ar.edu.itba.pod.server;


import ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import ar.edu.itba.pod.interfaces.SeatMapServiceInterface;
import ar.edu.itba.pod.interfaces.SeatsAssignmentServiceInterface;
import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.server.services.SeatMapService;
import ar.edu.itba.pod.server.services.SeatsAssignmentService;
import ar.edu.itba.pod.server.services.NotificationService;
import ar.edu.itba.pod.interfaces.NotificationServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        logger.info("tpe1-g10 Server Starting ...");
        try {
            final FlightAdminServiceInterface flightsAdminService = FlightsAdminService.getInstance();
            final SeatMapServiceInterface seatMapService = SeatMapService.getInstance();
            final SeatsAssignmentServiceInterface seatsAssignmentService = SeatsAssignmentService.getInstance();
            final NotificationServiceInterface notificationService = NotificationService.getInstance();

            final Registry registry = LocateRegistry.createRegistry(1099);
            final Remote remoteFlightsAdmin = UnicastRemoteObject.exportObject(flightsAdminService, 0);
            final Remote remoteSeatsAssignment = UnicastRemoteObject.exportObject(seatsAssignmentService, 0);
            final Remote remoteMapQuery = UnicastRemoteObject.exportObject(seatMapService, 0);
            final Remote remoteNotification = UnicastRemoteObject.exportObject(notificationService,0);

            registry.rebind("flightAdminService", remoteFlightsAdmin);
            logger.info("flightAdminService bound");

            registry.rebind("seatsAssignmentService", remoteSeatsAssignment);
            logger.info("seatsAssignmentService bound");

            registry.rebind("seatMapService", remoteMapQuery);
            logger.info("seatMapService bound");

            registry.rebind("notificationService", remoteNotification);
            logger.info("notificationService bound");

            logger.info("server online");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (!notificationService.awaitTermination())
                        logger.error("Could not terminate executor successfully.");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    logger.error("Could not terminate executor successfully.");
                }
            }));
        } catch (Exception ex ) {
            logger.error("An exception happened");
            ex.printStackTrace();
        }
    }
}
