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
    private static Logger logger = LoggerFactory.getLogger(FlightCrewClient.class);

    private static String serverAddressInput;
    private static String flightCodeInput;
    private static String outPathInput = "./";
    private static Optional<String> optionalCategoryInput;
    private static Optional<String> optionalRowInput;
    private static SeatCategory categoryInput;
    private static String rowInput;
    private static Map<String, Map<String, SeatDto>> planeMap;
    private static Map<String, SeatDto> rowPlaneMap;

    public static void main(String[] args) throws RemoteException,
            NotBoundException, MalformedURLException {
        logger.info("tpe1-g10 Flight Crew Client Starting ...");
        try {
            getSystemProperties();
            getResults();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        logger.info("Flight Crew Client has succesfully done his job");
    }

    private static void getSystemProperties() throws RemoteException {
        serverAddressInput = System.getProperty("serverAddress");
        flightCodeInput = System.getProperty("flightCode");
        optionalCategoryInput = Optional.ofNullable(System.getProperty("category"));
        optionalRowInput = Optional.ofNullable(System.getProperty("row"));


        if(optionalCategoryInput.isPresent() && optionalRowInput.isPresent()){
            throw new RemoteException("You cannot consult for category and row at the same time");
        } else if (optionalRowInput.isPresent()) {
            rowInput = optionalRowInput.get();
        } else optionalCategoryInput.ifPresent(s -> categoryInput = SeatCategory.valueOf(s));

//        StringBuilder str = new StringBuilder();
//        str.append(outPathInput);
//        str.append(System.getProperty("outPath"));
        outPathInput = System.getProperty("outPath");
    }

    private static void getResults() throws IOException {
        try {
            String ip = "//" + serverAddressInput + "/" + "seatMapService";

            final SeatMapServiceInterface handle = (SeatMapServiceInterface)
                    Naming.lookup(ip);
            if (rowInput == null && categoryInput == null) {
                planeMap = handle.peekAllSeats(flightCodeInput);
                writeOutputPlaneResults(planeMap);
            } else if (categoryInput != null) {
                planeMap = handle.peekCategorySeats(flightCodeInput, categoryInput);
                writeOutputPlaneResults(planeMap);
            } else {
                rowPlaneMap = handle.peekRowSeats(flightCodeInput, rowInput);
                writeOutputRowResults(rowPlaneMap, rowInput);
            }
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
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
