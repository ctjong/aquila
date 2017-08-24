package com.planmaster.dataadapters;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.planmaster.common.Event;
import com.planmaster.contexts.AppContext;
import com.planmaster.R;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.controls.TaskControl;
import com.planmaster.datamodels.DataModelBase;
import com.planmaster.datamodels.Plan;
import com.planmaster.datamodels.PlanEnrollment;
import com.planmaster.datamodels.PlanTask;
import com.planmaster.datamodels.Task;
import com.planmaster.common.TaskDate;
import com.planmaster.datamodels.TaskCollection;
import com.planmaster.services.HelperService;

/**
 * Adapter for populating tasks on the tasks view
 */
public class TaskCollectionAdapter extends CollectionAdapter<TaskControl>{
    private TaskDate mActiveDate;
    private TaskCollection mTasks;
    private Event mChangedEvent;

    /**
     * Instantiate a new tasks adapter
     */
    public TaskCollectionAdapter(TaskDate activeDate){
        super(R.layout.control_taskcontrol);
        mChangedEvent = new Event();
        mActiveDate = activeDate;
        mTasks = AppContext.getCurrent().getTasks();
        mTasks.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateList();
            }
        });
        updateList();
    }

    /**
     * Get view for item at the given position
     * @param position item position
     * @param convertView convert view
     * @param parent parent view
     * @return view object
     */
    @NonNull
    @Override
    public View getView (int position, View convertView, @NonNull ViewGroup parent){
        if(convertView == null || convertView instanceof TextView) {
            convertView = View.inflate(getContext(), R.layout.control_taskcontrol, null);
            if (convertView == null) {
                HelperService.logError("[TaskCollectionAdapter.getView] failed to get view for task at index" + position);
                return new TextView(getContext());
            }
        }
        final TaskControl taskControl = getItem(position);
        if(taskControl == null){
            HelperService.logError("[TaskCollectionAdapter.getView] failed to get task control at position " + position);
            return new TextView(getContext());
        }
        final DataModelBase data = taskControl.getData();
        data.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateList();
            }
        });
        taskControl.renderView(convertView);
        return convertView;
    }

    /**
     * Add changed event handler
     * @param handler event handler
     */
    public void addChangedHandler(Callback handler){
        mChangedEvent.addHandler(handler);
    }

    /**
     * Update task list on the tasks in the app context
     */
    private void updateList(){
        clear();
        for(Task task : mTasks.getItems()){
            if(task.getRecurrence() == null && task.getDate().equals(mActiveDate)) {
                // add regular (non-recurred) tasks to active array
                add(new TaskControl(task, task.getDate()));
            } else if(task.getRecurrence() != null && task.getRecurrence().isIncluded(mActiveDate)){
                // add recurred tasks to active array
                add(new TaskControl(task, mActiveDate));
            }
        }
        // add tasks that are a part of enrolled plans to active array
        for(PlanEnrollment enrollment : AppContext.getCurrent().getEnrollments().getItems()) {
            Plan plan = enrollment.getPlan();
            TaskDate startDate = enrollment.getStartDate();
            for(PlanTask planTask : plan.getItems()){
                if(planTask.getDay() <= enrollment.getCompletedDays()) continue;
                TaskDate planTaskDate = startDate.getModified(planTask.getDay() - 1);
                if(planTaskDate.equals(mActiveDate)){
                    add(new TaskControl(planTask, enrollment));
                }
            }
        }
        mChangedEvent.invoke(null);
        notifyDataSetChanged();
    }
}
