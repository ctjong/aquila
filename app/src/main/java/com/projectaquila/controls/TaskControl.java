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

import java.util.HashMap;

public class TaskControl {
    private String mId;
    private String mName;
    private String mDescription;
    private Event mDeleteEvent;

    public static TaskControl parse(Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            String name = json.getString("taskname");
            String description = json.getString("taskdescription");
            return new TaskControl(id, name, description);
        }catch(JSONException e){
            return null;
        }
    }

    private TaskControl(String id, String name, String description){
        mId = id;
        mName = name;
        mDescription = description;
        mDeleteEvent = new Event();
    }

    public View renderView(View view){
        TextView text = (TextView) view.findViewById(R.id.control_tasklistitem_text);
        text.setText(mName);
        LinearLayout slider = (LinearLayout) view.findViewById(R.id.control_tasklistitem_slider);

        Callback deleteTaskAction = getDeleteTaskAction();
        Callback openTaskAction = getOpenTaskAction();
        slider.setOnTouchListener(new SwipeListener(view, deleteTaskAction, deleteTaskAction, openTaskAction));
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
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.DELETE, "/data/task/private/" + mId, null, new Callback() {
                    @Override
                    public void execute(HashMap<String, Object> params, S s) {
                        // do nothing
                    }
                });
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
