package com.projectaquila.models;

public class DateBlock {
    private TaskDate mStart;
    private TaskDate mEnd;

    public static DateBlock parse(String str, TaskDate min, TaskDate max){
        str = str.replace("[", "").replace("]", "");
        String[] tokens = str.split("|");
        if(tokens.length < 2) {
            System.err.println("[DateBlock.parse] failed to parse " + str);
            return null;
        }
        TaskDate start = min == null ? TaskDate.parseDateKey("00000000") : min;
        if(tokens[0].length() > 0) {
            start = TaskDate.parseDateKey(tokens[0]);
            if (start == null) {
                System.err.println("[DateBlock.parse] failed to parse start date " + tokens[0]);
                return null;
            }
        }
        TaskDate end = max == null ? TaskDate.parseDateKey("99991231") : max;
        if(tokens[1].length() > 0){
            end = TaskDate.parseDateKey(tokens[1]);
            if(end == null){
                System.err.println("[DateBlock.parse] failed to parse end date " + tokens[1]);
                return null;
            }
        }

        return new DateBlock(start, end);
    }

    public DateBlock(TaskDate start, TaskDate end){
        mStart = start;
        mEnd = end;
    }

    public TaskDate getEnd(){
        return mEnd;
    }

    public boolean isInRange(TaskDate date){
        return date.getTime() >= mStart.getTime() && date.getTime() <= mEnd.getTime();
    }

    @Override
    public String toString(){
        return "[" + mStart.toDateKey() + "|" + mEnd.toDateKey() + "]";
    }
}
