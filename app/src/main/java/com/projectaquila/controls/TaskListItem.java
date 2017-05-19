package com.projectaquila.controls;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.activities.MainActivity;
import com.projectaquila.common.ShellActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskListItem {
    private static final int SliderDragMinX = 200;
    private static final int SliderClickMaxX = 10;

    private String mId;
    private String mTitle;
    private ShellActivity mPage;
    private FrameLayout mView;
    private float mSliderX;
    private float mTouchStartX;

    public TaskListItem(String id, String title, MainActivity page){
        mId = id;
        mTitle = title;
        mPage = page;
        initializeView();
    }

    public static TaskListItem create(JSONObject object, MainActivity page){
        try{
            String id = object.getString("id");
            String title = object.getString("title");
            return new TaskListItem(id, title, page);
        }catch(JSONException e){
            return null;
        }
    }

    public FrameLayout getView(){
        return mView;
    }

    private void deleteTask(){
        //TODO
        System.out.println("deleting task " + mId);
        //mPage.RemoveFromPage(mView);
    }

    private void openTask(){
        //TODO
        System.out.println("opening task " + mId);
    }

    private void initializeView(){
        mView = (FrameLayout) LayoutInflater.from(mPage).inflate(R.layout.view_tasklistitem, null);
        TextView text = (TextView) mView.findViewById(R.id.view_tasklistitem_text);
        text.setText(mTitle);
        LinearLayout slider = (LinearLayout) mView.findViewById(R.id.view_tasklistitem_slider);
        slider.setOnTouchListener(new TaskListItemTouchListener());
        mSliderX = slider.getX();
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
