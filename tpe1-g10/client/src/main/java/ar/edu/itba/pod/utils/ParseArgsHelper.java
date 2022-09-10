package ar.edu.itba.pod.utils;

import ar.edu.itba.pod.constants.ActionsFlightsAdmin;

public class ParseArgsHelper {
    public static String getServerAdress(String arg){
        return arg.split("=")[1];
    }
    public static ActionsFlightsAdmin getAction(String arg){
        return ActionsFlightsAdmin.valueOf(arg.split("=")[1]);
    }
}
