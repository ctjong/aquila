package com.projectaquila.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskDate extends java.util.Date {
    /**
     * Instantiate a new Date with current time
     */
    public TaskDate(){
        super();
    }

    /**
     * Instantiate a new Date with the given time
     * @param date time in long format
     */
    public TaskDate(long date){
        super(date);
    }

    /**
     * Instantiate a new Date with the given time
     * @param date java.util.Date object
     */
    public TaskDate(java.util.Date date){
        super(date.getTime());
    }

    /**
     * Get a key string representation of this
     * @return string representation
     */
    public String toDateKey(){
        return format("yyMMdd", this);
    }

    /**
     * Get a string representation of this that is n days away from the given date
     * @param numDays number of days to add/substract
     * @return string representation of the modified date
     */
    public String getModifiedKey(int numDays){
        return format("yyMMdd", getModified(numDays));
    }

    /**
     * Modify the given date by adding/substracting n number of days
     * @param numDays number of days to add/substact
     * @return modified date
     */
    public TaskDate getModified(int numDays){
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        c.add(Calendar.DATE, numDays);
        return new TaskDate(c.getTime());
    }

    /**
     * Get string representation of the given date
     * @param format format string
     * @param date date object
     * @return string representation of the date
     */
    public static String format(String format, TaskDate date){
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * Parse the given date key string (yyMMdd) into a date objecy
     * @param dateKey yyMMdd date key string
     * @return date object, or null on failure
     */
    public static TaskDate parseDateKey(String dateKey){
        if(dateKey == null) return null;
        try {
            return new TaskDate(new SimpleDateFormat("yyMMdd").parse(dateKey));
        } catch (ParseException e) {
            System.err.println("[TaskDate.parseDateKey] exception");
            e.printStackTrace();
            return null;
        }
    }
}
