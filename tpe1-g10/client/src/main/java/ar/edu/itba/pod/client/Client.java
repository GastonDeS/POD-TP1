package ar.edu.itba.pod.client;

import ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import ar.edu.itba.pod.constants.ActionsFlightsAdmin;

import java.rmi.Naming;
import java.util.Properties;

public class Client {

    private static String serverAddress;
    private static ActionsFlightsAdmin actionName;
    private static String fileName;
    private static String planeCode;

    public static void main(String[] args) {
        try {
            System.out.println("tpe1-g10 Client Starting ...");
            Properties props = System.getProperties();
            serverAddress = System.getProperty("serverAddress");
            System.out.println("Server address: " + serverAddress);
            actionName = ActionsFlightsAdmin.valueOf(props.getProperty("action").toUpperCase());
            fileName = System.getProperty("Path");
            planeCode = System.getProperty("flight");

            System.out.println("Server address: " + serverAddress);
            System.out.println("Action: " + actionName);
            System.out.println("File Path: " + fileName);
            System.out.println("Flight code: " + planeCode);

            final FlightAdminServiceInterface service = (FlightAdminServiceInterface) Naming.lookup("//127.0.0.1:1099/flightAdminService");

            System.out.println("client started");
        } catch (Exception ex) {
            System.out.println("An exception happened");
            ex.printStackTrace();
        }
    }
}
