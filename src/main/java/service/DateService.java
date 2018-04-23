package service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateService {

    public static String getCurrentDate() {

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);

    }
}
