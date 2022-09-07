package api.src.main.java.ar.edu.itba.pod.interfaces;


import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Plane;
import api.src.main.java.ar.edu.itba.pod.models.RowData;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface FlightAdminServiceInterface extends Remote {

    public Flight getFlight(String code) throws RemoteException;

    public Flight createFlight(Plane plane, String code, String origin, String destination) throws RemoteException;

    public Plane createPlane(String name, List<RowData> rowDataList) throws RemoteException;

    public FlightStatus checkFlightStatus(String code) throws RemoteException;

    public void confirmPendingFlight(String code) throws RemoteException;

    public void cancelPendingFlight(String code) throws RemoteException;

    public String findNewSeatsForCancelledFlights() throws RemoteException;

    public Map<String, Plane> getPlanes() throws RemoteException;

    public Map<String, Flight> getFlights() throws RemoteException;
}

