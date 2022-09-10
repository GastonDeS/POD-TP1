package ar.edu.itba.pod.interfaces;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.models.SeatDto;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface SeatMapServiceInterface extends Remote {

    Map<String, Map<String, SeatDto>> peekAllSeats(String flightCode) throws RemoteException;

    Map<String, SeatDto> peekRowSeats(String flightCode, String rowNumber) throws RemoteException;

    Map<String, Map<String, SeatDto>> peekCategorySeats(String flightCode, SeatCategory category) throws RemoteException;
}
