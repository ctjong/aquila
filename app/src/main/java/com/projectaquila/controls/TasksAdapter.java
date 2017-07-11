package com.projectaquila.controls;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.Callback;
import com.projectaquila.models.CallbackParams;
import com.projectaquila.models.Task;
import com.projectaquila.models.TaskDate;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Adapter for populating tasks on the tasks view
 */
public class TasksAdapter extends ArrayAdapter<TaskControl>{
    private static final int CACHE_DAYS_SPAN = 3;
    private HashMap<String, List<TaskControl>> mControlsMap;
    private TaskDate mActiveDate;

    /**
     * Instantiate a new tasks adapter
     */
    public TasksAdapter(){
        super(AppContext.getCurrent().getActivity(), R.layout.control_taskcontrol);
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
            AppContext.getCurrent().getTasks().clear();
            AppContext.getCurrent().getDataService().requestAll("/data/task/private/findall/id/%d/%d", new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    List result = (List)params.get("result");
                    for(Object tasks : result){
                        addToTasksModel((JSONArray)tasks);
                    }
                    expandControlsMap();
                    AppContext.getCurrent().getActivity().showContentScreen();
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
                System.err.println("[TasksAdapter.getView] failed to get view for task at index" + position);
                return new TextView(getContext());
            }
        }
        final TaskControl taskControl = getItem(position);
        if(taskControl == null){
            System.err.println("[TasksAdapter.getView] failed to get task control at position " + position);
            return new TextView(getContext());
        }
        final String taskKey = taskControl.getTask().getDateKey();
        taskControl.getTask().addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateControlsMap(mActiveDate);
                String currentKey = mActiveDate.toDateKey();
                if(!taskKey.equals(currentKey)){
                    updateControlsMap(taskControl.getTask().getDate());
                }
            }
        });
        taskControl.renderView(convertView);
        return convertView;
    }

    /**
     * Add the specified tasks to the tasks model in app context
     * @param tasks new tasks
     */
    private void addToTasksModel(JSONArray tasks){
        for(int i=0; i<tasks.length(); i++){
            try {
                Object taskObj = tasks.get(i);
                Task task = Task.parse(taskObj);
                if(task == null){
                    System.err.println("[TasksView.addToTasksModel] failed to parse task object. skipping.");
                    continue;
                }
                AppContext.getCurrent().getTasks().put(task.getId(), task);
            } catch (Exception e) {
                System.err.println("[TasksView.addToTasksModel] an exception occurred. skipping.");
                e.printStackTrace();
            }
        }
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
        for(Map.Entry<String,Task> entry : AppContext.getCurrent().getTasks().entrySet()){
            Task task = entry.getValue();
            String taskKey = task.getDate().toDateKey();
            if(task.getRecurrence() == null && taskKey.equals(mapKey) && mControlsMap.containsKey(taskKey)) {
                TaskControl control = new TaskControl(task, task.getDate());
                mControlsMap.get(taskKey).add(control);
                if(taskKey.equals(activeKey)) {
                    add(control);
                }
            } else if(task.getRecurrence() != null && task.getRecurrence().isIncluded(mapDate)){
                TaskControl control = new TaskControl(task, mapDate);
                mControlsMap.get(mapKey).add(control);
                if(mapKey.equals(activeKey) && task.getRecurrence().isIncluded(mActiveDate)) {
                    add(control);
                }
            }

        }
        notifyDataSetChanged();
    }
}
