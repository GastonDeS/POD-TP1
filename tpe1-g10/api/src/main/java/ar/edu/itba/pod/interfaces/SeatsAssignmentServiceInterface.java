package ar.edu.itba.pod.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import ar.edu.itba.pod.models.AvailableFlightDto;

public interface SeatsAssignmentServiceInterface extends Remote {
    // If seat is taken, returns the name of the passenger assigned to seat
    String checkEmptySeat(String flightCode, int row, String column) throws RemoteException;

    void assignSeat(String flightCode, String name, int row, String column) throws RemoteException;

    void changeSeat(String flightCode, String name, int row, String column) throws RemoteException;

    AvailableFlightDto getAvailableFlights(String flightCode, String name) throws RemoteException;

    void changeTicket(String name, String current, String alternative) throws RemoteException;
}
