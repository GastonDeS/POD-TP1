package ar.edu.itba.pod.interfaces;

import ar.edu.itba.pod.constants.SeatCategory;
import org.slf4j.Logger;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationCallbackHandler extends Remote {

    void subscribedNotification(Logger logger, String flightCode, String dest) throws RemoteException;

    void flightConfirmedNotification(Logger logger, String flightCode, String dest, String seatCategory, String place) throws RemoteException;

    void flightCancelledNotification(Logger logger, String flightCode, String dest, String seatCategory, String place) throws RemoteException;

    void assignedSeatNotification(Logger logger, String flightCode, String dest, String seatCategory, String place) throws RemoteException;

    void changedSeatNotification(Logger logger, String flightCode, String dest, String seatCategory, String place,
                                 String oldSeatCategory, String oldPlace) throws RemoteException;

    void changedTicketNotification(Logger logger, String flightCode, String dest, String seatCategory, String place,
                                   String oldFlightCode, String oldDest) throws RemoteException;
}
