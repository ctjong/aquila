package com.projectaquila.models;

import com.projectaquila.AppContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Task {
    private String mId;
    private TaskDate mDate;
    private String mName;
    private TaskRecurrence mRecurrence;
    private Event mChangedEvent;

    public Task(String id, TaskDate date, String name, TaskRecurrence recurrence){
        mId = id;
        mDate = date;
        mName = name;
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

            Task task = new Task(id, date, name, null);
            if(!json.isNull("recmode")) {
                TaskRecurrence rec = TaskRecurrence.parse(
                        task,
                        json.getInt("recmode"),
                        json.getString("recdays"),
                        json.getInt("recinterval"),
                        json.getString("recend"),
                        json.getString("recholes"));
                if(rec == null){
                    System.err.println("[Task.parse] failed to parse recurrence");
                    return null;
                }
                task.setRecurrence(rec);
            }
            return task;
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

    public TaskRecurrence getRecurrence(){
        return mRecurrence;
    }

    public void setDate(TaskDate date){
        if(date != null && !mDate.toDateKey().equals(date.toDateKey())) {
            mDate = date;
            if (mRecurrence != null) {
                mRecurrence.getHoles().clear();
            }
        }
    }

    public void setName(String name){
        mName = name;
    }

    public void setRecurrence(TaskRecurrence recurrence){
        if(mRecurrence != null && recurrence != null) {
            mRecurrence.set(recurrence);
        }else{
            mRecurrence = recurrence;
        }
    }

    public void notifyListeners(){
        mChangedEvent.invoke(null);
    }

    public HashMap<String, String> getDataMap(){
        HashMap<String, String> data = new HashMap<>();
        data.put("taskdate", mDate.toDateKey());
        data.put("taskname", mName);
        if(mRecurrence == null){
            data.put("recmode", null);
            data.put("recdays", null);
            data.put("recinterval", null);
            data.put("recholes", null);
            data.put("recend", null);
        }else{
            data.put("recmode", mRecurrence.getMode().getValue() + "");
            data.put("recdays", mRecurrence.getDaysString());
            data.put("recinterval", mRecurrence.getInterval() + "");
            data.put("recholes", mRecurrence.getHolesString());
            data.put("recend", mRecurrence.getEnd() == null ? null : mRecurrence.getEnd().toDateKey());
        }
        return data;
    }

    public void complete(Callback cb){
        System.out.println("[Task.complete] completing task " + mId);
        AppContext.getCurrent().getTasks().remove(mId);
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.DELETE, "/data/task/" + mId, null, cb);
        notifyListeners();
    }

    public void completeOccurrence(TaskDate occDate, Callback cb){
        String occDateKey = occDate.toDateKey();
        System.out.println("[Task.completeOccurrence] completing recurrence task " + mId + " at " + occDateKey);
        if(mDate.toDateKey().equals(occDateKey)){
            if(mRecurrence.shiftToNextOccurrence()){
                System.out.println("[Task.completeOccurrence] shifting recurrence series to " + getDateKey());
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/" + mId, getDataMap(), cb);
            }else{
                System.out.println("[Task.completeOccurrence] completing recurrence series " + mId);
                AppContext.getCurrent().getTasks().remove(mId);
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.DELETE, "/data/task/" + mId, null, cb);
            }
        }else{
            mRecurrence.getHoles().add(occDateKey);
            AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/" + mId, getDataMap(), cb);
        }
        notifyListeners();
    }
}
