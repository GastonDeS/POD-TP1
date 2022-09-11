package ar.edu.itba.pod.constants;

import java.io.Serializable;

public enum SeatCategory implements Serializable {
    BUSINESS("BUSINESS"), PREMIUM_ECONOMY("PREMIUM_ECONOMY"), ECONOMY("ECONOMY");
    private String message;
    SeatCategory(String message){this.message = message;}

    public String getMessage() {
        return message;
    }
}
