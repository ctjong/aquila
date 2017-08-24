package com.planmaster.controls;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.planmaster.R;
import com.planmaster.activities.ShellActivity;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.common.TaskDate;
import com.planmaster.contexts.AppContext;
import com.planmaster.dataadapters.TaskCollectionAdapter;
import com.planmaster.datamodels.Task;
import com.planmaster.services.HelperService;
import com.planmaster.views.TaskCollectionView;
import com.planmaster.views.TaskUpdateView;

import java.util.HashMap;

public class DailyTasksControl extends RelativeLayout {
    private ListView mTasksList;
    private View mNullView;
    private Task mNewTask;
    private TaskCollectionAdapter mAdapter;
    private TaskDate mCurrentDate;
    private ShellActivity mShell;

    public DailyTasksControl(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new TaskDate());
    }

    public DailyTasksControl(Context context, @Nullable AttributeSet attrs, TaskDate date) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_dailytasks, this);

        // init member variables
        mShell = AppContext.getCurrent().getActivity();
        mCurrentDate = date;
        mNewTask = new Task(null, mCurrentDate, "", null);
        mAdapter = new TaskCollectionAdapter(mCurrentDate);

        // current date controls
        View.OnClickListener datePickerClickHandler = new DatePickerClickListener(mCurrentDate, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                AppContext.getCurrent().getNavigationService().goToMainActivity(TaskCollectionView.class, HelperService.getSinglePairMap("date", params.get("retval")));
            }
        });
        TextView currentDateText = (TextView)findViewById(R.id.dailytasks_date);
        TextView currentMonthText = (TextView)findViewById(R.id.dailytasks_month);
        currentDateText.setOnClickListener(datePickerClickHandler);
        currentMonthText.setOnClickListener(datePickerClickHandler);
        currentDateText.setText(TaskDate.format("EEE dd", mCurrentDate));
        currentMonthText.setText(TaskDate.format("MMMM yyyy", mCurrentDate));

        // add-task controls
        findViewById(R.id.dailytasks_add_save).setOnClickListener(getAddSaveClickListener());
        findViewById(R.id.dailytasks_add_edit).setOnClickListener(getAddEditClickListener());

        // get UI elements
        mNullView = findViewById(R.id.dailytasks_null);
        mTasksList = (ListView)findViewById(R.id.dailytasks_list);
        mTasksList.setAdapter(mAdapter);

        // show/hide null text and task list
        updateVisibility();
        mAdapter.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateVisibility();
            }
        });
    }

    /**
     * Update the visibility of null text and task list
     */
    public void updateVisibility(){
        if(mAdapter.getCount() == 0){
            mNullView.setVisibility(View.VISIBLE);
            mTasksList.setVisibility(View.GONE);
        }else{
            mNullView.setVisibility(View.GONE);
            mTasksList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Get current date that is being viewed
     * @return current date
     */
    public TaskDate getCurrentDate(){
        return mCurrentDate;
    }

    /**
     * Get a click handler for the add-save button
     */
    private View.OnClickListener getAddSaveClickListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText taskNameCtrl = ((EditText)findViewById(R.id.dailytasks_add_text));
                String taskName = taskNameCtrl.getText().toString();
                if(taskName.equals("")) return;
                mShell.showLoadingScreen();

                // get changes
                mNewTask.setName(taskName);
                taskNameCtrl.setText("");

                // save to server
                mNewTask.submitUpdate(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        mNewTask = new Task(null, mCurrentDate, "", null);
                        mShell.showLoadingScreen();
                        AppContext.getCurrent().getTasks().loadItems(new Callback() {
                            @Override
                            public void execute(CallbackParams params) {
                                mShell.hideLoadingScreen();
                            }
                        });
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
                EditText taskNameCtrl = ((EditText)findViewById(R.id.dailytasks_add_text));

                // get changes
                mNewTask.setName(taskNameCtrl.getText().toString());
                mNewTask.setDate(mCurrentDate);
                taskNameCtrl.setText("");

                HashMap<String, Object> navParams = HelperService.getSinglePairMap("task",mNewTask);
                navParams.put("date", mCurrentDate);
                AppContext.getCurrent().getNavigationService().navigateChild(TaskUpdateView.class, navParams);
            }
        };
    }
}
