package com.projectaquila.controls;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.projectaquila.R;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.models.TaskDate;
import com.projectaquila.services.HelperService;

import java.util.HashMap;

public class DateEditText extends android.support.v7.widget.AppCompatTextView{
    private TaskDate mValue;

    public DateEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setValue(new TaskDate());
        setBackgroundResource(R.drawable.bottomborder);
        setOnClickListener(getDateTextClickHandler());
    }

    public TaskDate getValue(){
        return mValue;
    }

    public void setValue(TaskDate date){
        mValue = date;
        setText(TaskDate.format("MM/dd/yyyy", mValue));
    }

    private OnClickListener getDateTextClickHandler(){
        return HelperService.getDatePickerClickHandler(mValue, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                setValue((TaskDate)params.get("retval"));
            }
        });
    }
}
