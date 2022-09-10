package api.src.main.java.ar.edu.itba.pod.interfaces;

import api.src.main.java.ar.edu.itba.pod.constants.NotificationCategory;
import api.src.main.java.ar.edu.itba.pod.models.Ticket;

import java.rmi.RemoteException;

public interface NotificationServicePrivateInterface extends NotificationServiceInterface {

    void newNotification(String flightNumber, String name, NotificationCategory notificationCategory) throws RemoteException;

    void newNotification(String flightNumber, String name, Ticket oldTicket, NotificationCategory notificationCategory) throws RemoteException;
}
