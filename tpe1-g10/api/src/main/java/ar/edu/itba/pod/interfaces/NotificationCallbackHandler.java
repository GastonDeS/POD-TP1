package ar.edu.itba.pod.interfaces;

import ar.edu.itba.pod.constants.SeatCategory;
import org.slf4j.Logger;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationCallbackHandler extends Remote {

    void finish() throws RemoteException;

    void subscribedNotification(String flightCode, String dest) throws RemoteException;

    void flightConfirmedNotification(String flightCode, String dest, String seatCategory, String place) throws RemoteException;

    void flightCancelledNotification(String flightCode, String dest, String seatCategory, String place) throws RemoteException;

    void assignedSeatNotification(String flightCode, String dest, String seatCategory, String place) throws RemoteException;

    void changedSeatNotification(String flightCode, String dest, String seatCategory, String place,
                                 String oldSeatCategory, String oldPlace) throws RemoteException;

    void changedTicketNotification(String flightCode, String dest, String oldFlightCode, String oldDest) throws RemoteException;
}
