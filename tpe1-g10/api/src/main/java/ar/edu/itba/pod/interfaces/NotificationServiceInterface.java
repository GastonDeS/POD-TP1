package api.src.main.java.ar.edu.itba.pod.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotificationServiceInterface extends Remote {

    void subscribe(String flightNumber, String name, NotificationCallbackHandler handler) throws RemoteException;
}
