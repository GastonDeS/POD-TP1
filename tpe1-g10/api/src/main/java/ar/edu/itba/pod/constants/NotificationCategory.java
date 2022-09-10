package api.src.main.java.ar.edu.itba.pod.constants;

public enum NotificationCategory {

    SUBSCRIBED("Successful subscription!"),
    FLIGHT_CONFIRMED("Flight confirmed!"),
    FLIGHT_CANCELLED("Flight cancelled!"),
    ASSIGNED_SEAT("Seat assigned!"),
    CHANGED_SEAT("Seat changed!"),
    CHANGED_TICKET("Ticket changed!");

    private String message;

    NotificationCategory(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
