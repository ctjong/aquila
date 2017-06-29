package com.projectaquila.models;

public class DateBlock {
    private TaskDate mStart;
    private TaskDate mEnd;

    public static DateBlock parse(String str){
        str = str.replace("[", "").replace("]", "");
        String[] tokens = str.split("|");
        if(tokens.length < 2) {
            System.err.println("[DateBlock.parse] failed to parse " + str);
            return null;
        }
        TaskDate start = new TaskDate(Long.MIN_VALUE);
        if(tokens[0].length() > 0) {
            start = TaskDate.parseDateKey(tokens[0]);
            if (start == null) {
                System.err.println("[DateBlock.parse] failed to parse start date " + tokens[0]);
                return null;
            }
        }
        TaskDate end = new TaskDate(Long.MAX_VALUE);
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

    public TaskDate getStart(){
        return mStart;
    }

    public TaskDate getEnd(){
        return mEnd;
    }

    @Override
    public String toString(){
        return "[" + mStart.toDateKey() + "|" + mEnd.toDateKey() + "]";
    }
}
