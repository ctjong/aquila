package com.projectaquila.views;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.controls.DateEditText;
import com.projectaquila.controls.DaysPicker;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.RecurrenceMode;
import com.projectaquila.datamodels.Task;
import com.projectaquila.common.TaskDate;
import com.projectaquila.common.TaskRecurrence;
import com.projectaquila.services.HelperService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class TaskUpdateView extends ViewBase {
    private Task mTask;
    private TaskDate mActiveDate;
    private EditText mTaskNameText;
    private DateEditText mTaskDateText;

    private Spinner mRecModeSpinner;
    private ArrayAdapter<RecurrenceMode> mRecModeSpinnerAdapter;
    private DaysPicker mRecDaysPicker;
    private EditText mRecIntervalText;
    private DateEditText mRecStartText;
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
     * Get title bar string id
     * @return title bar string id
     */
    @Override
    protected int getTitleBarStringId(){
        if(getNavArgStr("id") != null){
            return R.string.taskupdate_title;
        }
        return R.string.taskcreate_title;
    }

    /**
     * Initialize the view
     */
    @Override
    protected void initializeView(){
        initializeTaskModel();
        initializeCommonControls();
        initializeRecControls();
        AppContext.getCurrent().getActivity().showContentScreen();
    }

    /**
     * Initialize task model based on whether we are on update/create mode
     */
    private void initializeTaskModel(){
        mTask = (Task)getNavArgObj("task");
        if(mTask.getId() != null){
            System.out.println("[TaskUpdateView.initializeView] mode=update, id=" + mTask.getId());
            mActiveDate = TaskDate.parseDateKey(getNavArgStr("activedatekey"));
        }else{
            System.out.println("[TaskUpdateView.initializeView] mode=create");
            mActiveDate = TaskDate.parseDateKey(getNavArgStr("date"));
        }
    }

    /**
     * Initialize common controls
     */
    private void initializeCommonControls(){
        mTaskNameText = ((EditText)findViewById(R.id.taskupdate_taskname));
        mTaskNameText.setText(mTask.getName());
        mTaskDateText = ((DateEditText) findViewById(R.id.taskupdate_taskdate));
        mTaskDateText.setValue(mActiveDate);
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

        // other recurrence controls
        mRecDaysPicker = (DaysPicker)findViewById(R.id.taskupdate_recdays_picker);
        mRecIntervalText = (EditText)findViewById(R.id.taskupdate_recinterval_text);
        mRecStartText = (DateEditText) findViewById(R.id.taskupdate_recstart_date);

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
                mTask.setName(mTaskNameText.getText().toString());
                int selectedRecModePos = mRecModeSpinner.getSelectedItemPosition();
                if(selectedRecModePos == 0){
                    mTask.setDate(mTaskDateText.getValue());
                    mTask.setRecurrence(null);
                }else{
                    mTask.setDate(mRecStartText.getValue());
                    RecurrenceMode recMode = mRecModeSpinnerAdapter.getItem(selectedRecModePos);
                    HashSet<Integer> recDays = mRecDaysPicker.getValue();
                    int recInterval = Integer.parseInt(mRecIntervalText.getText().toString());
                    TaskDate recEnd = mRecEndSpinner.getSelectedItemPosition() == 0 ? null : mRecEndText.getValue();
                    TaskRecurrence rec = new TaskRecurrence(mTask, recMode, recDays, recInterval, recEnd);
                    mTask.setRecurrence(rec);
                }

                // go back to main activity and reload
                AppContext.getCurrent().getNavigationService().goToMainActivity();
                AppContext.getCurrent().getActivity().showLoadingScreen();
                mTask.submitUpdate(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        String returnDateKey = mTask.getRecurrence() != null ? mActiveDate.toDateKey() : mTask.getDateKey();
                        AppContext.getCurrent().getNavigationService().navigate(TaskCollectionView.class, HelperService.getSinglePairMap("date", returnDateKey));
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
                AppContext.getCurrent().getNavigationService().goBack();
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
                System.out.println("[TaskUpdateView.getRecSpinnerSelectionHandler] recurrence mode = " + mode);
                View recBox = findViewById(R.id.taskupdate_recurrence_box);
                View recDaysRow = findViewById(R.id.taskupdate_recdays_row);

                // fast return for null recurrence
                if(mode == RecurrenceMode.None) {
                    recBox.setVisibility(View.GONE);
                    recDaysRow.setVisibility(View.GONE);
                    mTaskDateText.enable();
                    return;
                }

                // set visibility
                recBox.setVisibility(View.VISIBLE);
                if(mode == RecurrenceMode.Weekly){
                    recDaysRow.setVisibility(View.VISIBLE);
                }else{
                    recDaysRow.setVisibility(View.GONE);
                }
                mTaskDateText.disable();

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
                    mRecEndSpinner.setSelection(0);
                    mRecIntervalText.setText("1");
                    mRecStartText.setValue(mTask.getDate());
                    mRecEndText.setValue(new TaskDate());
                    List<Integer> defaultDays = new ArrayList<>();
                    defaultDays.add(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
                    mRecDaysPicker.setValue(defaultDays);
                }else{
                    // set to task values
                    TaskDate recEnd = rec.getEnd();
                    mRecEndSpinner.setSelection(recEnd != null ? 1 : 0);
                    mRecIntervalText.setText(HelperService.toString(mTask.getRecurrence().getInterval()));
                    mRecStartText.setValue(mTask.getDate());
                    mRecEndText.setValue(recEnd != null ? recEnd : mActiveDate);
                    mRecDaysPicker.setValue(mTask.getRecurrence().getDays());
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
}
