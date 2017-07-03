package com.projectaquila.views;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.controls.DateEditText;
import com.projectaquila.controls.DaysPicker;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.Callback;
import com.projectaquila.models.CallbackParams;
import com.projectaquila.models.RecurrenceMode;
import com.projectaquila.models.Task;
import com.projectaquila.models.TaskDate;
import com.projectaquila.models.TaskRecurrence;
import com.projectaquila.services.HelperService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class TaskUpdateView extends ViewBase {
    private Task mTask;
    private EditText mTaskNameText;
    private DateEditText mTaskDateText;

    private Spinner mRecModeSpinner;
    private ArrayAdapter<RecurrenceMode> mRecModeSpinnerAdapter;
    private DaysPicker mRecDaysPicker;
    private EditText mRecIntervalText;
    private Spinner mRecEndSpinner;
    private DateEditText mRecEndText;

    /**
     * Get layout id
     * @return layout id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_taskupdate;
    }

    /**
     * Initialize the view
     */
    @Override
    protected void initializeView(){
        initializeTaskModel();
        initializeCommonControls();
        initializeRecControls();
        updateView();
        AppContext.getCurrent().getActivity().setToolbarText(R.string.taskupdate_title);
        AppContext.getCurrent().getActivity().showContentScreen();
    }

    /**
     * Initialize task model based on whether we are on update/create mode
     */
    private void initializeTaskModel(){
        String taskId = getNavArg("id");
        if(taskId != null){
            System.out.println("[TaskUpdateView.initializeView] mode=update, id=" + taskId);
            if(!AppContext.getCurrent().getTasks().containsKey(taskId)) {
                System.err.println("[TaskUpdateView.initializeView] invalid task id found in nav params");
                AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
                return;
            }
            Task originalTask = AppContext.getCurrent().getTasks().get(taskId);
            mTask = new Task(originalTask.getId(), originalTask.getDate(), originalTask.getName(), originalTask.isCompleted(), originalTask.getRecurrence());
        }else{
            System.out.println("[TaskUpdateView.initializeView] mode=create");
            String taskName = getNavArg("taskname");
            TaskDate taskDate = TaskDate.parseDateKey(getNavArg("taskdate"));
            if(taskName == null || taskDate == null) {
                System.err.println("[TaskUpdateView.initializeView] missing task name/date");
                AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
                return;
            }
            mTask = new Task(null, taskDate, taskName, false, null);
        }
    }

    /**
     * Initialize common controls
     */
    private void initializeCommonControls(){
        mTaskNameText = ((EditText)findViewById(R.id.taskupdate_taskname));
        mTaskNameText.setText(mTask.getName());
        mTaskDateText = ((DateEditText) findViewById(R.id.taskupdate_taskdate));
        findViewById(R.id.taskupdate_save_btn).setOnClickListener(getSaveButtonClickHandler());
        findViewById(R.id.taskupdate_cancel_btn).setOnClickListener(getCancelButtonClickHandler());
    }

    /**
     * Initialize the controls that set task recurrence
     */
    private void initializeRecControls(){
        // recurrence mode controls
        mRecModeSpinner = (Spinner) findViewById(R.id.taskupdate_recspinner);
        mRecModeSpinnerAdapter = new ArrayAdapter<>(AppContext.getCurrent().getActivity(), R.layout.control_recspinneritem);
        mRecModeSpinner.setAdapter(mRecModeSpinnerAdapter);
        mRecModeSpinnerAdapter.add(RecurrenceMode.None);
        mRecModeSpinnerAdapter.add(RecurrenceMode.Daily);
        mRecModeSpinnerAdapter.add(RecurrenceMode.Weekly);
        mRecModeSpinnerAdapter.add(RecurrenceMode.MonthlyDateBased);
        mRecModeSpinnerAdapter.add(RecurrenceMode.MonthlyWeekBased);
        mRecModeSpinnerAdapter.add(RecurrenceMode.Yearly);
        mRecModeSpinner.setOnItemSelectedListener(getRecModeSpinnerSelectionHandler());

        // recurrence end controls
        mRecEndSpinner = (Spinner) findViewById(R.id.taskupdate_recend_spinner);
        ArrayAdapter<String> recEndSpinnerAdapter = new ArrayAdapter<>(AppContext.getCurrent().getActivity(), R.layout.control_recspinneritem);
        mRecEndSpinner.setAdapter(recEndSpinnerAdapter);
        recEndSpinnerAdapter.add(AppContext.getCurrent().getActivity().getString(R.string.taskrecurrence_end_forever));
        recEndSpinnerAdapter.add(AppContext.getCurrent().getActivity().getString(R.string.taskrecurrence_end_date));
        mRecEndSpinner.setOnItemSelectedListener(getRecEndSpinnerSelectionHandler());
        mRecEndText = (DateEditText)findViewById(R.id.taskupdate_recend_date);

        // recurrence days interval
        mRecDaysPicker = (DaysPicker)findViewById(R.id.taskupdate_recdays_picker);
        mRecIntervalText = (EditText)findViewById(R.id.taskupdate_recinterval_text);

        // init control values
        TaskRecurrence rec = mTask.getRecurrence();
        if(rec != null){
            mRecModeSpinner.setSelection(mRecModeSpinnerAdapter.getPosition(rec.getMode()));
        }
    }

    /**
     * Get click handler for the save button
     * @return click handler
     */
    private View.OnClickListener getSaveButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[TaskUpdateView.getSaveButtonClickHandler] saving");

                // get the changes
                mTask.setDate(mTaskDateText.getValue());
                mTask.setName(mTaskNameText.getText().toString());
                int selectedRecModePos = mRecModeSpinner.getSelectedItemPosition();
                if(selectedRecModePos == 0){
                    mTask.setRecurrence(null);
                }else{
                    RecurrenceMode recMode = mRecModeSpinnerAdapter.getItem(selectedRecModePos);
                    HashSet<Integer> recDays = mRecDaysPicker.getValue();
                    int recInterval = Integer.parseInt(mRecIntervalText.getText().toString());
                    TaskDate recEnd = mRecEndSpinner.getSelectedItemPosition() == 0 ? null : mRecEndText.getValue();
                    TaskRecurrence rec = new TaskRecurrence(mTask.getDate(), recMode, recDays, recInterval, recEnd);
                    mTask.setRecurrence(rec);
                }

                // go back to main activity and reload
                AppContext.getCurrent().getActivity().onBackPressed();
                AppContext.getCurrent().getActivity().showLoadingScreen();
                ApiTaskMethod method = mTask.getId() == null ? ApiTaskMethod.POST : ApiTaskMethod.PUT;
                String url = mTask.getId() == null ? "/data/task/public" : "/data/task/private/" + mTask.getId();
                AppContext.getCurrent().getDataService().request(method, url, mTask.getDataMap(), new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        HashMap<String, String> navParams = new HashMap<>();
                        navParams.put("date", mTask.getDateKey());
                        AppContext.getCurrent().getNavigationService().navigate(TasksView.class, navParams);
                    }
                });
            }
        };
    }

    /**
     * Get click handler for the cancel button
     * @return click handler
     */
    private View.OnClickListener getCancelButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[TaskUpdateView.getCancelButtonClickHandler] cancelling");
                AppContext.getCurrent().getActivity().onBackPressed();
            }
        };
    }

    /**
     * Get selection handler for the recurrence mode spinner control
     * @return selection handler
     */
    private AdapterView.OnItemSelectedListener getRecModeSpinnerSelectionHandler(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RecurrenceMode mode = mRecModeSpinnerAdapter.getItem(position);
                System.out.println("[TaskUpdateView.getRecSpinnerSelectionHandler] new mode = " + mode);
                View recBox = findViewById(R.id.taskupdate_recurrence_box);
                View recDaysRow = findViewById(R.id.taskupdate_recdays_row);

                // fast return for null recurrence
                if(mode == RecurrenceMode.None) {
                    recBox.setVisibility(View.GONE);
                    recDaysRow.setVisibility(View.GONE);
                    return;
                }

                // set visibility
                recBox.setVisibility(View.VISIBLE);
                if(mode == RecurrenceMode.Weekly){
                    recDaysRow.setVisibility(View.VISIBLE);
                }else{
                    recDaysRow.setVisibility(View.GONE);
                }

                // update interval suffix text
                TextView recIntervalSuffix = (TextView)findViewById(R.id.taskupdate_recinterval_suffix);
                if(mode == RecurrenceMode.Daily){
                    recIntervalSuffix.setText(R.string.taskrecurrence_interval_suffix_days);
                }else if(mode == RecurrenceMode.Weekly){
                    recIntervalSuffix.setText(R.string.taskrecurrence_interval_suffix_weeks);
                }else if(mode == RecurrenceMode.MonthlyWeekBased || mode == RecurrenceMode.MonthlyDateBased){
                    recIntervalSuffix.setText(R.string.taskrecurrence_interval_suffix_months);
                }else if(mode == RecurrenceMode.Yearly){
                    recIntervalSuffix.setText(R.string.taskrecurrence_interval_suffix_years);
                }else{
                    recIntervalSuffix.setText("");
                }

                // reset control values
                TaskRecurrence rec = mTask.getRecurrence();
                if(rec == null) {
                    // set to default values
                    ((Spinner) findViewById(R.id.taskupdate_recend_spinner)).setSelection(0);
                    ((TextView) findViewById(R.id.taskupdate_recinterval_text)).setText("1");
                    ((DateEditText) findViewById(R.id.taskupdate_recend_date)).setValue(new TaskDate());
                    HashSet<Integer> defaultDays = new HashSet<>();
                    defaultDays.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
                    ((DaysPicker) findViewById(R.id.taskupdate_recdays_picker)).setValue(defaultDays);
                }else{
                    // set to task values
                    TaskDate recEnd = rec.getEnd();
                    ((Spinner) findViewById(R.id.taskupdate_recend_spinner)).setSelection(recEnd != null ? 1 : 0);
                    ((TextView) findViewById(R.id.taskupdate_recinterval_text)).setText(HelperService.toString(mTask.getRecurrence().getInterval()));
                    ((DateEditText) findViewById(R.id.taskupdate_recend_date)).setValue(recEnd != null ? recEnd : new TaskDate());
                    ((DaysPicker) findViewById(R.id.taskupdate_recdays_picker)).setValue(mTask.getRecurrence().getDays());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    /**
     * Get selection handler for the recurrence end spinner control
     * @return selection handler
     */
    private AdapterView.OnItemSelectedListener getRecEndSpinnerSelectionHandler(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    // until forever
                    mRecEndText.setVisibility(View.GONE);
                }else {
                    // until date
                    mRecEndText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    /**
     * Update the view based on the change in the model
     */
    private void updateView(){
        mTaskDateText.setText(mTask.getDate().getFriendlyString());
    }
}
