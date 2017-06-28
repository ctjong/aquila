package com.projectaquila.services;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;

import com.projectaquila.AppContext;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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

    /**
     * Get a click handler that launches the "Go To Date" date picker
     */
    public static View.OnClickListener getDatePickerClickHandler(Date initialDate, final Callback dateSetHandler){
        final Calendar c = Calendar.getInstance();
        c.setTime(initialDate);
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AppContext.getCurrent().getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, month);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        HashMap<String, Object> retVal = new HashMap<>();
                        retVal.put("retval", c.getTime());
                        dateSetHandler.execute(retVal, S.OK);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        };
    }
}
