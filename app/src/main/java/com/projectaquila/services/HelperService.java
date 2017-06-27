package com.projectaquila.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HelperService {
    /**
     * Get a string representation of a date
     * @param date date object
     * @return string representation
     */
    public static String getDateKey(Date date){
        return getDateString("yyMMdd", date);
    }

    /**
     * Get a string representation of a date that is n days away from the given date
     * @param date original date
     * @param numDays number of days to add/substract
     * @return string representation of the modified date
     */
    public static String getDateKey(Date date, int numDays){
        return getDateString("yyMMdd", getModifiedDate(date, numDays));
    }

    /**
     * Get string representation of the given date
     * @param format format string
     * @param date date object
     * @return string representation of the date
     */
    public static String getDateString(String format, Date date){
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * Modify the given date by adding/substracting n number of days
     * @param date date object
     * @param numDays number of days to add/substact
     * @return modified date
     */
    public static Date getModifiedDate(Date date, int numDays){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, numDays);
        return c.getTime();
    }
}
