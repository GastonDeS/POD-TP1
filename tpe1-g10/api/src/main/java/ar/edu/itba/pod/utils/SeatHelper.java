package api.src.main.java.ar.edu.itba.pod.utils;

public class SeatHelper {

    public static String getRow(String place) {
        return place.split("[A-Z]")[0];
    }

    public static String getColumn(String place) {
        String row = getRow(place);
        return ""+place.charAt(row.length());
    }
}
