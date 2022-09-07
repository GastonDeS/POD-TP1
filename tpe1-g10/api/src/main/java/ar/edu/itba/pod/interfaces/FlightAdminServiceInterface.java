package api.src.main.java.ar.edu.itba.pod.interfaces;


import api.src.main.java.ar.edu.itba.pod.constants.FlightStatus;
import api.src.main.java.ar.edu.itba.pod.models.Flight;
import api.src.main.java.ar.edu.itba.pod.models.Plane;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface FlightAdminServiceInterface extends Remote {

    public Flight getFlight(String code) throws RemoteException;

    public void addPlaneModel(Plane plane) throws RemoteException;

    public void addFlight(Flight flight) throws RemoteException;

    public FlightStatus checkFlightStatus(String code) throws RemoteException;

    public void confirmPendingFlight(String code) throws RemoteException;

    public void cancelPendingFlight(String code) throws RemoteException;

    public void findNewSeatsForCancelledFlights() throws RemoteException;

    public Map<String, Plane> getPlanes() throws RemoteException;

    public Map<String, Flight> getFlights() throws RemoteException;
}

