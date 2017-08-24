package com.planmaster.controls;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.planmaster.R;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.common.TaskDate;

public class DateEditText extends android.support.v7.widget.AppCompatTextView{
    private TaskDate mValue;
    private DatePickerClickListener mClickListener;

    public DateEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mValue = new TaskDate();
        setText(mValue.getFriendlyString());
        if(!isInEditMode()) {
            mClickListener = new DatePickerClickListener(mValue, new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    setValue((TaskDate) params.get("retval"));
                }
            });
        }
        setBackgroundResource(R.drawable.bottomborder);
        enable();
    }

    public TaskDate getValue(){
        return mValue;
    }

    public void setValue(TaskDate date){
        mValue = date;
        setText(mValue.getFriendlyString());
        mClickListener.setDefaultValue(mValue);
    }

    public void disable(){
        setOnClickListener(null);
        setTextColor(Color.GRAY);
    }

    public void enable(){
        setOnClickListener(mClickListener);
        setTextColor(Color.BLACK);
    }
}
