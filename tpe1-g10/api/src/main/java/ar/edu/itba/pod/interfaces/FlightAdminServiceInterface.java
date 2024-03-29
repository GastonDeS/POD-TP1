package ar.edu.itba.pod.interfaces;


import ar.edu.itba.pod.models.AdminClientResponse;
import ar.edu.itba.pod.constants.FlightStatus;
import ar.edu.itba.pod.models.PlaneData;
import ar.edu.itba.pod.models.TicketDto;
import ar.edu.itba.pod.constants.SeatCategory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import ar.edu.itba.pod.models.AdminClientResponse;

public interface FlightAdminServiceInterface extends Remote {

    void createFlight(String planeName, String code, String destination, List<TicketDto> tickets) throws RemoteException;

    void createPlane(String name, Map<SeatCategory, PlaneData> planeDataMap) throws RemoteException;

    FlightStatus checkFlightStatus(String code) throws RemoteException;

    void confirmPendingFlight(String code) throws RemoteException;

    void cancelPendingFlight(String code) throws RemoteException;

    AdminClientResponse<TicketDto> findNewSeatsForCancelledFlights() throws RemoteException;

}

