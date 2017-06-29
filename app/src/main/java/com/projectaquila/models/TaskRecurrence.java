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

    /**
     * Try to construct a TaskRecurrence from the provided details
     * @param modeInt recurrence mode integer
     * @param daysStr recurrence days string
     * @param interval recurrence interval
     * @param endStr recurrence end date
     * @param activeStr recurrence active blocks string
     * @return task recurrence object
     */
    public static TaskRecurrence parse(int modeInt, String daysStr, int interval, String endStr, String activeStr){
        // parse mode
        RecurrenceMode mode = RecurrenceMode.parse(modeInt);
        if(mode == null){
            System.err.println("[TaskRecurrence.parse] invalid mode " + modeInt);
            return null;
        }

        // parse days
        if(daysStr == null || daysStr.length() < 7) {
            System.err.println("[TaskRecurrence.parse] missing or invalid days string");
            return null;
        }
        HashSet<Integer> days = new HashSet<>();
        if(daysStr.charAt(0) == '1') days.add(Calendar.SUNDAY);
        if(daysStr.charAt(1) == '1') days.add(Calendar.MONDAY);
        if(daysStr.charAt(2) == '1') days.add(Calendar.TUESDAY);
        if(daysStr.charAt(3) == '1') days.add(Calendar.WEDNESDAY);
        if(daysStr.charAt(4) == '1') days.add(Calendar.THURSDAY);
        if(daysStr.charAt(5) == '1') days.add(Calendar.FRIDAY);
        if(daysStr.charAt(6) == '1') days.add(Calendar.SATURDAY);

        // parse end date
        TaskDate end = null;
        if(endStr == null){
            System.err.println("[TaskRecurrence.parse] missing end date");
            return null;
        }
        if(endStr.length() > 0 && !endStr.equals("null")){
            end = TaskDate.parseDateKey(endStr);
            if(end == null){
                System.err.println("[TaskRecurrence.parse] missing end date");
                return null;
            }
        }

        // parse active blocks
        List<DateBlock> activeBlocks = new LinkedList<>();
        if(activeStr != null && !activeStr.equals("null")) {
            String[] activeStrTokens = activeStr.split(",");
            for (String activeStrToken : activeStrTokens) {
                DateBlock block = DateBlock.parse(activeStrToken);
                if (block != null) {
                    activeBlocks.add(block);
                }
            }
        }

        return new TaskRecurrence(mode, days, interval, end, activeBlocks);
    }

    /**
     * Construct a task recurrence from the given details, with empty active blocks
     * @param mode recurrence mode
     * @param days recurrence days
     * @param interval recurrence interval
     * @param end recurrence end date
     */
    public TaskRecurrence(RecurrenceMode mode, HashSet<Integer> days, int interval, TaskDate end){
        this(mode, days, interval, end, new LinkedList<DateBlock>());
    }

    /**
     * Construct a task recurrence from the given details
     * @param mode recurrence mode
     * @param days recurrence days
     * @param interval recurrence interval
     * @param end recurrence end date
     * @param activeBlocks recurrence active blocks
     */
    public TaskRecurrence(RecurrenceMode mode, HashSet<Integer> days, int interval, TaskDate end, List<DateBlock> activeBlocks){
        mMode = mode;
        mDays = days;
        mInterval = interval;
        mEnd = end;
        mActiveBlocks = activeBlocks;
    }

    /**
     * Get recurrence mode
     * @return recurrence mode
     */
    public RecurrenceMode getMode(){
        return mMode;
    }

    /**
     * Get recurrence days
     * @return recurrence days
     */
    public HashSet<Integer> getDays(){
        return mDays;
    }

    /**
     * Get recurrence interval
     * @return recurrence interval
     */
    public int getInterval(){
        return mInterval;
    }

    /**
     * Get recurrence end date
     * @return recurrence end date
     */
    public TaskDate getEnd(){
        return mEnd;
    }

    /**
     * Get recurrence active blocks
     * @return recurrence active blocks
     */
    public List<DateBlock> getActiveBlocks(){
        return mActiveBlocks;
    }

    /**
     * Get string representation of the recurrence days
     * @return string representation of the recurrence days
     */
    public String getDaysString(){
        String str = "";
        str += mDays.contains(Calendar.SUNDAY) ? "1" : "0";
        str += mDays.contains(Calendar.MONDAY) ? "1" : "0";
        str += mDays.contains(Calendar.TUESDAY) ? "1" : "0";
        str += mDays.contains(Calendar.WEDNESDAY) ? "1" : "0";
        str += mDays.contains(Calendar.THURSDAY) ? "1" : "0";
        str += mDays.contains(Calendar.FRIDAY) ? "1" : "0";
        str += mDays.contains(Calendar.SATURDAY) ? "1" : "0";
        return str;
    }

    /**
     * Get string representation of the active blocks
     * @return string representation of the active blocks
     */
    public String getActiveString(){
        String str = "";
        for(DateBlock block : mActiveBlocks){
            str += (str.length() == 0 ? "" : ",") + block.toString();
        }
        return str;
    }

    /**
     * Update the current task recurrence based on the info provided in another task recurrence object
     * @param tr other task recurrence object
     */
    public void set(TaskRecurrence tr){
        boolean shouldClearActiveBlocks = false;
        if(mMode != tr.getMode()){
            shouldClearActiveBlocks = true;
        }
        HashSet<Integer> trDays = tr.getDays();
        for(int recDay : mDays){
            if(!trDays.contains(recDay)){
                shouldClearActiveBlocks = true;
            }
        }
        for(int recDay : trDays){
            if(!mDays.contains(recDay)){
                shouldClearActiveBlocks = true;
            }
        }
        if(mInterval != tr.getInterval()){
            shouldClearActiveBlocks = true;
        }
        if(shouldClearActiveBlocks){
            mActiveBlocks.clear();
        }

        mMode = tr.getMode();
        mDays = tr.getDays();
        mInterval = tr.getInterval();
        mEnd = tr.getEnd();
    }
}
