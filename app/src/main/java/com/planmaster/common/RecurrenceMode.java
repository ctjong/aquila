package com.planmaster.common;

import com.planmaster.contexts.AppContext;
import com.planmaster.R;

public enum RecurrenceMode {
    None(0, R.string.taskrecurrence_none),
    Daily(1, R.string.taskrecurrence_daily),
    Weekly(2, R.string.taskrecurrence_weekly),
    MonthlyDateBased(3, R.string.taskrecurrence_monthlydatebased),
    MonthlyWeekBased(4, R.string.taskrecurrence_monthlyweekbased),
    Yearly(5, R.string.taskrecurrence_yearly);

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
