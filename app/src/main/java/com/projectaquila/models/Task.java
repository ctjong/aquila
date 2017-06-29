package com.projectaquila.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Task {
    private String mId;
    private TaskDate mDate;
    private String mName;
    private boolean mIsCompleted;
    private TaskRecurrence mRecurrence;
    private Event mChangedEvent;

    public Task(String id, TaskDate date, String name, boolean isCompleted, TaskRecurrence recurrence){
        mId = id;
        mDate = date;
        mName = name;
        mIsCompleted = isCompleted;
        mRecurrence = recurrence;
        mChangedEvent = new Event();
    }

    public static Task parse(Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            String dateString = json.getString("taskdate");
            TaskDate date = TaskDate.parseDateKey(dateString);
            if(date == null){
                System.err.println("[Task.parse] failed to parse date: " + dateString);
                return null;
            }
            String name = json.getString("taskname");
            boolean isCompleted = json.getBoolean("iscompleted");

            if(json.isNull("recmode")) {
                return new Task(id, date, name, isCompleted, null);
            }else{
                TaskRecurrence rec = TaskRecurrence.parse(
                        json.getInt("recmode"),
                        json.getString("recdays"),
                        json.getInt("recinterval"),
                        json.getString("recend"),
                        json.getString("recactive"));
                if(rec == null){
                    System.err.println("[Task.parse] failed to parse recurrence");
                    return null;
                }
                return new Task(id, date, name, isCompleted, rec);
            }
        }catch(JSONException e){
            System.err.println("[Task.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    public String getDateKey(){
        return mDate.toDateKey();
    }

    public void addChangedHandler(Callback handler){
        mChangedEvent.addHandler(handler);
    }

    public String getId(){
        return mId;
    }

    public TaskDate getDate(){
        return mDate;
    }

    public String getName(){
        return mName;
    }

    public boolean isCompleted(){
        return mIsCompleted;
    }

    public TaskRecurrence getRecurrence(){
        return mRecurrence;
    }

    public void setDate(TaskDate date){
        mDate = date;
    }

    public void setName(String name){
        mName = name;
    }

    public void setCompletedState(boolean isCompleted){
        mIsCompleted = isCompleted;
    }

    public void setRecurrence(TaskRecurrence recurrence){
        mRecurrence = recurrence;
    }

    public void notifyListeners(){
        mChangedEvent.invoke(null);
    }

    public HashMap<String, String> getDataMap(){
        HashMap<String, String> data = new HashMap<>();
        data.put("taskdate", mDate.toDateKey());
        data.put("taskname", mName);
        data.put("iscompleted", mIsCompleted ? "1" : "0");
        if(mRecurrence == null){
            data.put("recmode", null);
            data.put("recdays", null);
            data.put("recinterval", null);
            data.put("recactive", null);
        }else{
            data.put("recmode", mRecurrence.getMode().getValue() + "");
            data.put("recdays", mRecurrence.getDaysString());
            data.put("recinterval", mRecurrence.getInterval() + "");
            data.put("recactive", mRecurrence.getActiveString());
        }
        return data;
    }
}
