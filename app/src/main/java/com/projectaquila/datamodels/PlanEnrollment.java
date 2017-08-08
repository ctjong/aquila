package com.projectaquila.datamodels;

import com.projectaquila.common.Callback;
import com.projectaquila.common.TaskDate;
import com.projectaquila.services.HelperService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A single plan enrollment information
 */
public class PlanEnrollment extends DataModelBase {
    private Plan mPlan;
    private TaskDate mStartDate;
    private int mCompletedDays;
    private TaskDate mCreatedTime;

    /**
     * Construct a new plan enrollment
     */
    public PlanEnrollment(String id, Plan plan, String startDate, int completedDays, TaskDate createdTime){
        super(id);
        mPlan = plan;
        mStartDate = TaskDate.parseDateKey(startDate);
        mCompletedDays = completedDays;
        mCreatedTime = createdTime;
    }

    /**
     * Parse the given object and try to create a plan task object
     * @param object input object
     * @return plan task object, or null on failure
     */
    public static PlanEnrollment parse(Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            JSONObject obj = json.getJSONObject("plan");
            Plan plan = Plan.parse(obj);
            String startDate = json.getString("startdate");
            int completedDays = json.getInt("completeddays");
            TaskDate createdTime = new TaskDate(json.getLong("createdtime"));
            if(id == null || plan == null){
                System.err.println("[PlanEnrollment.parse] failed to parse plan enrollment");
                return null;
            }
            return new PlanEnrollment(id, plan, startDate, completedDays, createdTime);
        }catch(JSONException e){
            System.err.println("[PlanTask.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the enrolled plan
     * @return enrolled plan
     */
    public Plan getPlan(){
        return mPlan;
    }

    /**
     * Get the enrollment start date
     * @return enrollment start date
     */
    public TaskDate getStartDate(){
        return mStartDate;
    }

    /**
     * Get the number of completed days
     * @return number of completed days
     */
    public int getCompletedDays(){
        return mCompletedDays;
    }

    /**
     * Set the number of completed days
     * @param completedDays number of completed days
     */
    public void setCompletedDays(int completedDays){
        mCompletedDays = completedDays;
    }

    /**
     * Set the start date of this enrollment
     * @param startDate start date
     */
    public void setStartDate(TaskDate startDate){
        mStartDate = startDate;
    }

    @Override
    protected List<DataModelBase> getNestedItems(){
        List<DataModelBase> items = new ArrayList<>();
        items.add(mPlan);
        return items;
    }

    @Override
    protected HashMap<String, String> getCreateDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("planid", mPlan.getId());
        data.put("startdate", mStartDate.toDateKey());
        return data;
    }

    @Override
    protected HashMap<String, String> getUpdateDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("startdate", mStartDate.toDateKey());
        data.put("completeddays", HelperService.toString(mCompletedDays));
        return data;
    }

    @Override
    protected String getCreateUrl() {
        return "/data/planenrollment";
    }

    @Override
    protected String getUpdateUrl() {
        return "/data/planenrollment/" + getId();
    }

    @Override
    protected String getDeleteUrl() {
        return "/data/planenrollment/" + getId();
    }
}
