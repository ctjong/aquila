package com.projectaquila.models;

import com.projectaquila.AppContext;
import com.projectaquila.R;

public enum RecurrenceMode {
    None(0, R.string.taskupdate_recmode_none),
    Daily(1, R.string.taskupdate_recmode_daily),
    Weekly(2, R.string.taskupdate_recmode_weekly),
    MonthlyDateBased(3, R.string.taskupdate_recmode_monthlydatebased),
    MonthlyWeekBased(4, R.string.taskupdate_recmode_monthlyweekbased),
    Yearly(5, R.string.taskupdate_recmode_yearly);

    private final int mValue;
    private final int mStringId;

    RecurrenceMode(int value, int stringId) {
        mValue = value;
        mStringId = stringId;
    }

    public int getValue() {
        return mValue;
    }

    @Override
    public String toString(){
        return AppContext.getCurrent().getActivity().getString(mStringId);
    }

    public static RecurrenceMode parse(int value){
        if(value == None.getValue()) return None;
        if(value == Daily.getValue()) return Daily;
        if(value == Weekly.getValue()) return Weekly;
        if(value == MonthlyDateBased.getValue()) return MonthlyDateBased;
        if(value == MonthlyWeekBased.getValue()) return MonthlyWeekBased;
        if(value == Yearly.getValue()) return Yearly;
        return null;
    }
}
