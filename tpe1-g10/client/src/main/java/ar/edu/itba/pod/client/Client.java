package ar.edu.itba.pod.client;


import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;
import api.src.main.java.ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Plane;
import api.src.main.java.ar.edu.itba.pod.models.RowData;

import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

public class Client {

    public static void main(String[] args) {
        try {
            System.out.println("tpe1-g10 Client Starting ...");

            final FlightAdminServiceInterface service = (FlightAdminServiceInterface) Naming.lookup("//127.0.0.1:1099/flightAdminService");


            List<RowData> rowData = new ArrayList<>();
            rowData.add(new RowData(SeatCategory.BUSINESS, 3));
            Plane plane = service.createPlane("Gasti plane", rowData);

            Flight flight = service.createFlight(plane, "A", "A", "B");

            Flight flight1 = service.getFlight("A");
            System.out.println(flight1.getTicketList());

            System.out.println("client started");
        } catch (Exception ex) {
            System.out.println("An exception happened");
            ex.printStackTrace();
        }
    }
}
