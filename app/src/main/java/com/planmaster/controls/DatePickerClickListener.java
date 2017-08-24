package com.planmaster.controls;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;

import com.planmaster.contexts.AppContext;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.common.TaskDate;

import java.util.Calendar;

public class DatePickerClickListener implements View.OnClickListener{
    private Callback mCallback;
    private TaskDate mDefaultValue;
    private DatePickerDialog mDialog;

    public DatePickerClickListener(@NonNull TaskDate defaultValue, @NonNull final Callback cb){
        mDefaultValue = defaultValue;
        mCallback = cb;
        initDialog();
    }

    public void setDefaultValue(@NonNull TaskDate defaultValue){
        mDefaultValue = defaultValue;
        initDialog();
    }

    @Override
    public void onClick(View v) {
        mDialog.show();
    }

    private void initDialog(){
        final Calendar c = Calendar.getInstance();
        c.setTime(mDefaultValue);
        mDialog = new DatePickerDialog(AppContext.getCurrent().getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mCallback.execute(new CallbackParams("retval", new TaskDate(c.getTime())));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }
}
