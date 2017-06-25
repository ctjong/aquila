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

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Adapter for populating tasks on the tasks view
 */
public class TasksAdapter extends ArrayAdapter<TaskControl>{
    private static final int ITEMS_PER_DATE = 100;
    private static final int CACHE_DAYS_SPAN = 3;
    private HashMap<String, List<TaskControl>> mData;
    private List<TaskControl> mCurrentDateData;

    /**
     * Instantiate a new tasks adapter
     */
    public TasksAdapter(){
        super(AppContext.getCurrent().getCore(), R.layout.control_tasklistitem);
        mData = new HashMap<>();
    }

    /**
     * Load data for the given date
     * @param date date object
     * @param refreshCache true to refresh in-memory cache
     */
    public void loadDate(Date date, boolean refreshCache){
        String key = getDateKey(date);
        if(mData.containsKey(key) && !refreshCache){
            clear();
            mCurrentDateData = mData.get(key);
            addAll(mCurrentDateData);
            notifyDataSetChanged();
        }else{
            retrieveData(date);
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
        taskControl.addDeleteHandler(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                remove(taskControl);
                mCurrentDateData.remove(taskControl);
                notifyDataSetChanged();
            }
        });
        return taskControl.renderView(view);
    }

    /**
     * Retrieve data for the given date from API
     * @param date date of the data to retrieve
     */
    private void retrieveData(final Date date){
        AppContext.getCurrent().getShell().showLoadingScreen();

        // get data URL
        String dataUrl = getDataUrlForDate(date);
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
                processServerResponse(tasks, date);
                notifyDataSetChanged();
                AppContext.getCurrent().getShell().showContentScreen();
            }
        });
    }

    /**
     * Populate data variables for the given json response from server
     * @param tasks json array of tasks
     * @param date date object
     */
    private void processServerResponse(JSONArray tasks, Date date){
        clear();
        for(int i=-1*CACHE_DAYS_SPAN; i<=CACHE_DAYS_SPAN; i++){
            String key = getDateKey(date, i);
            if(!mData.containsKey(key)){
                List<TaskControl> list = new LinkedList<>();
                mData.put(key, list);
            }
        }
        for(int i=0; i<tasks.length(); i++){
            try {
                Object taskObj = tasks.get(i);
                TaskControl task = TaskControl.parse(taskObj);
                if(task == null){
                    System.err.println("[TasksView.loadTasks] failed to parse task object. skipping.");
                    continue;
                }
                String key = getDateKey(task.getDate());
                mData.get(key).add(task);
                if (key.equals(getDateKey(date))) {
                    add(task);
                }
            } catch (Exception e) {
                System.err.println("[TasksView.loadTasks] an exception occurred. skipping.");
                e.printStackTrace();
            }
        }
        mCurrentDateData = mData.get(date);
    }

    /**
     * Get data URL for the given date. This will retrieve data for date +/- CACHE_DAYS_SPAN days.
     * @param date date object
     * @return URL string
     */
    private String getDataUrlForDate(Date date){
        try {
            String startDate = getDateKey(date, -1 * CACHE_DAYS_SPAN);
            String endDate = getDateKey(date, CACHE_DAYS_SPAN);
            String condition = URLEncoder.encode("taskdate>=" + startDate + "&taskdate<=" + endDate, "UTF-8");
            return "/data/task/private/findbyconditions/id/0/" + ITEMS_PER_DATE + "/" + condition;
        } catch (UnsupportedEncodingException e) {
            System.err.println("[TasksView.getDataUrlForCurrentDate] exception");
            e.printStackTrace();
            AppContext.getCurrent().getShell().showErrorScreen(R.string.shell_error_unknown);
            return null;
        }
    }

    /**
     * Get a string representation of a date
     * @param date date object
     * @return string representation
     */
    private String getDateKey(Date date){
        return new SimpleDateFormat("yyMMdd").format(date);
    }

    /**
     * Get a string representation of a date that is n days away from the given date
     * @param date original date
     * @param numDays number of days to add/substract
     * @return string representation of the modified date
     */
    private String getDateKey(Date date, int numDays){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, numDays);
        return new SimpleDateFormat("yyMMdd").format(c.getTime());
    }
}
