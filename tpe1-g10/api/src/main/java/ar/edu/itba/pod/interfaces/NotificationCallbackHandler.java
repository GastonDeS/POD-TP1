package ar.edu.itba.pod.interfaces;

import ar.edu.itba.pod.constants.SeatCategory;
import org.slf4j.Logger;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationCallbackHandler extends Remote {

    void sendNotification(String message, Logger logger) throws RemoteException;
    // TODO: Erase comments
//
//    void sendNotificationUpdate(String flightCode, String destCode, SeatCategory seatCategory, String place, SeatCategory oldSeatCategory
//            , String oldPlace, String message) throws RemoteException;
}
