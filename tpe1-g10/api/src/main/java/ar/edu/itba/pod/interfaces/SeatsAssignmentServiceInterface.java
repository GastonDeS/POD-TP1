package api.src.main.java.ar.edu.itba.pod.interfaces;

import api.src.main.java.ar.edu.itba.pod.models.Seat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface SeatsAssignmentServiceInterface extends Remote {

    boolean checkEmptySeat(String flightCode, int row, String column) throws RemoteException;

    void assignSeat(String flightCode, String name, int row, String column) throws RemoteException;

    void changeSeat(String flightCode, String name, int row, String column) throws RemoteException;

    Map<String, List<Seat>> getAvailableFlights(String flightCode, String name) throws RemoteException;

    void changeTicket(String name, String current, String alternative) throws RemoteException;
}
