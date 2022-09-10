package client.src.main.java.ar.edu.itba.pod.client;

import api.src.main.java.ar.edu.itba.pod.constants.SeatCategory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvGenerator {
    final static String planeModel = "Boeing 787";
    final static String planeModel2 = "Airbus A321";
    final static String firstDest = "JFK";
    final static String flightPreCode = "AA";
    final static String BUSINESS = "BUSINESS";
    final static String PREMIUM_ECONOMY = "PREMIUM_ECONOMY";
    final static String ECONOMIC = "ECONOMIC";
    final static String[] names = {"Flor", "Sol", "Gasti", "Brittu", "Juanma"};



    public static void main(String[] args) throws Exception {
        generateCsvForCreateFlight(20);
        generateCsvForCreatePlanes();
    }

    public static void generateCsvForCreatePlanes() throws IOException {
        File planesCsv = new File("tpe1-g10/client/src/main/resources/filesCsv/planes.csv");
        FileWriter fileWriter = new FileWriter(planesCsv);

        fileWriter.write("Model;Seats\n");
        fileWriter.write("Boeing 787;BUSINESS#2#3,PREMIUM_ECONOMY#3#3,ECONOMY#20#10\n");
        fileWriter.write("Airbus A321;ECONOMY#15#9,PREMIUM_ECONOMY#3#6\n");

        fileWriter.close();


    }

    public static void generateCsvForCreateFlight(int amount) throws IOException {
        File flightCsv = new File("tpe1-g10/client/src/main/resources/filesCsv/flights.csv");
        FileWriter fileWriter = new FileWriter(flightCsv);

        fileWriter.write("Model;FlightCode;DestinyAirport;Tickets\n");

        for (int i =0; i< amount; i++) {
            boolean randomSelector = Math.random() > 0.5;
            StringBuilder planeBuilder = new StringBuilder();
            if (randomSelector) {
                planeBuilder.append(planeModel);
            } else {
                planeBuilder.append(planeModel2);
            }
            planeBuilder.append(";").append(flightPreCode).append(100+i).append(";");
            planeBuilder.append(firstDest).append(";");
            int ticketAmount = Integer.parseInt(""+Math.round(Math.random() * 50));
            for (int j =0 ; j < ticketAmount ; j++) {
                double random = Math.random();
                if (random < 0.33) {
                    planeBuilder.append(BUSINESS);
                } else if (random < 0.66)
                    planeBuilder.append(PREMIUM_ECONOMY);
                else planeBuilder.append(ECONOMIC);

                planeBuilder.append("#").append(names[j % names.length]).append(j);
                if (j < ticketAmount - 1) planeBuilder.append(",");
            }
            planeBuilder.append("\n");
            fileWriter.write(planeBuilder.toString());
        }
        fileWriter.close();
    }

}
