package client.src.main.java.ar.edu.itba.pod.client;

import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;
import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;
import api.src.main.java.ar.edu.itba.pod.interfaces.FlightAdminServiceInterface;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Plane;
import api.src.main.java.ar.edu.itba.pod.models.RowData;
import api.src.main.java.ar.edu.itba.pod.services.FlightsAdminService;
import client.src.main.java.ar.edu.itba.pod.constants.ActionsFlightsAdmin;
import client.src.main.java.ar.edu.itba.pod.utils.ParseArgsHelper;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class FlightsAdminClient {

    private void cancelMethod(FlightsAdminService service, String planeCode) {
        try {
            service.cancelPendingFlight(planeCode);
        } catch (RemoteException ex){
            System.out.println("Flight" + planeCode + "does not exist");
            System.out.println("Flight" + planeCode + "was already CANCELLED");
        }
        System.out.println("Flight" + planeCode + "was CANCELLED");
    }

    private void statusMethod(FlightsAdminService service, String planeCode){
        FlightStatus flightStatus = FlightStatus.PENDING;
        try {
            flightStatus = service.checkFlightStatus(planeCode);
        } catch (RemoteException ex) {
            System.out.println("Error");
        }
        System.out.println("Flight" + planeCode + "new status is: " + flightStatus);
    }

    private void confirmMethod(FlightsAdminService service, String planeCode){
        try {
            service.confirmPendingFlight(planeCode);
        } catch (RemoteException ex) {
            System.out.println("Flight" + planeCode + "does not exist");
            System.out.println("Flight" + planeCode + "was already CONFIRMED");
        }
        System.out.println("Flight" + planeCode + "was CONFIRMED");
    }

    private void callMethod(ActionsFlightsAdmin actionsFlightsAdmin, FlightsAdminService service, String planeCode) throws RemoteException {
        switch (actionsFlightsAdmin){
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
                service.findNewSeatsForCancelledFlights();
                break;
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("tpe1-g10 Client Starting ...");

            String serverAddress = ParseArgsHelper.getServerAdress(args[1]);
            ActionsFlightsAdmin actionsFlightsAdmin = ParseArgsHelper.getAction(args[2]);

            final FlightAdminServiceInterface service = (FlightAdminServiceInterface) Naming.lookup("//" + serverAddress+ "/flightAdminService");

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
