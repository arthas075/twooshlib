package lib.twoosh.twooshlib.utils;

/**
 * Created by satyamsurendra on 17/11/16.
 */
public class Utils {

    public Utils(){

    }


    public static String getTimeZoneString(String twoosh_time){

        String date = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new java.util.Date (Long.parseLong(twoosh_time)*1000));
        return date;
    }
}
