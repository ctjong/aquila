package com.projectaquila.views;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.AppContext;
import com.projectaquila.models.ApiTaskMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TasksView extends ViewBase {
    private static final int ITEMS_PER_DATE = 100;

    private ListView mTasksList;
    private TextView mCurrentDateText;
    private TextView mCurrentMonthText;
    private ArrayAdapter<String> mTasksAdapter;
    private Date mCurrentDate;

    @Override
    protected int getLayoutId() {
        return R.layout.view_tasks;
    }

    @Override
    protected void initializeView(){
        mTasksList = (ListView)findViewById(R.id.view_tasks_list);
        mCurrentDateText = (TextView)findViewById(R.id.view_tasks_date);
        mCurrentMonthText = (TextView)findViewById(R.id.view_tasks_month);
        mTasksAdapter = new ArrayAdapter(AppContext.getCurrent().getCore(), R.layout.control_tasklistitem);
        mCurrentDate = new Date();

        mTasksList.setAdapter(mTasksAdapter);
        refresh();
    }

    /**
     * Refresh current view for the current date
     */
    private void refresh(){
        AppContext.getCurrent().getShell().showLoadingScreen();

        // update date
        mCurrentDateText.setText(getCurrentDateString("EEE dd"));
        mCurrentMonthText.setText(getCurrentDateString("MMMM yyyy"));

        // get data URL
        String dataUrl = getDataUrlForCurrentDate();
        if(dataUrl == null) return;

        // request data
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.GET, AppContext.getCurrent().getApiBase() + dataUrl, null, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                // check for errors
                if(s == S.Unauthorized){
                    AppContext.getCurrent().getNavigationService().navigate(MainView.class, null);
                    return;
                }else if(s != s.OK){
                    AppContext.getCurrent().getShell().showErrorScreen(R.string.shell_error_unknown);
                    return;
                }

                JSONArray tasks = (JSONArray)params.get("value");
                if(tasks == null) {
                    System.err.println("[TasksView.loadTasks] null tasks returned");
                    return;
                }

                // update tasks list
                mTasksAdapter.clear();
                for(int i=0; i<tasks.length(); i++){
                    try {
                        Object taskObj = tasks.get(i);
                        if(!(taskObj instanceof JSONObject)){
                            System.err.println("[TasksView.loadTasks] invalid task type. skipping.");
                            continue;
                        }
                        JSONObject task = (JSONObject) taskObj;
                        Object taskNameObj = task.get("taskname");
                        if(!(taskNameObj instanceof String)){
                            System.err.println("[TasksView.loadTasks] invalid task type. skipping.");
                            continue;
                        }
                        mTasksAdapter.add((String)taskNameObj);
                    } catch (JSONException e) {
                        System.err.println("[TasksView.loadTasks] an exception occured. skipping.");
                        e.printStackTrace();
                    }
                }
                AppContext.getCurrent().getShell().showContentScreen();
                mTasksAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Get data URL for the current date
     * @return URL string
     */
    private String getDataUrlForCurrentDate(){
        try {
            String currentDateStr = getCurrentDateString("yyMMdd");
            String condition = URLEncoder.encode("taskdate=" + currentDateStr, "UTF-8");
            String dataUrl = "/data/task/private/findbyconditions/id/0/" + ITEMS_PER_DATE + "/" + condition;
            return dataUrl;
        } catch (UnsupportedEncodingException e) {
            System.err.println("[TasksView.getDataUrlForCurrentDate] exception");
            e.printStackTrace();
            AppContext.getCurrent().getShell().showErrorScreen(R.string.shell_error_unknown);
            return null;
        }
    }

    /**
     * Get string representation of the current date
     * @param format format string
     * @return current date string
     */
    private String getCurrentDateString(String format){
        return new SimpleDateFormat(format).format(mCurrentDate);
    }
}
