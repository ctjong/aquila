package com.projectaquila.models;

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class TaskRecurrence {
    private RecurrenceMode mMode;
    private HashSet<Integer> mDays;
    private int mInterval;
    private TaskDate mEnd;
    private List<DateBlock> mActiveBlocks;

    public static TaskRecurrence parse(int modeInt, String daysStr, int interval, String endStr, String activeStr){
        RecurrenceMode mode = RecurrenceMode.parse(modeInt);
        if(mode == null){
            System.err.println("[TaskRecurrence.parse] invalid mode " + modeInt);
            return null;
        }

        if(daysStr == null) {
            System.err.println("[TaskRecurrence.parse] missing days string");
            return null;
        }
        HashSet<Integer> days = new HashSet<>();
        for (int i = 0; i < daysStr.length(); i++) {
            char c = daysStr.charAt(i);
            if (c == '0') {
                days.add(Calendar.SUNDAY);
            }else if(c == '1'){
                days.add(Calendar.MONDAY);
            }else if(c == '2'){
                days.add(Calendar.TUESDAY);
            }else if(c == '3'){
                days.add(Calendar.WEDNESDAY);
            }else if(c == '4'){
                days.add(Calendar.THURSDAY);
            }else if(c == '5'){
                days.add(Calendar.FRIDAY);
            }else if(c == '6'){
                days.add(Calendar.SATURDAY);
            }
        }

        TaskDate end = null;
        if(endStr == null){
            System.err.println("[TaskRecurrence.parse] missing end date");
            return null;
        }
        if(endStr.length() > 0){
            end = TaskDate.parseDateKey(endStr);
            if(end == null){
                System.err.println("[TaskRecurrence.parse] missing end date");
                return null;
            }
        }

        if(activeStr == null){
            System.err.println("[TaskRecurrence.parse] missing active string");
            return null;
        }
        List<DateBlock> activeBlocks = new LinkedList<>();
        String[] activeStrTokens = activeStr.split(",");
        for(String activeStrToken : activeStrTokens){
            DateBlock block = DateBlock.parse(activeStrToken);
            if(block != null){
                activeBlocks.add(block);
            }
        }

        return new TaskRecurrence(mode, days, interval, end, activeBlocks);
    }

    public TaskRecurrence(RecurrenceMode mode, HashSet<Integer> days, int interval, TaskDate end, List<DateBlock> activeBlocks){
        mMode = mode;
        mDays = days;
        mInterval = interval;
        mEnd = end;
        mActiveBlocks = activeBlocks;
    }

    public RecurrenceMode getMode(){
        return mMode;
    }

    public HashSet<Integer> getDays(){
        return mDays;
    }

    public int getInterval(){
        return mInterval;
    }

    public TaskDate getEnd(){
        return mEnd;
    }

    public List<DateBlock> getActiveBlocks(){
        return mActiveBlocks;
    }

    public String getDaysString(){
        String str = "";
        for(int day : mDays){
            if(day == Calendar.SUNDAY){
                str += "0";
            }else if(day == Calendar.MONDAY){
                str += "1";
            }else if(day == Calendar.TUESDAY){
                str += "2";
            }else if(day == Calendar.WEDNESDAY){
                str += "3";
            }else if(day == Calendar.THURSDAY){
                str += "4";
            }else if(day == Calendar.FRIDAY){
                str += "5";
            }else if(day == Calendar.SATURDAY){
                str += "6";
            }
        }
        return str;
    }

    public String getActiveString(){
        String str = "";
        for(DateBlock block : mActiveBlocks){
            str += (str.length() == 0 ? "" : ",") + block.toString();
        }
        return str;
    }

    public void setMode(RecurrenceMode recMode){
        mMode = recMode;
    }

    public void setInterval(int recInterval){
        mInterval = recInterval;
    }

    public void setEnd(TaskDate end){
        mEnd = end;
    }
}
