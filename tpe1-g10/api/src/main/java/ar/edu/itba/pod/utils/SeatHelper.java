package ar.edu.itba.pod.utils;

public class SeatHelper {

    public static String getRow(String place) {
        return place.split("[A-Z]")[0];
    }

    public static String getRowFromInt(int row) {
        return (row + 1) < 10 ? ("0" + row) : "" + (row );
    }

    public static String getColumn(String place) {
        String row = getRow(place);
        return ""+place.charAt(row.length());
    }
}
