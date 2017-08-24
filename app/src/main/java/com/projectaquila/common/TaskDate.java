package com.projectaquila.common;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.services.HelperService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskDate extends java.util.Date {
    public static final TaskDate MAX = TaskDate.parseDateKey("99991231");
    private static final String DateKeyFormat = "yyyyMMdd";
    private static final String FriendlyFormat = "MM/dd/yyyy";

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
        return format(DateKeyFormat, this);
    }

    /**
     * Get a friendly string of this date
     * @return date string
     */
    public String getFriendlyString(){
        if(equals(MAX)){
            return AppContext.getCurrent().getActivity().getString(R.string.taskrecurrence_end_forever);
        }else {
            return format(FriendlyFormat, this);
        }
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
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    /**
     * Parse the given date key string into a date object
     * @param dateKey date key string
     * @return date object, or null on failure
     */
    public static TaskDate parseDateKey(String dateKey){
        if(dateKey == null) return null;
        try {
            return new TaskDate(new SimpleDateFormat(DateKeyFormat, Locale.getDefault()).parse(dateKey));
        } catch (ParseException e) {
            HelperService.logError("[TaskDate.parseDateKey] exception " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object other){
        return other instanceof TaskDate && toDateKey().equals(((TaskDate)other).toDateKey());
    }

    @Override
    public String toString(){
        return toDateKey();
    }
}
