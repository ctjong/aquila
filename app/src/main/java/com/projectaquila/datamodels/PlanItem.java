package com.projectaquila.datamodels;

import com.projectaquila.services.HelperService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A single task in a plan
 */
public class PlanItem extends DataModelBase {
    private Plan mParent;
    private int mDay;
    private String mName;
    private String mDescription;

    /**
     * Construct a new plan item
     * @param parent parent plan
     * @param id plan item id
     * @param day plan item day in the plan
     * @param name plan item name
     * @param description plan item description
     */
    public PlanItem(Plan parent, String id, int day, String name, String description){
        super(id);
        mParent = parent;
        mDay = day;
        mName = name;
        mDescription = description;
    }

    /**
     * Parse the given object and try to create a plan item object
     * @param parent parent plan
     * @param object input object
     * @return plan item object, or null on failure
     */
    public static PlanItem parse(Plan parent, Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            int day = json.getInt("day");
            String name = json.getString("name");
            String description = json.getString("description");
            if(id == null || name == null){
                System.err.println("[PlanItem.parse] failed to parse plan item");
                return null;
            }
            return new PlanItem(parent, id, day, name, description);
        }catch(JSONException e){
            System.err.println("[PlanItem.parse] received JSONException.");
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
     * Get the plan item day
     * @return plan item day
     */
    public int getDay(){
        return mDay;
    }

    /**
     * Get the plan item name
     * @return plan item name
     */
    public String getName(){
        return mName;
    }

    /**
     * Get the plan item description
     * @return plan item description
     */
    public String getDescription(){
        return mDescription;
    }

    /**
     * Set the day of this plan item
     * @param day new day
     */
    public void setDay(int day){
        mDay = day;
    }

    /**
     * Set the name of this plan item
     * @param name new name
     */
    public void setName(String name){
        mName = name;
    }

    /**
     * Set the description of this plan item
     * @param description new description
     */
    public void setDescription(String description){
        mDescription = description;
    }

    @Override
    protected HashMap<String, String> getCreateDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("planid", mParent.getId());
        data.put("day", HelperService.toString(mDay));
        data.put("name", mName);
        data.put("description", mDescription);
        return data;
    }

    @Override
    protected HashMap<String, String> getUpdateDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("day", HelperService.toString(mDay));
        data.put("name", mName);
        data.put("description", mDescription);
        return data;
    }

    @Override
    protected String getCreateUrl() {
        return "/data/planitem";
    }

    @Override
    protected String getUpdateUrl() {
        return "/data/planitem/" + getId();
    }

    @Override
    protected String getDeleteUrl() {
        return "/data/planitem/" + getId();
    }
}
