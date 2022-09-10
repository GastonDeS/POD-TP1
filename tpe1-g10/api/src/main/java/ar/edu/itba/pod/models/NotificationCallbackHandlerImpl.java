package api.src.main.java.ar.edu.itba.pod.models;

import api.src.main.java.ar.edu.itba.pod.interfaces.NotificationCallbackHandler;
import org.slf4j.Logger;

import java.rmi.RemoteException;

public class NotificationCallbackHandlerImpl implements NotificationCallbackHandler {

    @Override
    public void sendNotification(String message, Logger logger) throws RemoteException {
        logger.info(message);
    }
}
