package com.projectaquila.views;

import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.controls.SwipeListener;
import com.projectaquila.controls.TaskControl;
import com.projectaquila.controls.TasksAdapter;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.AppContext;
import com.projectaquila.models.ApiTaskMethod;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TasksView extends ViewBase {
    private static final int ITEMS_PER_DATE = 100;

    private ListView mTasksList;
    private TextView mCurrentDateText;
    private TextView mCurrentMonthText;
    private TasksAdapter mTasksAdapter;
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
        mTasksAdapter = new TasksAdapter();
        mCurrentDate = new Date();

        findViewById(R.id.view_tasks_add_save).setOnClickListener(getAddSaveClickListener());
        findViewById(R.id.view_tasks_add_edit).setOnClickListener(getAddEditClickListener());

        Callback incrementDateAction = getDateUpdateAction(1);
        Callback decrementDateAction = getDateUpdateAction(-1);
        View view = findViewById(R.id.view_tasks);
        view.setOnTouchListener(new SwipeListener(view, incrementDateAction, decrementDateAction, null));
        mTasksList.setOnTouchListener(new SwipeListener(view, incrementDateAction, decrementDateAction, null));

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
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.GET, dataUrl, null, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                // check for errors
                if(s == S.Error) return;
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
                        TaskControl task = TaskControl.parse(taskObj);
                        if(task == null){
                            System.err.println("[TasksView.loadTasks] failed to parse task object. skipping.");
                            continue;
                        }
                        mTasksAdapter.add(task);
                    } catch (JSONException e) {
                        System.err.println("[TasksView.loadTasks] an exception occured. skipping.");
                        e.printStackTrace();
                    }
                }
                mTasksAdapter.notifyDataSetChanged();
                AppContext.getCurrent().getShell().showContentScreen();
            }
        });
    }

    /**
     * Get a click handler for the add-save button
     */
    private View.OnClickListener getAddSaveClickListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText taskNameCtrl = ((EditText)findViewById(R.id.view_tasks_add_text));
                String taskName = taskNameCtrl.getText().toString();
                String taskDate = getCurrentDateString("yyMMdd");
                taskNameCtrl.setText("");
                HashMap<String,String> data = new HashMap<>();
                data.put("taskname", taskName);
                data.put("taskdate", taskDate);
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.POST, "/data/task/public", data, new Callback() {
                    @Override
                    public void execute(HashMap<String, Object> params, S s) {
                        if(s == S.Error) return;
                        refresh();
                    }
                });
            }
        };
    }

    /**
     * Get a click handler for the add-edit button
     */
    private View.OnClickListener getAddEditClickListener(){
        return new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        };
    }

    /**
     * Get an action that updates the current date by adding/substracting it with the given number of days
     * @param numDays number of days
     */
    private Callback getDateUpdateAction(final int numDays) {
        return new Callback(){
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                Calendar c = Calendar.getInstance();
                c.setTime(mCurrentDate);
                c.add(Calendar.DATE, numDays);
                mCurrentDate = c.getTime();
                refresh();
            }
        };
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
