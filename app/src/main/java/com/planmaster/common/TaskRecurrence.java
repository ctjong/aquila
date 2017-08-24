package com.planmaster.common;

import com.planmaster.datamodels.Task;
import com.planmaster.services.HelperService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TaskRecurrence {
    private Task mTask;
    private RecurrenceMode mMode;
    private HashSet<Integer> mDays;
    private int mInterval;
    private TaskDate mEnd;
    private HashSet<String> mHoles;

    /**
     * Try to construct a TaskRecurrence from the provided details
     * @param modeInt recurrence mode integer
     * @param daysStr recurrence days string
     * @param interval recurrence interval
     * @param endStr recurrence end date
     * @param holesStr string that lists out the holes in the recurrence sequence
     * @return task recurrence object
     */
    public static TaskRecurrence parse(Task task, int modeInt, String daysStr, int interval, String endStr, String holesStr){
        // parse mode
        RecurrenceMode mode = RecurrenceMode.parse(modeInt);
        if(mode == null){
            HelperService.logError("[TaskRecurrence.parse] invalid mode " + modeInt);
            return null;
        }

        // parse days
        if(daysStr == null || daysStr.length() < 7) {
            HelperService.logError("[TaskRecurrence.parse] missing or invalid days string");
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
        if(endStr != null && endStr.length() > 0 && !endStr.equals("null")){
            end = TaskDate.parseDateKey(endStr);
            if(end == null){
                HelperService.logError("[TaskRecurrence.parse] missing end date");
                return null;
            }
        }

        // parse active blocks
        HashSet<String> holes = new HashSet<>();
        if(holesStr != null && !holesStr.equals("null") && !holesStr.equals("")) {
            String[] holesStrArr = holesStr.split(",");
            for (String holeStr : holesStrArr) {
                TaskDate hole = TaskDate.parseDateKey(holeStr);
                if(hole != null){
                    holes.add(holeStr);
                }
            }
        }

        return new TaskRecurrence(task, mode, days, interval, end, holes);
    }

    /**
     * Construct a task recurrence from the given details, with no holes
     * @param task parent task
     * @param mode recurrence mode
     * @param days recurrence days
     * @param interval recurrence interval
     * @param end recurrence end date
     */
    public TaskRecurrence(Task task, RecurrenceMode mode, HashSet<Integer> days, int interval, TaskDate end){
        this(task, mode, days, interval, end, new HashSet<String>());
    }

    /**
     * Construct a task recurrence from the given details
     * @param task parent task
     * @param mode recurrence mode
     * @param days recurrence days
     * @param interval recurrence interval
     * @param end recurrence end date
     * @param holes date holes in the recurrence sequence
     */
    public TaskRecurrence(Task task, RecurrenceMode mode, HashSet<Integer> days, int interval, TaskDate end, HashSet<String> holes){
        mTask = task;
        mMode = mode;
        mDays = days;
        mInterval = interval;
        mEnd = end == null ? TaskDate.MAX : end;
        mHoles = holes;
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
    public List<Integer> getDays(){
        List<Integer> daysList = new ArrayList<>(mDays);
        Collections.sort(daysList);
        return daysList;
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
     * Get a list of date-holes in the recurrence sequence
     * @return recurrence holes
     */
    public HashSet<String> getHoles(){
        return mHoles;
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
     * Get string representation of the recurrence holes
     * @return string representation of the recurrence holes
     */
    public String getHolesString(){
        String str = "";
        for(String hole : mHoles){
            str += (str.length() == 0 ? "" : ",") + hole;
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
        List<Integer> trDays = tr.getDays();
        for(int trDay : trDays){
            if(!mDays.contains(trDay)){
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
            mHoles.clear();
        }

        mMode = tr.getMode();
        mDays = new HashSet<>(trDays);
        mInterval = tr.getInterval();
        mEnd = tr.getEnd();
    }

    /**
     * Check whether the given date is included in the recurrence
     * @return true if included, false otherwise
     */
    public boolean isIncluded(TaskDate target){
        if(target.getTime() < mTask.getDate().getTime() || target.getTime() > mEnd.getTime()){
            return false;
        }
        String targetKey = target.toDateKey();
        for(String hole : mHoles){
            if(hole.equals(targetKey)){
                return false;
            }
        }
        if(mMode == RecurrenceMode.Daily){
            return true;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(target);
        int targetDay = c.get(Calendar.DAY_OF_WEEK);
        int targetDate = c.get(Calendar.DATE);
        int targetWeek = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        int targetMonth = c.get(Calendar.MONTH);
        c.setTime(mTask.getDate());
        int startDay = c.get(Calendar.DAY_OF_WEEK);
        int startDate = c.get(Calendar.DATE);
        int startWeek = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        int startMonth = c.get(Calendar.MONTH);
        if(mMode == RecurrenceMode.Weekly){
            for(int day : mDays){
                if(targetDay == day) {
                    return true;
                }
            }
            return false;
        }
        if(mMode == RecurrenceMode.MonthlyDateBased){
            return targetDate == startDate;
        }
        if(mMode == RecurrenceMode.MonthlyWeekBased){
            return targetWeek == startWeek && targetDay == startDay;
        }
        if(mMode == RecurrenceMode.Yearly){
            return targetDate == startDate && targetMonth == startMonth;
        }
        return false;
    }

    /**
     * Shift task date to the next occurrence in the series
     * @return true if next occurrence exists, false otherwise
     */
    public boolean shiftToNextOccurrence(){
        TaskDate currentDate = mTask.getDate();
        String currentDateKey = currentDate.toDateKey();
        List<Integer> daysList = getDays();
        while(currentDate == mTask.getDate() || mHoles.contains(currentDateKey)){
            mHoles.remove(currentDateKey);
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            int date = c.get(Calendar.DATE);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            int weekOfMonth = c.get(Calendar.WEEK_OF_MONTH);
            int dayOfWeekInMonth = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            if(mMode == RecurrenceMode.Daily){
                c.set(Calendar.DATE, date + 1);
            }else if(mMode == RecurrenceMode.Weekly){
                if(daysList.size() == 0)
                    return false;
                boolean found = false;
                boolean updated = false;
                for(int day : daysList){
                    if(found){
                        c.set(Calendar.DAY_OF_WEEK, day);
                        updated = true;
                        break;
                    }
                    if(dayOfWeek == day){
                        found = true;
                    }
                }
                if(!updated){
                    c.set(Calendar.DAY_OF_WEEK, daysList.get(0));
                    c.set(Calendar.WEEK_OF_MONTH, weekOfMonth + 1);
                }
            }else if(mMode == RecurrenceMode.MonthlyDateBased){
                c.set(Calendar.MONTH, month + 1);
            }else if(mMode == RecurrenceMode.MonthlyWeekBased) {
                c.set(Calendar.MONTH, month + 1);
                c.set(Calendar.DAY_OF_WEEK_IN_MONTH, dayOfWeekInMonth);
                c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            }else{
                c.set(Calendar.YEAR, year + 1);
            }
            currentDate = new TaskDate(c.getTime());
            currentDateKey = currentDate.toDateKey();
        }
        if(!currentDateKey.equals(mEnd.toDateKey()) && currentDate.getTime() > mEnd.getTime()) {
            return false;
        }
        mTask.setDate(currentDate);
        return true;
    }
}
