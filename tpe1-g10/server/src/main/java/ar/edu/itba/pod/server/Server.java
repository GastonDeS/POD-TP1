package ar.edu.itba.pod.server;


import api.src.main.java.ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import api.src.main.java.ar.edu.itba.pod.services.FlightsAdminService;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

    public static void main(String[] args) {
        System.out.println("tpe1-g10 Server Starting ...");
        try {
            final FlightAdminServiceInterface flightsAdminService = FlightsAdminService.getInstance();

            final Registry registry = LocateRegistry.createRegistry(1099);
            final Remote remote = UnicastRemoteObject.exportObject(flightsAdminService, 0);

            registry.rebind("flightAdminService", remote);
            System.out.println("flightAdminService bound");


            System.out.println("server online");
        } catch (Exception ex ) {
            System.out.println("An exception happened");
            ex.printStackTrace();
        }
    }
}
