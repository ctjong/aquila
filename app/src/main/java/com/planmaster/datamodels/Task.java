package com.planmaster.datamodels;

import com.planmaster.common.Callback;
import com.planmaster.common.TaskDate;
import com.planmaster.common.TaskRecurrence;
import com.planmaster.services.HelperService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Task extends DataModelBase {
    private TaskDate mDate;
    private String mName;
    private TaskRecurrence mRecurrence;
    private boolean mIsDeleted;

    public Task(String id, TaskDate date, String name, TaskRecurrence recurrence){
        super(id);
        mDate = date;
        mName = name;
        mRecurrence = recurrence;
        mIsDeleted = false;
    }

    public static Task parse(Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            String dateString = json.getString("date");
            TaskDate date = TaskDate.parseDateKey(dateString);
            if(date == null){
                HelperService.logError("[Task.parse] failed to parse date: " + dateString);
                return null;
            }
            String name = json.getString("name");

            Task task = new Task(id, date, name, null);
            int recMode = json.getInt("recmode");
            if(recMode != 0) {
                TaskRecurrence rec = TaskRecurrence.parse(task,recMode, json.getString("recdays"),
                        json.getInt("recinterval"), json.getString("recend"), json.getString("recholes"));
                if(rec == null){
                    HelperService.logError("[Task.parse] failed to parse recurrence");
                    return null;
                }
                task.setRecurrence(rec);
            }
            return task;
        }catch(JSONException e){
            HelperService.logError("[Task.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
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

    public boolean isDeleted(){
        return mIsDeleted;
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

    public void complete(Callback cb){
        System.out.println("[Task.complete] completing task " + getId());
        submitDelete(cb);
        notifyListeners();
    }

    public void completeOccurrence(TaskDate occDate, Callback cb){
        String occDateKey = occDate.toDateKey();
        System.out.println("[Task.completeOccurrence] completing recurrence task " + getId() + " at " + occDateKey);
        if(mDate.toDateKey().equals(occDateKey)){
            if(mRecurrence.shiftToNextOccurrence()){
                System.out.println("[Task.completeOccurrence] shifting recurrence series to " + getDate());
                submitUpdate(cb);
            }else{
                System.out.println("[Task.completeOccurrence] completing recurrence series " + getId());
                submitDelete(cb);
            }
        }else{
            mRecurrence.getHoles().add(occDateKey);
            submitUpdate(cb);
        }
        notifyListeners();
    }

    @Override
    protected HashMap<String, String> getCreateDataMap(){
        HashMap<String, String> data = new HashMap<>();
        data.put("date", mDate.toDateKey());
        data.put("name", mName);
        if(mRecurrence == null){
            data.put("recmode", "0");
            data.put("recdays", null);
            data.put("recinterval", "0");
            data.put("recholes", "0");
            data.put("recend", "0");
        }else{
            data.put("recmode", mRecurrence.getMode().getValue() + "");
            data.put("recdays", mRecurrence.getDaysString());
            data.put("recinterval", mRecurrence.getInterval() + "");
            data.put("recholes", mRecurrence.getHolesString());
            data.put("recend", mRecurrence.getEnd() == null ? null : mRecurrence.getEnd().toDateKey());
        }
        return data;
    }

    @Override
    protected String getUpdateUrl() {
        return "/data/task/" + getId();
    }

    @Override
    protected String getCreateUrl() {
        return "/data/task";
    }

    @Override
    protected String getDeleteUrl() {
        return "/data/task/" + getId();
    }
}
