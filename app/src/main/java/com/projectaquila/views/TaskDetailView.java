package com.projectaquila.views;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.RecurrenceMode;
import com.projectaquila.models.Task;
import com.projectaquila.models.TaskRecurrence;
import com.projectaquila.services.HelperService;


import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class TaskDetailView extends ViewBase {
    @Override
    protected int getLayoutId() {
        return R.layout.view_taskdetail;
    }

    @Override
    protected void initializeView(){
        String taskId = getNavArg("id");
        if(taskId == null || !AppContext.getCurrent().getTasks().containsKey(taskId)) {
            System.err.println("[TaskDetailView.initializeView] invalid task id found in nav params");
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
            return;
        }

        final Task task = AppContext.getCurrent().getTasks().get(taskId);
        ((TextView)findViewById(R.id.taskdetail_taskname)).setText(task.getName());
        ((TextView)findViewById(R.id.taskdetail_taskdate)).setText(task.getDate().getFriendlyString());
        findViewById(R.id.taskdetail_edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> navParams = new HashMap<>();
                navParams.put("id", task.getId());
                AppContext.getCurrent().getNavigationService().navigate(TaskUpdateView.class, navParams);
            }
        });

        // task recurrence
        if(task.getRecurrence() != null){
            findViewById(R.id.taskdetail_recurrence_box).setVisibility(View.VISIBLE);
            TaskRecurrence rec = task.getRecurrence();
            RecurrenceMode mode = rec.getMode();
            ((TextView)findViewById(R.id.taskdetail_recmode)).setText(mode.toString());

            // recurrence days
            if(mode == RecurrenceMode.Weekly){
                findViewById(R.id.taskdetail_recdays_row).setVisibility(View.VISIBLE);
                HashSet<Integer> days = rec.getDays();
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

            // recurrence end
            ((TextView)findViewById(R.id.taskdetail_recend)).setText(rec.getEnd().getFriendlyString());
        }else{
            // no recurrence
            ((TextView)findViewById(R.id.taskdetail_recmode)).setText(RecurrenceMode.None.toString());
            findViewById(R.id.taskdetail_recurrence_box).setVisibility(View.GONE);
        }

        AppContext.getCurrent().getActivity().setToolbarText(R.string.taskdetail_title);
        AppContext.getCurrent().getActivity().showContentScreen();
    }
}
