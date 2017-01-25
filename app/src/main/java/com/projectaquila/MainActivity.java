package com.projectaquila;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ShellActivity {
    private final String TestDataUrl = "http://www.ctjong.com/aquila/test.json";

    private enum MainPageVisualState {LOADING, LOADED, ERROR};
    private LinearLayout mTaskList;
    private ProgressBar mProgressBar;
    private TextView mErrorText;
    private ScrollView mTaskListView;

    @Override
    protected int getLayoutId() {
        return R.layout.page_main;
    }

    @Override
    protected void initializeView(){
        mTaskList = (LinearLayout) findViewById(R.id.page_main_tasklist);
        mProgressBar = (ProgressBar) findViewById(R.id.page_main_loading);
        mErrorText = (TextView) findViewById(R.id.page_main_errortext);
        mTaskListView = (ScrollView) findViewById(R.id.page_main_tasklistview);

        setVisualState(MainPageVisualState.LOADING);
        new ApiTask(TestDataUrl, new ApiCallback() {
            @Override
            public void execute(JSONObject response) {
                render(response);
            }
        }).execute();
    }

    private void render(JSONObject data){
        if(data == null){
            setVisualState(MainPageVisualState.ERROR);
            return;
        }
        mTaskList.removeAllViews();
        try{
            JSONArray tasks = data.getJSONArray("tasks");
            for (int i = 0; i < tasks.length(); i++) {
                TaskListItem item = TaskListItem.parse(tasks.getJSONObject(i));
                if(item == null) continue;
                mTaskList.addView(item.getView(this));
            }
            setVisualState(MainPageVisualState.LOADED);
        }catch(JSONException e){
            setVisualState(MainPageVisualState.ERROR);
        }
    }

    private void setVisualState(MainPageVisualState visualState){
        mTaskListView.setVisibility(View.GONE);
        mErrorText.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        switch(visualState){
            case LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case LOADED:
                mTaskListView.setVisibility(View.VISIBLE);
                break;
            default:
                mErrorText.setVisibility(View.VISIBLE);
                break;
        }
    }
}
