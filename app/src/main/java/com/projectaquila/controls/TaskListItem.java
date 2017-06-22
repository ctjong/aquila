package com.projectaquila.controls;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.Event;
import com.projectaquila.R;
import com.projectaquila.Callback;
import com.projectaquila.views.ViewBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TaskListItem {
    private static final int SliderDragMinX = 200;
    private static final int SliderClickMaxX = 10;

    private String mId;
    private String mTitle;
    private FrameLayout mView;
    private float mSliderX;
    private float mTouchStartX;

    private Event mDeleteEvent;

    public TaskListItem(String id, String title){
        mId = id;
        mTitle = title;
        mDeleteEvent = new Event();
    }

    public static TaskListItem create(JSONObject object){
        try{
            String id = object.getString("id");
            String title = object.getString("title");
            return new TaskListItem(id, title);
        }catch(JSONException e){
            return null;
        }
    }

    public FrameLayout renderView(){
        if(mView != null) {
            return mView;
        }
        mView = (FrameLayout) LayoutInflater.from(AppContext.current.getShell()).inflate(R.layout.control_tasklistitem, null);
        TextView text = (TextView) mView.findViewById(R.id.control_tasklistitem_text);
        text.setText(mTitle);
        LinearLayout slider = (LinearLayout) mView.findViewById(R.id.control_tasklistitem_slider);
        slider.setOnTouchListener(new TaskListItemTouchListener());
        mSliderX = slider.getX();
        return mView;
    }

    public void addDeleteHandler(Callback cb){
        mDeleteEvent.addHandler(cb);
    }

    private void deleteTask(){
        System.out.println("deleting task " + mId);
        HashMap params = new HashMap();
        params.put("item", mView);
        mDeleteEvent.invoke(params);
    }

    private void openTask(){
        System.out.println("opening task " + mId);
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
