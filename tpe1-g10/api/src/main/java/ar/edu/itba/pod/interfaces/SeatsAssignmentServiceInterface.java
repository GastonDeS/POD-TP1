package ar.edu.itba.pod.interfaces;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.Seat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface SeatsAssignmentServiceInterface extends Remote {
    // If seat is taken, returns the name of the passenger assigned to seat
    String checkEmptySeat(String flightCode, int row, String column) throws RemoteException;

    void assignSeat(String flightCode, String name, int row, String column) throws RemoteException;

    void changeSeat(String flightCode, String name, int row, String column) throws RemoteException;

    Map<SeatCategory, Map<String, Long>> getAvailableFlights(String flightCode, String name) throws RemoteException;

    void changeTicket(String name, String current, String alternative) throws RemoteException;
}
