package api.src.main.java.ar.edu.itba.pod.interfaces;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationCallbackHandler extends Remote {

    void sendNotification(String flightCode, String destCode, SeatCategory seatCategory, String place, String message) throws RemoteException;

    void sendNotificationUpdate(String flightCode, String destCode, SeatCategory seatCategory, String place, SeatCategory oldSeatCategory
            , String oldPlace, String message) throws RemoteException;
}
