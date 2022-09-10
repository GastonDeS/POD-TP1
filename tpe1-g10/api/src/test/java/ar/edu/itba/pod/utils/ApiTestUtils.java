package ar.edu.itba.pod.utils;

import ar.edu.itba.pod.models.RowData;

import java.util.ArrayList;
import java.util.List;

public class ApiTestUtils {
    public static List<RowData> getRowDataForFlight() {
        List<ar.edu.itba.pod.models.RowData> rowData = new ArrayList<>();
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.BUSINESS, 3));
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.BUSINESS, 3));
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.BUSINESS, 3));
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.PREMIUM_ECONOMY, 5));
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.PREMIUM_ECONOMY, 5));
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.PREMIUM_ECONOMY, 5));
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.PREMIUM_ECONOMY, 5));
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.ECONOMY, 7));
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.ECONOMY, 7));
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.ECONOMY, 7));
        rowData.add(new ar.edu.itba.pod.models.RowData(ar.edu.itba.pod.constants.SeatCategory.ECONOMY, 7));
        return rowData;
    }
}
