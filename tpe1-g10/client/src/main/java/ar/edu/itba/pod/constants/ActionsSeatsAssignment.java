package ar.edu.itba.pod.constants;

import ar.edu.itba.pod.exceptions.InvalidArgumentsException;

public enum ActionsSeatsAssignment {
    STATUS("status"),
    ASSIGN("assign"),
    MOVE("move"),
    ALTERNATIVES("alternatives"),
    CHANGE("changeTicket");

    private final String action;

    ActionsSeatsAssignment(String name) {
        this.action = name;
    }

    public String getAction() {
        return action;
    }

    public static ActionsSeatsAssignment fromString(String action) throws InvalidArgumentsException {
        for (ActionsSeatsAssignment a: values()) {
            if (a.getAction().equals(action)) {
                return a;
            }
        }
        throw new InvalidArgumentsException("The action provided does not exist");
    }

}
