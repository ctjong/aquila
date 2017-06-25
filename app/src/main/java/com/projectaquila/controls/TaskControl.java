package com.projectaquila.controls;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.Callback;
import com.projectaquila.models.Event;
import com.projectaquila.models.S;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TaskControl {
    private String mId;
    private Date mDate;
    private String mName;
    private Event mDeleteEvent;

    public static TaskControl parse(Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            Date date = new SimpleDateFormat("yyMMdd").parse(json.getString("taskdate"));
            String name = json.getString("taskname");
            return new TaskControl(id, date, name);
        }catch(JSONException e){
            System.err.println("[TaskControl.parse] received JSONException. skipping.");
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            System.err.println("[TaskControl.parse] received ParseException. skipping.");
            e.printStackTrace();
            return null;
        }
    }

    public TaskControl(String id, Date date, String name){
        mId = id;
        mDate = date;
        mName = name;
        mDeleteEvent = new Event();
    }

    public Date getDate(){
        return mDate;
    }

    public View renderView(View view){
        TextView text = (TextView) view.findViewById(R.id.control_tasklistitem_text);
        text.setText(mName);

        Callback deleteTaskAction = getDeleteTaskAction();
        Callback openTaskAction = getOpenTaskAction();
        View slider = view.findViewById(R.id.control_tasklistitem_slider);
        SwipeListener.listen(slider, slider, deleteTaskAction, deleteTaskAction, openTaskAction);
        return view;
    }

    public void addDeleteHandler(Callback cb){
        mDeleteEvent.addHandler(cb);
    }

    private Callback getDeleteTaskAction(){
        return new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                System.out.println("[TaskListItem.deleteTask] deleting task " + mId);
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.DELETE, "/data/task/private/" + mId, null, null);
                // update UI without waiting for delete request, for seamless UI response.
                mDeleteEvent.invoke(null);
            }
        };
    }

    private Callback getOpenTaskAction(){
        return new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                System.out.println("[TaskListItem.openTask] opening task " + mId);
                //TODO
            }
        };
    }
}
