package com.projectaquila.controls;

import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.models.Callback;
import com.projectaquila.models.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TaskControl {
    private static final int SliderDragMinX = 200;
    private static final int SliderClickMaxX = 10;

    private String mId;
    private String mName;
    private String mDescription;
    private Event mDeleteEvent;

    private float mSliderX;
    private float mTouchStartX;
    private View mView;

    public TaskControl(String id, String name, String description){
        mId = id;
        mName = name;
        mDescription = description;
        mDeleteEvent = new Event();
    }

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

    public View renderView(View view){
        TextView text = (TextView) view.findViewById(R.id.control_tasklistitem_text);
        text.setText(mName);
        LinearLayout slider = (LinearLayout) view.findViewById(R.id.control_tasklistitem_slider);
        slider.setOnTouchListener(new TaskListItemTouchListener());
        mSliderX = slider.getX();
        mView = view;
        return view;
    }

    public void addDeleteHandler(Callback cb){
        mDeleteEvent.addHandler(cb);
    }

    private void deleteTask(){
        System.out.println("[TaskListItem.deleteTask] deleting task " + mId);
        HashMap params = new HashMap();
        params.put("view", mView);
        mDeleteEvent.invoke(params);
    }

    private void openTask(){
        System.out.println("[TaskListItem.openTask] opening task " + mId);
    }

    private float getNewSliderX(float pointerX){
        if(Float.isNaN(mTouchStartX) || Float.isNaN(pointerX)) return mSliderX;
        return pointerX - mTouchStartX + mSliderX;
    }

    private class TaskListItemTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            float pointerX = motionEvent.getX();
            float newSliderX = getNewSliderX(pointerX);
            if(action == MotionEvent.ACTION_DOWN){
                mTouchStartX = pointerX;
            }else if(action == MotionEvent.ACTION_MOVE){
                view.setX(newSliderX);
            }else if(action == MotionEvent.ACTION_UP){
                float delta = Math.abs(pointerX - mTouchStartX);
                if(delta > SliderDragMinX){
                    deleteTask();
                }else if(delta < SliderClickMaxX){
                    openTask();
                }
                mTouchStartX = Float.NaN;
                view.setX(mSliderX);
            }
            return true;
        }
    }
}
