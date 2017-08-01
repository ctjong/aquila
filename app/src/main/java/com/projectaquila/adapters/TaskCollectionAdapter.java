package com.projectaquila.adapters;

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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Adapter for populating tasks on the tasks view
 */
public class TaskCollectionAdapter extends CollectionAdapter<TaskControl>{
    private static final int CACHE_DAYS_SPAN = 3;
    private HashMap<String, List<TaskControl>> mControlsMap;
    private TaskDate mActiveDate;
    private TaskCollection mTasks;

    /**
     * Instantiate a new tasks adapter
     */
    public TaskCollectionAdapter(){
        super(R.layout.control_taskcontrol);
        mTasks = new TaskCollection();
    }

    /**
     * Load data for the given date
     * @param date date object
     * @param refreshCache true to refresh in-memory cache
     */
    public void loadDate(TaskDate date, boolean refreshCache){
        mActiveDate = date;
        String key = date.toDateKey();
        if(refreshCache || mControlsMap == null){
            mControlsMap = new HashMap<>();
            AppContext.getCurrent().getActivity().showLoadingScreen();
            mTasks.load(new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    expandControlsMap();
                    AppContext.getCurrent().getActivity().hideLoadingScreen();
                }
            });
        }else {
            if (!mControlsMap.containsKey(key)) {
                expandControlsMap();
            }
            clear();
            List<TaskControl> activeControls = mControlsMap.get(key);
            addAll(activeControls);
            notifyDataSetChanged();
        }
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
                System.err.println("[TaskCollectionAdapter.getView] failed to get view for task at index" + position);
                return new TextView(getContext());
            }
        }
        final TaskControl taskControl = getItem(position);
        if(taskControl == null){
            System.err.println("[TaskCollectionAdapter.getView] failed to get task control at position " + position);
            return new TextView(getContext());
        }
        final DataModelBase data = taskControl.getData();
        data.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateControlsMap(mActiveDate);
                if(data instanceof Task){
                    String currentKey = mActiveDate.toDateKey();
                    Task task = (Task)data;
                    if(!task.getDateKey().equals(currentKey)) {
                        updateControlsMap(task.getDate());
                    }
                }
            }
        });
        taskControl.renderView(convertView);
        return convertView;
    }

    /**
     * Initialize keys for dates that are near the active date in the controls map
     */
    private void expandControlsMap(){
        for(int i=-1*CACHE_DAYS_SPAN; i<=CACHE_DAYS_SPAN; i++){
            updateControlsMap(mActiveDate.getModified(i));
        }
    }

    /**
     * Update the controls map based on the tasks model for the given date
     */
    private void updateControlsMap(TaskDate mapDate){
        String activeKey = mActiveDate.toDateKey();
        String mapKey = mapDate.toDateKey();
        if(mapKey.equals(activeKey)){
            clear();
        }
        mControlsMap.put(mapKey, new LinkedList<TaskControl>());
        for(Task task : mTasks.getItems()){
            String taskKey = task.getDate().toDateKey();
            if(task.getRecurrence() == null && taskKey.equals(mapKey) && mControlsMap.containsKey(taskKey)) {
                // add regular (non-recurred) tasks to active array
                TaskControl control = new TaskControl(task, task.getDate());
                mControlsMap.get(taskKey).add(control);
                if(taskKey.equals(activeKey)) {
                    add(control);
                }
            } else if(task.getRecurrence() != null && task.getRecurrence().isIncluded(mapDate)){
                // add recurred tasks to active array
                TaskControl control = new TaskControl(task, mapDate);
                mControlsMap.get(mapKey).add(control);
                if(mapKey.equals(activeKey) && task.getRecurrence().isIncluded(mActiveDate)) {
                    add(control);
                }
            }
        }
        // add tasks that are a part of enrolled plans to active array
        for(PlanEnrollment enrollment : AppContext.getCurrent().getEnrollments().getItems()) {
            Plan plan = enrollment.getPlan();
            TaskDate startDate = TaskDate.parseDateKey(enrollment.getEnrollmentStartDate());
            for(PlanTask planTask : plan.getItems()){
                TaskDate planTaskDate = startDate.getModified(planTask.getDay() - 1);
                if(planTaskDate.equals(mapDate)){
                    TaskControl control = new TaskControl(planTask, enrollment);
                    mControlsMap.get(mapKey).add(control);
                    if(mapKey.equals(activeKey)) {
                        add(control);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}
