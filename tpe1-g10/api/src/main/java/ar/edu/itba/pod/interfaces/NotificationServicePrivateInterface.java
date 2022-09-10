package ar.edu.itba.pod.interfaces;

import ar.edu.itba.pod.constants.NotificationCategory;
import ar.edu.itba.pod.models.Ticket;
import ar.edu.itba.pod.interfaces.NotificationServiceInterface;

import java.rmi.RemoteException;

public interface NotificationServicePrivateInterface extends NotificationServiceInterface {

    void newNotification(String flightNumber, String name, NotificationCategory notificationCategory) throws RemoteException;

    void newNotification(String flightNumber, String name, Ticket oldTicket, NotificationCategory notificationCategory) throws RemoteException;
}
