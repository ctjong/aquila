package com.projectaquila.dataadapters;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.controls.TaskControl;
import com.projectaquila.datamodels.DataModelBase;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.datamodels.PlanEnrollment;
import com.projectaquila.datamodels.PlanTask;
import com.projectaquila.datamodels.Task;
import com.projectaquila.common.TaskDate;
import com.projectaquila.datamodels.TaskCollection;
import com.projectaquila.services.HelperService;

/**
 * Adapter for populating tasks on the tasks view
 */
public class TaskCollectionAdapter extends CollectionAdapter<TaskControl>{
    private TaskDate mActiveDate;
    private TaskCollection mTasks;

    /**
     * Instantiate a new tasks adapter
     */
    public TaskCollectionAdapter(TaskDate activeDate){
        super(R.layout.control_taskcontrol);
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
        notifyDataSetChanged();
    }
}
