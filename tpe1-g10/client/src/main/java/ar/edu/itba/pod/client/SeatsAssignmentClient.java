package client.src.main.java.ar.edu.itba.pod.client;

import api.src.main.java.ar.edu.itba.pod.interfaces.SeatsAssignmentServiceInterface;
import client.src.main.java.ar.edu.itba.pod.exceptions.InvalidArgumentsException;
import client.src.main.java.ar.edu.itba.pod.utils.SeatsAssignmentClientParser;

import java.rmi.Naming;

public class SeatsAssignmentClient {

    public static void main(String[] args) {
        try {
            SeatsAssignmentClientParser parser = new SeatsAssignmentClientParser();

            try {
                parser.parseArguments();
            } catch (InvalidArgumentsException e) {
                System.out.println(e.getMessage());
                return;
            }

            final SeatsAssignmentServiceInterface service = (SeatsAssignmentServiceInterface) Naming.lookup("//" + parser.getServerAddress() + "/seatsAssignmentService");

        } catch (Exception ex) {
            System.out.println("An exception happened");
            ex.printStackTrace();
        }
    }
}
