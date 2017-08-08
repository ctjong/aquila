package com.projectaquila.views;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.controls.DatePickerClickListener;
import com.projectaquila.controls.SwipeListener;
import com.projectaquila.dataadapters.TaskCollectionAdapter;
import com.projectaquila.common.Callback;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.datamodels.Task;
import com.projectaquila.common.TaskDate;
import com.projectaquila.services.HelperService;

import java.util.Calendar;

public class TaskCollectionView extends ViewBase {
    private Task mNewTask;
    private TextView mCurrentDateText;
    private TextView mCurrentMonthText;
    private TaskCollectionAdapter mTaskCollectionAdapter;
    private TaskDate mCurrentDate;

    @Override
    protected int getLayoutId() {
        return R.layout.view_tasks;
    }

    @Override
    protected int getTitleBarStringId() {
        return R.string.menu_tasks;
    }

    @Override
    protected void initializeView(){
        mNewTask = new Task(null, new TaskDate(), "", null);
        mCurrentDateText = (TextView)findViewById(R.id.view_tasks_date);
        mCurrentMonthText = (TextView)findViewById(R.id.view_tasks_month);
        mTaskCollectionAdapter = new TaskCollectionAdapter();

        String dateArg = getNavArgStr("date");
        if(dateArg != null){
            mCurrentDate = TaskDate.parseDateKey(dateArg);
        }else{
            mCurrentDate = new TaskDate();
        }

        View.OnClickListener datePickerClickHandler = new DatePickerClickListener(mCurrentDate, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                mCurrentDate = (TaskDate)params.get("retval");
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
        tasksList.setAdapter(mTaskCollectionAdapter);
        refresh();
    }

    /**
     * Refresh current view for the current date
     */
    private void refresh(){
        // update date
        mCurrentDateText.setText(TaskDate.format("EEE dd", mCurrentDate));
        mCurrentMonthText.setText(TaskDate.format("MMMM yyyy", mCurrentDate));

        // update tasks list
        mTaskCollectionAdapter.loadDate(mCurrentDate, false);
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
                if(taskName.equals("")) return;
                AppContext.getCurrent().getActivity().showLoadingScreen();

                // get changes
                mNewTask.setName(taskName);
                taskNameCtrl.setText("");

                // save to server
                mNewTask.submitUpdate(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        mTaskCollectionAdapter.loadDate(mCurrentDate, true);
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
                EditText taskNameCtrl = ((EditText)findViewById(R.id.view_tasks_add_text));

                // get changes
                mNewTask.setName(taskNameCtrl.getText().toString());
                mNewTask.setDate(mCurrentDate);
                taskNameCtrl.setText("");

                AppContext.getCurrent().getNavigationService().navigateChild(TaskUpdateView.class, HelperService.getSinglePairMap("task",mNewTask));
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
            public void execute(CallbackParams params) {
                Calendar c = Calendar.getInstance();
                c.setTime(mCurrentDate);
                c.add(Calendar.DATE, numDays);
                mCurrentDate = new TaskDate(c.getTime());
                refresh();
            }
        };
    }
}
