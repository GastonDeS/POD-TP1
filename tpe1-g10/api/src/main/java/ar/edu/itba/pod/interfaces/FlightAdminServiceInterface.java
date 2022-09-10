package ar.edu.itba.pod.interfaces;


import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.models.Plane;
import ar.edu.itba.pod.models.RowData;
import ar.edu.itba.pod.models.Ticket;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FlightAdminServiceInterface extends Remote {

    void createFlight(String planeName, String code, String destination, List<Ticket> tickets) throws RemoteException;

    void createPlane(String name, List<RowData> rowDataList) throws RemoteException;

    FlightStatus checkFlightStatus(String code) throws RemoteException;

    void confirmPendingFlight(String code) throws RemoteException;

    void cancelPendingFlight(String code) throws RemoteException;

    String findNewSeatsForCancelledFlights() throws RemoteException;

}

