package com.projectaquila.views;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.controls.SwipeListener;
import com.projectaquila.controls.TasksAdapter;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.AppContext;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.services.HelperService;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TasksView extends ViewBase {
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
        mCurrentDateText = (TextView)findViewById(R.id.view_tasks_date);
        mCurrentMonthText = (TextView)findViewById(R.id.view_tasks_month);
        mTasksAdapter = new TasksAdapter();
        mCurrentDate = new Date();

        View.OnClickListener datePickerClickHandler = HelperService.getDatePickerClickHandler(mCurrentDate, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                mCurrentDate = (Date)params.get("retval");
                refresh();
            }
        });
        mCurrentDateText.setOnClickListener(datePickerClickHandler);
        mCurrentMonthText.setOnClickListener(datePickerClickHandler);

        findViewById(R.id.view_tasks_add_save).setOnClickListener(getAddSaveClickListener());
        findViewById(R.id.view_tasks_add_edit).setOnClickListener(getAddEditClickListener());

        ListView tasksList = (ListView)findViewById(R.id.view_tasks_list);
        Callback incrementDateAction = getDateUpdateAction(1);
        Callback decrementDateAction = getDateUpdateAction(-1);
        View draggableView = findViewById(R.id.view_tasks);
        SwipeListener.listen(draggableView, draggableView, incrementDateAction, decrementDateAction, null);
        SwipeListener.listen(tasksList, draggableView, incrementDateAction, decrementDateAction, null);
        tasksList.setAdapter(mTasksAdapter);
        refresh();
    }

    /**
     * Refresh current view for the current date
     */
    private void refresh(){
        // update date
        mCurrentDateText.setText(HelperService.getDateString("EEE dd", mCurrentDate));
        mCurrentMonthText.setText(HelperService.getDateString("MMMM yyyy", mCurrentDate));

        // update tasks list
        mTasksAdapter.loadDate(mCurrentDate, false);
    }

    /**
     * Get a click handler for the add-save button
     */
    private View.OnClickListener getAddSaveClickListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AppContext.getCurrent().getActivity().showLoadingScreen();
                EditText taskNameCtrl = ((EditText)findViewById(R.id.view_tasks_add_text));
                String taskName = taskNameCtrl.getText().toString();
                String taskDate = HelperService.getDateString("yyMMdd", mCurrentDate);
                taskNameCtrl.setText("");
                HashMap<String,String> data = new HashMap<>();
                data.put("taskname", taskName);
                data.put("taskdate", taskDate);
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.POST, "/data/task/public", data, new Callback() {
                    @Override
                    public void execute(HashMap<String, Object> params, S s) {
                        if(s == S.Error) return;
                        mTasksAdapter.loadDate(mCurrentDate, true);
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
}
