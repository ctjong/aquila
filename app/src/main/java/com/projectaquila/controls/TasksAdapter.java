package com.projectaquila.controls;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.models.Task;
import com.projectaquila.services.HelperService;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Adapter for populating tasks on the tasks view
 */
public class TasksAdapter extends ArrayAdapter<TaskControl>{
    private static final int ITEMS_PER_DATE = 100;
    private static final int CACHE_DAYS_SPAN = 3;
    private HashMap<String, List<TaskControl>> mControlsMap;
    private List<TaskControl> mAllControls;
    private List<TaskControl> mActiveControls;
    private Date mActiveDate;

    /**
     * Instantiate a new tasks adapter
     */
    public TasksAdapter(){
        super(AppContext.getCurrent().getCore(), R.layout.control_tasklistitem);
        mControlsMap = new HashMap<>();
    }

    /**
     * Load data for the given date
     * @param date date object
     * @param refreshCache true to refresh in-memory cache
     */
    public void loadDate(Date date, boolean refreshCache){
        mActiveDate = date;
        String key = HelperService.getDateKey(date);
        if(mControlsMap.containsKey(key) && !refreshCache){
            clear();
            mActiveControls = mControlsMap.get(key);
            addAll(mActiveControls);
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
    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.control_tasklistitem, null);
        if(view == null){
            System.err.println("[TasksAdapter.getView] failed to get view for task at index" + position);
            return null;
        }
        final TaskControl taskControl = getItem(position);
        if(taskControl == null){
            System.err.println("[TasksAdapter.getView] failed to get task data at position " + position);
            return null;
        }
        taskControl.addCompleteHandler(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                remove(taskControl);
                mActiveControls.remove(taskControl);
            }
        });
        taskControl.addPostponeHandler(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                updateControlsMap();
            }
        });
        return taskControl.renderView(view);
    }

    /**
     * Retrieve data for the active date from API
     */
    private void retrieveFromServer(){
        AppContext.getCurrent().getShell().showLoadingScreen();

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
                updateAllControlsList(tasks);
                initNearbyDateKeys();
                updateControlsMap();
                AppContext.getCurrent().getShell().showContentScreen();
            }
        });
    }

    /**
     * Update the all controls list based on the given data from API
     * @param tasks json array of tasks
     */
    private void updateAllControlsList(JSONArray tasks){
        mAllControls = new LinkedList<>();
        for(int i=0; i<tasks.length(); i++){
            try {
                Object taskObj = tasks.get(i);
                Task task = Task.parse(taskObj);
                if(task == null){
                    System.err.println("[TasksView.loadTasks] failed to parse task object. skipping.");
                    continue;
                }
                TaskControl taskControl = new TaskControl(task);
                mAllControls.add(taskControl);
            } catch (Exception e) {
                System.err.println("[TasksView.loadTasks] an exception occurred. skipping.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialize keys for dates that are near the active date in the controls map
     */
    private void initNearbyDateKeys(){
        for(int i=-1*CACHE_DAYS_SPAN; i<=CACHE_DAYS_SPAN; i++){
            String key = HelperService.getDateKey(mActiveDate, i);
            mControlsMap.put(key, null);
        }
    }

    /**
     * Update the controls map based on the active date
     */
    private void updateControlsMap(){
        clear();
        Iterator it = mControlsMap.keySet().iterator();
        while(it.hasNext()){
            mControlsMap.put((String)it.next(), new LinkedList<TaskControl>());
        }
        String activeKey = HelperService.getDateKey(mActiveDate);
        for(int i=0; i<mAllControls.size(); i++){
            TaskControl control = mAllControls.get(i);
            String key = HelperService.getDateKey(control.getTask().getDate());
            if(!mControlsMap.containsKey(key)) continue;
            mControlsMap.get(key).add(control);
            if(key.equals(activeKey)) add(control);
        }
        mActiveControls = mControlsMap.get(activeKey);
        notifyDataSetChanged();
    }

    /**
     * Get data URL for the active date. This will retrieve data for date +/- CACHE_DAYS_SPAN days.
     * @return URL string
     */
    private String getDataUrlForActiveDate(){
        try {
            String startDate = HelperService.getDateKey(mActiveDate, -1 * CACHE_DAYS_SPAN);
            String endDate = HelperService.getDateKey(mActiveDate, CACHE_DAYS_SPAN);
            String condition = URLEncoder.encode("iscompleted=0&taskdate>=" + startDate + "&taskdate<=" + endDate, "UTF-8");
            return "/data/task/private/findbyconditions/id/0/" + ITEMS_PER_DATE + "/" + condition;
        } catch (UnsupportedEncodingException e) {
            System.err.println("[TasksView.getDataUrlForCurrentDate] exception");
            e.printStackTrace();
            AppContext.getCurrent().getShell().showErrorScreen(R.string.shell_error_unknown);
            return null;
        }
    }
}
