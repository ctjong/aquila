package com.projectaquila.services;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.DatePicker;

import com.projectaquila.AppContext;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.models.TaskDate;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class HelperService {
    /**
     * Convert the given integer to string
     * @param i integer
     * @return string
     */
    public static String toString(int i){
        Locale currentLocale;
        Context ctx = AppContext.getCurrent().getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale = ctx.getResources().getConfiguration().getLocales().get(0);
        }else{
            currentLocale = ctx.getResources().getConfiguration().locale;
        }
        return String.format(currentLocale, "%d", i);
    }

    /**
     * Get a click handler that launches the "Go To Date" date picker
     */
    public static View.OnClickListener getDatePickerClickHandler(TaskDate initialDate, final Callback dateSetHandler){
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
                        retVal.put("retval", new TaskDate(c.getTime()));
                        dateSetHandler.execute(retVal, S.OK);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        };
    }
}
