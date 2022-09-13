package ar.edu.itba.pod.utils;

import ar.edu.itba.pod.constants.ActionsSeatsAssignment;
import ar.edu.itba.pod.exceptions.InvalidArgumentsException;


import java.util.Properties;

public class SeatsAssignmentClientParser {
    private static final String ORIGINAL_FLIGHT_KEY = "originalFlight";
    private static final String COL_KEY = "col";
    private static final String ROW_KEY = "row";
    private static final String PASSENGER_KEY = "passenger";
    private static final String FLIGHT_KEY = "flight";
    private static final String ACTION_KEY = "action";
    private static final String SERVER_ADDRESS_KEY = "serverAddress";

    private String serverAddress;
    private ActionsSeatsAssignment action;
    private String flight;
    private String passenger;
    private Integer row;
    private String col;
    private String originalFlight;

    public String getServerAddress() {
        return serverAddress;
    }

    public ActionsSeatsAssignment getAction() {
        return action;
    }

    public String getFlight() {
        return flight;
    }

    public String getPassenger() {
        return passenger;
    }

    public Integer getRow() {
        return row;
    }

    public String getCol() {
        return col;
    }

    public String getOriginalFlight() {
        return originalFlight;
    }

    public void parseArguments() throws InvalidArgumentsException {
        Properties props = System.getProperties();

        if (!props.containsKey(SERVER_ADDRESS_KEY)) {
            throw new InvalidArgumentsException("Server address is not specified");
        } else {
            this.serverAddress = props.getProperty(SERVER_ADDRESS_KEY);
        }

        if (!props.containsKey(FLIGHT_KEY)) {
            throw new InvalidArgumentsException("Flight is not specified");
        } else {
            this.flight = props.getProperty(FLIGHT_KEY);
        }

        if (!props.containsKey(ACTION_KEY)) {
            throw new InvalidArgumentsException("Action is not specified");
        } else {
            this.action = ActionsSeatsAssignment.fromString(props.getProperty(ACTION_KEY));
            checkActionArguments(props);
        }
    }

    private void checkActionArguments(Properties props) throws InvalidArgumentsException {
        switch (this.action) {
            case STATUS:
                checkRowAndCol(props);
                break;
            case ASSIGN:
            case MOVE:
                checkRowAndCol(props);
                checkPassenger(props);
                break;
            case ALTERNATIVES:
                checkPassenger(props);
                break;
            case CHANGE:
                checkPassenger(props);
                checkOriginalFlight(props);
                break;
        }
    }

    private void checkRowAndCol(Properties props) throws InvalidArgumentsException {
        if (!props.containsKey(ROW_KEY)) {
            throw new InvalidArgumentsException("Row is not specified");
        }
        if (!props.containsKey(COL_KEY)) {
            throw new InvalidArgumentsException("Column is not specified");
        }
        this.row = Integer.valueOf(props.getProperty(ROW_KEY));
        this.col = props.getProperty(COL_KEY);
    }

    private void checkPassenger(Properties props) throws InvalidArgumentsException {
        if (!props.containsKey(PASSENGER_KEY)) {
            throw new InvalidArgumentsException("Passenger is not specified");
        }
        this.passenger = props.getProperty(PASSENGER_KEY);
    }

    private void checkOriginalFlight(Properties props) throws InvalidArgumentsException {
        if (!props.containsKey(ORIGINAL_FLIGHT_KEY)) {
            throw new InvalidArgumentsException("Original flight is not specified");
        }
        this.originalFlight = props.getProperty(ORIGINAL_FLIGHT_KEY);
    }
}
