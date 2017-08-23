package com.projectaquila.controls;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.activities.ShellActivity;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.TaskDate;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.dataadapters.TaskCollectionAdapter;
import com.projectaquila.datamodels.Task;
import com.projectaquila.services.HelperService;
import com.projectaquila.views.TaskCollectionView;
import com.projectaquila.views.TaskUpdateView;

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
        mNewTask = new Task(null, new TaskDate(), "", null);
        mCurrentDate = date;
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

        /*
        Callback incrementDateAction = getDateUpdateAction(1);
        Callback decrementDateAction = getDateUpdateAction(-1);
        View draggableView = findViewById(R.id.dailytasks);
        SwipeListener.listen(draggableView, draggableView, incrementDateAction, decrementDateAction, null, DragMinX);
        SwipeListener.listen(mTasksList, draggableView, incrementDateAction, decrementDateAction, null, DragMinX);
        */

        // listen to adapter changes
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(mAdapter.getCount() == 0){
                    mNullView.setVisibility(View.VISIBLE);
                    mTasksList.setVisibility(View.GONE);
                }else{
                    mNullView.setVisibility(View.GONE);
                    mTasksList.setVisibility(View.VISIBLE);
                }
            }
        });
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
