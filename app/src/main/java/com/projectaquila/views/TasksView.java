package com.projectaquila.views;

import android.view.View;
import android.widget.LinearLayout;

import com.projectaquila.R;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.AppContext;
import com.projectaquila.models.ApiTaskMethod;

import java.util.HashMap;

public class TasksView extends ViewBase {
    private static final int ITEM_PER_PAGE = 10;

    private LinearLayout mTasksUI;
    private LinearLayout mNullResultUI;

    @Override
    protected int getLayoutId() {
        return R.layout.view_tasks;
    }

    @Override
    protected void initializeView(){
        mTasksUI = (LinearLayout) findViewById(R.id.view_tasks_nullresult);
        mNullResultUI = (LinearLayout) findViewById(R.id.view_tasks_nullresult);
        loadTasks(0);
    }

    private void loadTasks(int pageNum){
        AppContext.getCurrent().getShell().showLoadingScreen();
        int skip = ITEM_PER_PAGE * pageNum;
        String dataUrl = "/data/task/private/findall/taskdate/" + skip + "/" + ITEM_PER_PAGE;
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.GET, AppContext.getCurrent().getApiBase() + dataUrl, null, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                if(s == S.Unauthorized){
                    AppContext.getCurrent().getNavigationService().navigate(MainView.class, null);
                    return;
                }
                mTasksUI.setVisibility(View.GONE);
                mNullResultUI.setVisibility(View.VISIBLE);
                AppContext.getCurrent().getShell().showContentScreen();
            }
        });
    }
}
