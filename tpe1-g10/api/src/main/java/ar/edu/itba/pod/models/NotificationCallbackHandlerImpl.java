package api.src.main.java.ar.edu.itba.pod.models;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;
import api.src.main.java.ar.edu.itba.pod.interfaces.NotificationCallbackHandler;

import java.rmi.RemoteException;

public class NotificationCallbackHandlerImpl implements NotificationCallbackHandler {

    // TODO: implement correctly
    @Override
    public void sendNotification(String flightCode, String destCode, SeatCategory seatCategory, String place, String message) throws RemoteException {

    }

    @Override
    public void sendNotificationUpdate(String flightCode, String destCode, SeatCategory seatCategory, String place,
                                       SeatCategory oldSeatCategory, String oldPlace, String message) throws RemoteException {

    }
}
