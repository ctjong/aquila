package com.projectaquila.models;

import com.projectaquila.services.HelperService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Task {
    private String mId;
    private Date mDate;
    private String mName;
    private boolean mIsCompleted;
    private Event mChangedEvent;

    public Task(String id, Date date, String name, boolean isCompleted){
        mId = id;
        mDate = date;
        mName = name;
        mIsCompleted = isCompleted;
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
            Date date = HelperService.parseDateKey(dateString);
            if(date == null){
                System.err.println("[Task.parse] failed to parse date: " + dateString);
                return null;
            }
            String name = json.getString("taskname");
            boolean isCompleted = json.getBoolean("iscompleted");
            return new Task(id, date, name, isCompleted);
        }catch(JSONException e){
            System.err.println("[Task.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    public void addChangedHandler(Callback handler){
        mChangedEvent.addHandler(handler);
    }

    public String getId(){
        return mId;
    }

    public Date getDate(){
        return mDate;
    }

    public String getName(){
        return mName;
    }

    public boolean isCompleted(){
        return mIsCompleted;
    }

    public void setDate(Date date){
        mDate = date;
    }

    public void setName(String name){
        mName = name;
    }

    public void setCompletedState(boolean isCompleted){
        mIsCompleted = isCompleted;
    }

    public void notifyListeners(){
        mChangedEvent.invoke(null);
    }
}
