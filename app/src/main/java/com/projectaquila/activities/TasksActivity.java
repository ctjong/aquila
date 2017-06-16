package com.projectaquila.activities;

import android.view.View;
import android.widget.LinearLayout;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.ShellActivity;
import com.projectaquila.context.AppContext;
import com.projectaquila.data.ApiTask;
import com.projectaquila.data.ApiTaskMethod;

import java.util.HashMap;

public class TasksActivity extends ShellActivity {
    private static final int ITEM_PER_PAGE = 10;

    private LinearLayout mTasksUI;
    private LinearLayout mNullResultUI;

    @Override
    protected int getLayoutId() {
        return R.layout.page_tasks;
    }

    @Override
    protected void initializeView(){
        mTasksUI = (LinearLayout) findViewById(R.id.page_tasks_nullresult);
        mNullResultUI = (LinearLayout) findViewById(R.id.page_tasks_nullresult);
        loadTasks(0);
    }

    private void loadTasks(int pageNum){
        setVisualState(VisualState.LOADING);
        int skip = ITEM_PER_PAGE * pageNum;
        String dataUrl = "/data/task/private/findall/taskdate/" + skip + "/" + ITEM_PER_PAGE;
        ApiTask.execute(ApiTaskMethod.GET, AppContext.current.getApiBase() + dataUrl, null, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params) {
                mTasksUI.setVisibility(View.GONE);
                mNullResultUI.setVisibility(View.VISIBLE);
                setVisualState(VisualState.LOADED);
            }
        });
    }
}
