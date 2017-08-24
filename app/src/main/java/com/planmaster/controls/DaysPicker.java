package com.planmaster.controls;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.planmaster.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DaysPicker extends LinearLayout {
    private HashMap<Integer, Integer> mControlToCalendarDaysMap;
    private HashMap<Integer, Boolean> mControlValues;

    /**
     * Create a new days picker
     * @param context The Context the view is running in
     */
    public DaysPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_dayspicker, this);

        mControlToCalendarDaysMap = new HashMap<>();
        mControlToCalendarDaysMap.put(R.id.dayspicker_sun, Calendar.SUNDAY);
        mControlToCalendarDaysMap.put(R.id.dayspicker_mon, Calendar.MONDAY);
        mControlToCalendarDaysMap.put(R.id.dayspicker_tue, Calendar.TUESDAY);
        mControlToCalendarDaysMap.put(R.id.dayspicker_wed, Calendar.WEDNESDAY);
        mControlToCalendarDaysMap.put(R.id.dayspicker_thu, Calendar.THURSDAY);
        mControlToCalendarDaysMap.put(R.id.dayspicker_fri, Calendar.FRIDAY);
        mControlToCalendarDaysMap.put(R.id.dayspicker_sat, Calendar.SATURDAY);

        mControlValues = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry : mControlToCalendarDaysMap.entrySet()){
            mControlValues.put(entry.getKey(), false);
            findViewById(entry.getKey()).setOnClickListener(getDayTextClickHandler());
        }
    }

    /**
     * Get click handler for the day TextViews
     * @return click handler
     */
    public OnClickListener getDayTextClickHandler(){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                int controlId = v.getId();
                mControlValues.put(controlId, !mControlValues.get(controlId));
                updateView();
            }
        };
    }

    /**
     * Return the value of this control
     * @return value set
     */
    public HashSet<Integer> getValue(){
        HashSet<Integer> set = new HashSet<>();
        for(Map.Entry<Integer, Boolean> entry : mControlValues.entrySet()){
            if(entry.getValue()){
                set.add(mControlToCalendarDaysMap.get(entry.getKey()));
            }
        }
        return set;
    }

    /**
     * Set the value of this control
     * @param newValue value set
     */
    public void setValue(List<Integer> newValue){
        HashSet<Integer> newValueSet = new HashSet<>(newValue);
        for(Map.Entry<Integer, Integer> entry : mControlToCalendarDaysMap.entrySet()){
            mControlValues.put(entry.getKey(), newValueSet.contains(entry.getValue()));
        }
        updateView();
    }

    /**
     * Update view based on the control values set
     */
    private void updateView(){
        for(Map.Entry<Integer, Boolean> entry : mControlValues.entrySet()) {
            TextView tv = (TextView)findViewById(entry.getKey());
            if(entry.getValue()){
                tv.setBackgroundResource(R.drawable.dayspicker_activebg);
            }else{
                tv.setBackgroundResource(0);
            }
        }
    }

}
