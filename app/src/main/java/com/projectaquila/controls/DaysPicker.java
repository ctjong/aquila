package com.projectaquila.controls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.R;

public class DaysPicker extends LinearLayout {
    private TextView mTextSun;
    private TextView mTextMon;
    private TextView mTextTue;
    private TextView mTextWed;
    private TextView mTextThu;
    private TextView mTextFri;
    private TextView mTextSat;

    /**
     * Create a new days picker
     * @param context The Context the view is running in
     */
    public DaysPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_dayspicker, this);

        mTextSun = (TextView)findViewById(R.id.dayspicker_sun);
        mTextMon = (TextView)findViewById(R.id.dayspicker_mon);
        mTextTue = (TextView)findViewById(R.id.dayspicker_tue);
        mTextWed = (TextView)findViewById(R.id.dayspicker_wed);
        mTextThu = (TextView)findViewById(R.id.dayspicker_thu);
        mTextFri = (TextView)findViewById(R.id.dayspicker_fri);
        mTextSat = (TextView)findViewById(R.id.dayspicker_sat);

        mTextSun.setOnClickListener(getDayTextClickHandler());
        mTextMon.setOnClickListener(getDayTextClickHandler());
        mTextTue.setOnClickListener(getDayTextClickHandler());
        mTextWed.setOnClickListener(getDayTextClickHandler());
        mTextThu.setOnClickListener(getDayTextClickHandler());
        mTextFri.setOnClickListener(getDayTextClickHandler());
        mTextSat.setOnClickListener(getDayTextClickHandler());
    }

    /**
     * Get click handler for the day TextViews
     * @return click handler
     */
    public OnClickListener getDayTextClickHandler(){
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView)v;
                if(tv.getBackground() == null){
                    tv.setBackgroundResource(R.drawable.dayspicker_activebg);
                }else{
                    tv.setBackgroundResource(0);
                }
            }
        };
    }



}
