package com.projectaquila.services;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;

import com.projectaquila.AppContext;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.models.TaskDate;

import java.util.Calendar;
import java.util.HashMap;

public class HelperService {
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
