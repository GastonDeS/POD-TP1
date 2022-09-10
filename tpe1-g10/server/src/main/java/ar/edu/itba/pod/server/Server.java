package ar.edu.itba.pod.server;


import ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import ar.edu.itba.pod.interfaces.SeatMapServiceInterface;
import ar.edu.itba.pod.interfaces.SeatsAssignmentServiceInterface;
import ar.edu.itba.pod.server.services.FlightsAdminService;
import ar.edu.itba.pod.server.services.SeatMapService;
import ar.edu.itba.pod.server.services.SeatsAssignmentService;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    public static void main(String[] args) {
        System.out.println("tpe1-g10 Server Starting ...");
        try {
            final FlightAdminServiceInterface flightsAdminService = FlightsAdminService.getInstance();
            final SeatMapServiceInterface seatMapService = SeatMapService.getInstance();
            final SeatsAssignmentServiceInterface seatsAssignmentService = SeatsAssignmentService.getInstance();

            final Registry registry = LocateRegistry.createRegistry(1099);
            final Remote remoteFlightsAdmin = UnicastRemoteObject.exportObject(flightsAdminService, 0);
            final Remote remoteSeatsAssignment = UnicastRemoteObject.exportObject(seatsAssignmentService, 0);
            final Remote remoteMapQuery = UnicastRemoteObject.exportObject(seatMapService, 0);

            registry.rebind("flightAdminService", remoteFlightsAdmin);
            System.out.println("flightAdminService bound");

            registry.rebind("seatsAssignmentService", remoteSeatsAssignment);
            System.out.println("seatsAssignmentService bound");

            registry.rebind("seatMapService", remoteMapQuery);
            System.out.println("seatMapService bound");

            System.out.println("server online");
        } catch (Exception ex ) {
            System.out.println("An exception happened");
            ex.printStackTrace();
        }
    }
}
