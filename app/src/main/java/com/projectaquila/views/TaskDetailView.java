package com.projectaquila.views;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.RecurrenceMode;
import com.projectaquila.datamodels.Task;
import com.projectaquila.common.TaskDate;
import com.projectaquila.common.TaskRecurrence;
import com.projectaquila.services.HelperService;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TaskDetailView extends ViewBase {
    @Override
    protected int getLayoutId() {
        return R.layout.view_taskdetail;
    }

    @Override
    protected int getTitleBarStringId(){
        return R.string.taskdetail_title;
    }

    @Override
    protected void initializeView(){
        final Task task = (Task)getNavArgObj("task");
        final String activeDateKey = getNavArgStr("activedatekey");

        // task details
        final TaskDate activeDate = TaskDate.parseDateKey(activeDateKey);
        ((TextView)findViewById(R.id.taskdetail_taskname)).setText(task.getName());
        ((TextView)findViewById(R.id.taskdetail_taskdate)).setText(activeDate.getFriendlyString());

        // task recurrence
        if(task.getRecurrence() != null){
            findViewById(R.id.taskdetail_recurrence_box).setVisibility(View.VISIBLE);
            TaskRecurrence rec = task.getRecurrence();
            RecurrenceMode mode = rec.getMode();
            ((TextView)findViewById(R.id.taskdetail_recmode)).setText(mode.toString());

            // recurrence days
            if(mode == RecurrenceMode.Weekly){
                findViewById(R.id.taskdetail_recdays_row).setVisibility(View.VISIBLE);
                List<Integer> days = rec.getDays();
                String daysStr = "";
                Context ctx = AppContext.getCurrent().getActivity();
                for(int day : days){
                    if(day == Calendar.SUNDAY){
                        daysStr += (daysStr.equals("") ? "" : ", ") + ctx.getString(R.string.taskrecurrence_days_sunday);
                    }else if (day == Calendar.MONDAY){
                        daysStr += (daysStr.equals("") ? "" : ", ") + ctx.getString(R.string.taskrecurrence_days_monday);
                    }else if (day == Calendar.TUESDAY){
                        daysStr += (daysStr.equals("") ? "" : ", ") + ctx.getString(R.string.taskrecurrence_days_tuesday);
                    }else if (day == Calendar.WEDNESDAY){
                        daysStr += (daysStr.equals("") ? "" : ", ") + ctx.getString(R.string.taskrecurrence_days_wednesday);
                    }else if (day == Calendar.THURSDAY){
                        daysStr += (daysStr.equals("") ? "" : ", ") + ctx.getString(R.string.taskrecurrence_days_thursday);
                    }else if (day == Calendar.FRIDAY){
                        daysStr += (daysStr.equals("") ? "" : ", ") + ctx.getString(R.string.taskrecurrence_days_friday);
                    }else if (day == Calendar.SATURDAY) {
                        daysStr += (daysStr.equals("") ? "" : ", ") + ctx.getString(R.string.taskrecurrence_days_saturday);
                    }
                }
                ((TextView)findViewById(R.id.taskdetail_recdays)).setText(daysStr);
            }else{
                findViewById(R.id.taskdetail_recdays_row).setVisibility(View.GONE);
            }

            // recurrence interval
            ((TextView)findViewById(R.id.taskdetail_recinterval)).setText(HelperService.toString(rec.getInterval()));
            if(mode == RecurrenceMode.Daily){
                ((TextView)findViewById(R.id.taskdetail_recinterval_suffix)).setText(R.string.taskrecurrence_interval_suffix_days);
            }else if(mode == RecurrenceMode.Weekly){
                ((TextView)findViewById(R.id.taskdetail_recinterval_suffix)).setText(R.string.taskrecurrence_interval_suffix_weeks);
            }else if(mode == RecurrenceMode.MonthlyDateBased || mode == RecurrenceMode.MonthlyWeekBased){
                ((TextView)findViewById(R.id.taskdetail_recinterval_suffix)).setText(R.string.taskrecurrence_interval_suffix_months);
            }else if(mode == RecurrenceMode.Yearly){
                ((TextView)findViewById(R.id.taskdetail_recinterval_suffix)).setText(R.string.taskrecurrence_interval_suffix_years);
            }

            // recurrence start
            ((TextView)findViewById(R.id.taskdetail_recstart)).setText(task.getDate().getFriendlyString());

            // recurrence end
            ((TextView)findViewById(R.id.taskdetail_recend)).setText(rec.getEnd().getFriendlyString());
        }else{
            // no recurrence
            ((TextView)findViewById(R.id.taskdetail_recmode)).setText(RecurrenceMode.None.toString());
            findViewById(R.id.taskdetail_recurrence_box).setVisibility(View.GONE);
        }

        // buttons
        findViewById(R.id.taskdetail_edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> navParams = new HashMap<>();
                navParams.put("task", task);
                navParams.put("activedatekey", activeDateKey);
                AppContext.getCurrent().getNavigationService().navigateChild(TaskUpdateView.class, navParams);
            }
        });
        Button completeBtn = (Button)findViewById(R.id.taskdetail_complete_btn);
        Button completeOccBtn = (Button)findViewById(R.id.taskdetail_completeocc_btn);
        if(task.getRecurrence() == null){
            completeBtn.setText(R.string.taskdetail_complete_btn_text);
            completeOccBtn.setVisibility(View.GONE);
        }else{
            completeBtn.setText(R.string.taskdetail_completeser_btn_text);
            completeOccBtn.setVisibility(View.VISIBLE);
        }
        completeBtn.setOnClickListener(getCompleteTaskAction(task, null, activeDateKey));
        completeOccBtn.setOnClickListener(getCompleteTaskAction(task, activeDate, activeDateKey));
    }

    private View.OnClickListener getCompleteTaskAction(final Task task, final TaskDate occurrenceDate, final String activeDateKey){
        final Callback cb = new Callback() {
            @Override
            public void execute(CallbackParams params) {
                AppContext.getCurrent().getNavigationService().navigate(TaskCollectionView.class, HelperService.getSinglePairMap("date", activeDateKey));
            }
        };
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(occurrenceDate == null){
                    HelperService.showAlert(R.string.prompt_completetask_series_title, R.string.prompt_completetask_series_msg, new Callback() {
                        @Override
                        public void execute(CallbackParams params) {
                            AppContext.getCurrent().getNavigationService().goToMainActivity();
                            AppContext.getCurrent().getActivity().showLoadingScreen();
                            task.complete(cb);
                        }
                    }, null);
                }else{
                    HelperService.showAlert(R.string.prompt_completetask_title, R.string.prompt_completetask_msg, new Callback() {
                        @Override
                        public void execute(CallbackParams params) {
                            AppContext.getCurrent().getNavigationService().goToMainActivity();
                            AppContext.getCurrent().getActivity().showLoadingScreen();
                            task.completeOccurrence(occurrenceDate, cb);
                        }
                    }, null);
                }
            }
        };
    }
}
