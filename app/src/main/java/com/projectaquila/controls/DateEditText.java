package com.projectaquila.controls;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.projectaquila.R;
import com.projectaquila.models.Callback;
import com.projectaquila.models.CallbackParams;
import com.projectaquila.models.TaskDate;

public class DateEditText extends android.support.v7.widget.AppCompatTextView{
    private TaskDate mValue;
    private DatePickerClickListener mClickListener;

    public DateEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mValue = new TaskDate();
        setText(mValue.getFriendlyString());
        mClickListener = new DatePickerClickListener(mValue, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                setValue((TaskDate)params.get("retval"));
            }
        });
        setOnClickListener(mClickListener);
        setBackgroundResource(R.drawable.bottomborder);
    }

    public TaskDate getValue(){
        return mValue;
    }

    public void setValue(TaskDate date){
        mValue = date;
        setText(mValue.getFriendlyString());
        mClickListener.setDefaultValue(mValue);
    }
}
