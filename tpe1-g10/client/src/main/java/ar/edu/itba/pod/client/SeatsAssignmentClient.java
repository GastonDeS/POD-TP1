package client.src.main.java.ar.edu.itba.pod.client;

import api.src.main.java.ar.edu.itba.pod.interfaces.SeatsAssignmentServiceInterface;
import client.src.main.java.ar.edu.itba.pod.constants.ActionsSeatsAssignment;
import client.src.main.java.ar.edu.itba.pod.exceptions.InvalidArgumentsException;
import client.src.main.java.ar.edu.itba.pod.utils.SeatsAssignmentClientParser;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class SeatsAssignmentClient {

    private static void changeTicket(
            SeatsAssignmentServiceInterface service,
            SeatsAssignmentClientParser parser) throws RemoteException {
        service.changeTicket(parser.getPassenger(), parser.getOriginalFlight(), parser.getFlight());
    }

    private static void changeSeat(
            SeatsAssignmentServiceInterface service,
            SeatsAssignmentClientParser parser) throws RemoteException {
        service.changeSeat(parser.getFlight(), parser.getPassenger(), parser.getRow(), parser.getCol());
    }

    private static void assignSeat(
            SeatsAssignmentServiceInterface service,
            SeatsAssignmentClientParser parser) throws RemoteException {
        service.assignSeat(parser.getFlight(), parser.getPassenger(), parser.getRow(), parser.getCol());
    }

    private static void checkSeatStatus(
            SeatsAssignmentServiceInterface service,
            SeatsAssignmentClientParser parser) throws RemoteException {
        String passenger = service.checkEmptySeat(parser.getFlight(), parser.getRow(), parser.getCol());
        String status = passenger == null ? "FREE" : "ASSIGNED to " + passenger;
        System.out.println("Seat " + parser.getRow() + parser.getCol() + " is " + status);
    }

    private static void callMethodFromAction(
            SeatsAssignmentClientParser parser,
            SeatsAssignmentServiceInterface service) throws RemoteException {
        switch (parser.getAction()) {
            case STATUS:
                checkSeatStatus(service, parser);
            case ASSIGN:
                assignSeat(service, parser);
            case MOVE:
                changeSeat(service, parser);
            case ALTERNATIVES:
            case CHANGE:
                changeTicket(service, parser);
        }
    }

    public static void main(String[] args) {
        try {
            SeatsAssignmentClientParser parser = new SeatsAssignmentClientParser();

            try {
                parser.parseArguments();
            } catch (InvalidArgumentsException e) {
                System.out.println(e.getMessage());
                return;
            }

            final SeatsAssignmentServiceInterface service = (SeatsAssignmentServiceInterface)
                    Naming.lookup("//" + parser.getServerAddress() + "/seatsAssignmentService");

            callMethodFromAction(parser, service);

        } catch (Exception ex) {
            System.out.println("An exception happened");
            ex.printStackTrace();
        }
    }
}
