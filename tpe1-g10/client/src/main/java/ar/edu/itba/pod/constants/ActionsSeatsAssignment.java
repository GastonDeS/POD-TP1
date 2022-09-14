package ar.edu.itba.pod.constants;

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

    public static ActionsSeatsAssignment fromString(String action) {
        for (ActionsSeatsAssignment a: values()) {
            if (a.getAction().equals(action)) {
                return a;
            }
        }
        return null;
    }

}
