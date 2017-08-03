package com.projectaquila.datamodels;

import com.projectaquila.services.HelperService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A single task in a plan
 */
public class PlanTask extends DataModelBase {
    private Plan mParent;
    private int mState;
    private int mDay;
    private String mName;
    private String mDescription;

    /**
     * Construct a new plan task
     * @param parent parent plan
     * @param id plan task id
     * @param state state of this task
     * @param day plan task day in the plan
     * @param name plan task name
     * @param description plan task description
     */
    public PlanTask(Plan parent, String id, int state, int day, String name, String description){
        super(id);
        mParent = parent;
        mState = state;
        mDay = day;
        mName = name;
        mDescription = description;
    }

    /**
     * Parse the given object and try to create a plan task object
     * @param parent parent plan
     * @param object input object
     * @return plan task object, or null on failure
     */
    public static PlanTask parse(Plan parent, Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            int state = json.getInt("state");
            int day = json.getInt("day");
            String name = json.getString("name");
            String description = json.getString("description");
            if(id == null || name == null){
                System.err.println("[PlanTask.parse] failed to parse plan task");
                return null;
            }
            return new PlanTask(parent, id, state, day, name, description);
        }catch(JSONException e){
            System.err.println("[PlanTask.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the parent plan of this item
     * @return parent plan
     */
    public Plan getParent(){
        return mParent;
    }

    /**
     * Get the plan task state
     * @return plan task state
     */
    public int getState(){
        return mState;
    }

    /**
     * Get the plan task day
     * @return plan task day
     */
    public int getDay(){
        return mDay;
    }

    /**
     * Get the plan task name
     * @return plan task name
     */
    public String getName(){
        return mName;
    }

    /**
     * Get the plan task description
     * @return plan task description
     */
    public String getDescription(){
        return mDescription;
    }

    /**
     * Set the day of this plan task
     * @param day new day
     */
    public void setDay(int day){
        mDay = day;
    }

    /**
     * Set the name of this plan task
     * @param name new name
     */
    public void setName(String name){
        mName = name;
    }

    /**
     * Set the description of this plan task
     * @param description new description
     */
    public void setDescription(String description){
        mDescription = description;
    }

    @Override
    protected HashMap<String, String> getCreateDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("planid", mParent.getId());
        data.put("state", HelperService.toString(mState));
        data.put("day", HelperService.toString(mDay));
        data.put("name", mName);
        data.put("description", mDescription);
        return data;
    }

    @Override
    protected HashMap<String, String> getUpdateDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("state", HelperService.toString(mState));
        data.put("day", HelperService.toString(mDay));
        data.put("name", mName);
        data.put("description", mDescription);
        return data;
    }

    @Override
    protected String getCreateUrl() {
        return "/data/plantask";
    }

    @Override
    protected String getUpdateUrl() {
        return "/data/plantask/" + getId();
    }

    @Override
    protected String getDeleteUrl() {
        return "/data/plantask/" + getId();
    }
}
