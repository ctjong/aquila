package com.projectaquila.controls;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.models.Task;
import com.projectaquila.models.TaskDate;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Adapter for populating tasks on the tasks view
 */
public class TasksAdapter extends ArrayAdapter<TaskControl>{
    private static final int ITEMS_PER_DATE = 100;
    private static final int CACHE_DAYS_SPAN = 3;
    private HashMap<String, List<TaskControl>> mControlsMap;
    private TaskDate mActiveDate;

    /**
     * Instantiate a new tasks adapter
     */
    public TasksAdapter(){
        super(AppContext.getCurrent().getActivity(), R.layout.control_taskcontrol);
        mControlsMap = new HashMap<>();
    }

    /**
     * Load data for the given date
     * @param date date object
     * @param refreshCache true to refresh in-memory cache
     */
    public void loadDate(TaskDate date, boolean refreshCache){
        mActiveDate = date;
        String key = date.toDateKey();
        if(mControlsMap.containsKey(key) && !refreshCache){
            clear();
            List<TaskControl> activeControls = mControlsMap.get(key);
            addAll(activeControls);
            notifyDataSetChanged();
        }else{
            retrieveFromServer();
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
            System.err.println("[TasksAdapter.getView] failed to get task data at position " + position);
            return new TextView(getContext());
        }
        taskControl.getTask().addChangedHandler(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                updateControlsMap();
            }
        });
        taskControl.renderView(convertView);
        return convertView;
    }

    /**
     * Retrieve data for the active date from API
     */
    private void retrieveFromServer(){
        AppContext.getCurrent().getActivity().showLoadingScreen();

        // get data URL
        String dataUrl = getDataUrlForActiveDate();
        if(dataUrl == null) return;

        // request data
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.GET, dataUrl, null, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                // check for errors
                if(s == S.Error) return;
                JSONArray tasks = (JSONArray)params.get("value");
                if(tasks == null) {
                    System.err.println("[TasksView.loadTasks] null tasks returned");
                    return;
                }

                // update tasks list
                addToTasksModel(tasks);
                initNearbyDateKeys();
                updateControlsMap();
                AppContext.getCurrent().getActivity().showContentScreen();
            }
        });
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
    private void initNearbyDateKeys(){
        for(int i=-1*CACHE_DAYS_SPAN; i<=CACHE_DAYS_SPAN; i++){
            String key = mActiveDate.getModifiedKey(i);
            mControlsMap.put(key, null);
        }
    }

    /**
     * Update the controls map based on the tasks model
     */
    private void updateControlsMap(){
        clear();
        for (String key : mControlsMap.keySet()) {
            mControlsMap.put(key, new LinkedList<TaskControl>());
        }
        String activeKey = mActiveDate.toDateKey();
        for(Map.Entry<String,Task> entry : AppContext.getCurrent().getTasks().entrySet()){
            Task task = entry.getValue();
            if(task.isCompleted()) continue;
            TaskControl control = new TaskControl(task);
            String key = task.getDate().toDateKey();
            if(!mControlsMap.containsKey(key)) continue;
            mControlsMap.get(key).add(control);
            if(key.equals(activeKey)) add(control);
        }
        notifyDataSetChanged();
    }

    /**
     * Get data URL for the active date. This will retrieve data for date +/- CACHE_DAYS_SPAN days.
     * @return URL string
     */
    private String getDataUrlForActiveDate(){
        try {
            String startDate = mActiveDate.getModifiedKey(-1 * CACHE_DAYS_SPAN);
            String endDate = mActiveDate.getModifiedKey(CACHE_DAYS_SPAN);
            String condition = URLEncoder.encode("taskdate>=" + startDate + "&taskdate<=" + endDate, "UTF-8");
            return "/data/task/private/findbyconditions/id/0/" + ITEMS_PER_DATE + "/" + condition;
        } catch (UnsupportedEncodingException e) {
            System.err.println("[TasksView.getDataUrlForCurrentDate] exception");
            e.printStackTrace();
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
            return null;
        }
    }
}
