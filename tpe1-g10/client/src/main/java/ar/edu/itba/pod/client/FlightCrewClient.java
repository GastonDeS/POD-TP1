package ar.edu.itba.pod.client;

import ar.edu.itba.pod.constants.SeatCategory;
import ar.edu.itba.pod.interfaces.SeatMapServiceInterface;
import ar.edu.itba.pod.models.SeatDto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlightCrewClient {
    private static final Logger logger = LoggerFactory.getLogger(FlightCrewClient.class);

    private static String serverAddressInput;
    private static String flightCodeInput;
    private static String outPathInput = "./";
    private static SeatCategory categoryInput;
    private static String rowInput;

    public static void main(String[] args) throws RemoteException,
            NotBoundException, MalformedURLException {
        try {
            logger.info("tpe1-g10 Flight Crew Client Starting ...");
            getSystemProperties();
            getResults();
        } catch (Exception e) {
            logger.error(e.getCause().getMessage());
        }
    }

    private static void getSystemProperties() {
        serverAddressInput = System.getProperty("serverAddress");
        flightCodeInput = System.getProperty("flightCode");
        String optionalCategoryInput = System.getProperty("category");
        String optionalRowInput = System.getProperty("row");


        if(optionalCategoryInput != null && optionalRowInput!= null){
            logger.error("You cannot consult for category and row at the same time");
            return;
        } else if (optionalRowInput!= null) {
            rowInput = optionalRowInput;
        } else {
            if (optionalCategoryInput != null)
                categoryInput = SeatCategory.valueOf(optionalCategoryInput);
        }
        outPathInput = System.getProperty("outPath");
    }

    private static void getResults() {
        try {
            String ip = "//" + serverAddressInput + "/" + "seatMapService";

            final SeatMapServiceInterface handle = (SeatMapServiceInterface)
                    Naming.lookup(ip);
            Map<String, Map<String, SeatDto>> planeMap;

            if (rowInput == null && categoryInput == null) {
                if( flightCodeInput == null) {
                    logger.error("There must be a valid flight code");
                    return;
                }
                planeMap = handle.peekAllSeats(flightCodeInput);
                writeOutputPlaneResults(planeMap);
            } else if (categoryInput != null) {
                if (flightCodeInput == null){
                    logger.error("There must be a valid flight code");
                    return;
                }
                planeMap = handle.peekCategorySeats(flightCodeInput, categoryInput);
                writeOutputPlaneResults(planeMap);
            } else {
                if (flightCodeInput == null) {
                    logger.error("There must be a valid flight code");
                    return;
                }
                Map<String, SeatDto> rowPlaneMap = handle.peekRowSeats(flightCodeInput, rowInput);
                writeOutputRowResults(rowPlaneMap, rowInput);
            }
        } catch (Exception e) {
            logger.error(e.getCause().getMessage());
        }
    }

    private static void writeOutputRowResults(Map<String, SeatDto> rowPlaneMap, String row) throws IOException {
        File fileCsv = new File(outPathInput);
        FileWriter fw = new FileWriter(fileCsv);
        SeatCategory category = rowPlaneMap.entrySet().stream().findFirst().get().getValue().getSeatCategory();
        for(String column : rowPlaneMap.keySet()){
            char initial = rowPlaneMap.get(column).getInfo();
            String initalLetter = Character.toString(initial).toUpperCase();
            fw.write(row + " " + column + " " + initalLetter + ";");
        }
        fw.write(category.getMessage() + "\n");
        fw.close();
    }

    private static void writeOutputPlaneResults(Map<String, Map<String, SeatDto>> planeMap) throws IOException{
        File fileCsv = new File(outPathInput);
        FileWriter fw = new FileWriter(fileCsv);
        for(String row : planeMap.keySet().stream().sorted().collect(Collectors.toList())){
            Map<String, SeatDto> rowMap = planeMap.get(row);
            SeatCategory category = rowMap.entrySet().stream().findFirst().get().getValue().getSeatCategory();
            for(String column : rowMap.keySet()){
                char initial = rowMap.get(column).getInfo();
                String initalLetter = Character.toString(initial).toUpperCase();
                fw.write(row + " " + column + " " + initalLetter + ";");
            }
            fw.write(category.getMessage() + "\n");
        }
        fw.close();
    }

}
