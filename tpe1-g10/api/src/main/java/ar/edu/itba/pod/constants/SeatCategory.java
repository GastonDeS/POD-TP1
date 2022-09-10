package ar.edu.itba.pod.constants;

import java.io.Serializable;

public enum SeatCategory implements Serializable {
    BUSINESS("business"), PREMIUM_ECONOMY("premium economy"), ECONOMY("economy");
    private String message;
    SeatCategory(String message){this.message = message;}

    public String getMessage() {
        return message;
    }
}
