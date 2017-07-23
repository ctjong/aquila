package com.projectaquila.datamodels;

import com.projectaquila.services.HelperService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A single task in a plan
 */
public class PlanItem extends DataModelBase {
    private Plan mPlan;
    private int mOrder;
    private String mName;
    private String mDescription;

    /**
     * Construct a new plan item
     * @param plan parent plan
     * @param id plan item id
     * @param order plan item order in the plan
     * @param name plan item name
     * @param description plan item description
     */
    public PlanItem(Plan plan, String id, int order, String name, String description){
        super(id);
        mPlan = plan;
        mOrder = order;
        mName = name;
        mDescription = description;
    }

    /**
     * Parse the given object and try to create a plan item object
     * @param plan parent plan
     * @param object input object
     * @return plan item object, or null on failure
     */
    public static PlanItem parse(Plan plan, Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            int order = json.getInt("order");
            String name = json.getString("name");
            String description = json.getString("description");
            if(id == null || name == null){
                System.err.println("[PlanItem.parse] failed to parse plan item");
                return null;
            }
            return new PlanItem(plan, id, order, name, description);
        }catch(JSONException e){
            System.err.println("[PlanItem.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the plan item order
     * @return plan item order
     */
    public int getOrder(){
        return mOrder;
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
     * Set the order of this plan item
     * @param order new order
     */
    public void setOrder(int order){
        mOrder = order;
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
    protected HashMap<String, String> getDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("planid", mPlan.getId());
        data.put("order", HelperService.toString(mOrder));
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
