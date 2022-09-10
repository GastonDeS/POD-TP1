package ar.edu.itba.pod.interfaces;


import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.models.Flight;
import ar.edu.itba.pod.models.Plane;
import ar.edu.itba.pod.models.RowData;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface FlightAdminServiceInterface extends Remote {

    Flight getFlight(String code) throws RemoteException;

    Flight createFlight(Plane plane, String code, String destination) throws RemoteException;

    Plane createPlane(String name, List<RowData> rowDataList) throws RemoteException;

    FlightStatus checkFlightStatus(String code) throws RemoteException;

    void confirmPendingFlight(String code) throws RemoteException;

    void cancelPendingFlight(String code) throws RemoteException;

    String findNewSeatsForCancelledFlights() throws RemoteException;

    Map<String, Plane> getPlanes() throws RemoteException;

    Map<String, Flight> getFlights() throws RemoteException;
}

