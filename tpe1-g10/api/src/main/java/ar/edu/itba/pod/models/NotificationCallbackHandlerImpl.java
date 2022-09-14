package ar.edu.itba.pod.models;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.interfaces.NotificationCallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;

public class NotificationCallbackHandlerImpl implements NotificationCallbackHandler, Serializable {

    private boolean finished = false;
    private final Logger logger = LoggerFactory.getLogger(NotificationCallbackHandlerImpl.class);

    @Override
    public void finish() throws RemoteException {
        synchronized (this) {
            finished = true;
            this.notify();
        }
    }

    private void notifyClient() {
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public void subscribedNotification(String flightCode, String dest) throws RemoteException {
        logger.info("You are following flight " + flightCode + " with destination " + dest);
        this.notifyClient();
    }

    @Override
    public void flightConfirmedNotification(String flightCode, String dest, String seatCategory, String place) throws RemoteException {
        if (seatCategory != null && place != null) {
            logger.info("Your flight " + flightCode + " with destination " + dest + " was confirmed and your seat is " + seatCategory + " " + place);
        } else {
            logger.info("Your flight " + flightCode + " with destination " + dest + " was confirmed");

        }
        this.notifyClient();
    }

    @Override
    public void flightCancelledNotification(String flightCode, String dest, String seatCategory, String place) throws RemoteException {
        if (seatCategory != null && place != null) {
            logger.info("Your flight " + flightCode + " with destination " + dest + " was cancelled and your seat is " + seatCategory + " " + place);
        } else {
            logger.info("Your flight " + flightCode + " with destination " + dest + " was cancelled");
        }
        this.notifyClient();
    }

    @Override
    public void assignedSeatNotification(String flightCode, String dest, String seatCategory, String place) throws RemoteException {
        logger.info("Your seat is " + seatCategory + " " + place + " for flight " + flightCode + " with destination " + dest);
        this.notifyClient();
    }

    @Override
    public void changedSeatNotification(String flightCode, String dest, String seatCategory, String place,
                                        String oldSeatCategory, String oldPlace) throws RemoteException {
        logger.info("Your seat changed to " + seatCategory + " " + place + " from " + oldSeatCategory + " " + oldPlace + " for flight " + flightCode + " with destination " + dest);
        this.notifyClient();
    }

    @Override
    public void changedTicketNotification(String flightCode, String dest, String oldFlightCode, String oldDest) throws RemoteException {
        logger.info("Your ticket changed to flight " + flightCode + " with destination " + dest + " from flight " + oldFlightCode + " with destination " + oldDest);
        this.notifyClient();
    }

    public boolean sleep() throws InterruptedException {
        synchronized (this) {
            this.wait();
            return finished;
        }
    }
}
