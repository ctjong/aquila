package com.projectaquila.datamodels;

import com.projectaquila.common.TaskDate;
import com.projectaquila.services.HelperService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A single plan enrollment information
 */
public class PlanEnrollment extends DataModelBase {
    private Plan mPlan;
    private String mStartDate;
    private int mCompletedDays;
    private TaskDate mCreatedTime;

    /**
     * Construct a new plan enrollment
     */
    public PlanEnrollment(String id, Plan plan, String startDate, int completedDays, TaskDate createdTime){
        super(id);
        mPlan = plan;
        mStartDate = startDate;
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
            int version = json.getInt("version");
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
    public String getEnrollmentStartDate(){
        return mStartDate;
    }

    /**
     * Get the number of completed days
     * @return number of completed days
     */
    public int getCompletedDays(){
        return mCompletedDays;
    }

    @Override
    protected HashMap<String, String> getCreateDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("planid", mPlan.getId());
        data.put("startdate", mStartDate);
        return data;
    }

    @Override
    protected HashMap<String, String> getUpdateDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("startdate", mStartDate);
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
