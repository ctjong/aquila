package com.projectaquila.datamodels;

import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.TaskDate;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.services.HelperService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * A plan object, which is also a collection of plan tasks.
 */
public class Plan extends CollectionModelBase<PlanTask> {
    private String mOwnerId;
    private int mState;
    private String mName;
    private String mDescription;
    private String mImageUrl;
    private TaskDate mCreatedTime;

    /**
     * Instantiate a new plan
     */
    public Plan(String id, String ownerId, int state, String name, String description, String imageUrl, TaskDate createdTime){
        super(id);
        mOwnerId = ownerId;
        mState = state;
        mName = name;
        mDescription = description;
        mImageUrl = imageUrl;
        mCreatedTime = createdTime;
    }

    /**
     * Try parsing the given object to get a plan
     * @param object input object
     * @return plan, or null on failure
     */
    public static Plan parse(Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            String ownerId = json.getString("ownerid");
            int state = json.getInt("state");
            String name = json.getString("name");
            String description = json.getString("description");
            String imageUrl = json.getString("imageurl");
            TaskDate createdTime = new TaskDate(json.getLong("createdtime"));
            if(id == null || ownerId == null || name == null){
                System.err.println("[Plan.parse] failed to parse plan");
                return null;
            }
            return new Plan(id, ownerId, state, name, description, imageUrl, createdTime);
        }catch(JSONException e){
            System.err.println("[Task.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the plan creator user id
     * @return creator user id
     */
    public String getOwnerId(){
        return mOwnerId;
    }

    /**
     * Get the plan state (0-3)
     * @return plan state (0-3)
     */
    public int getState(){
        return mState;
    }

    /**
     * Get the plan name
     * @return plan name
     */
    public String getName(){
        return mName;
    }

    /**
     * Get the plan description
     * @return plan description
     */
    public String getDescription(){
        return mDescription;
    }

    /**
     * Get the plan image url
     * @return plan image url
     */
    public String getImageUrl(){
        return mImageUrl;
    }

    /**
     * Set the plan state. It has to greater or equal to the current one.
     * @param state new state (0-3)
     */
    public void setState(int state){
        if(state < mState) {
            System.err.println("[Plan.setState] new state " + state + " is smaller than current " + mState + ". abort.");
            return;
        }
        mState = state;
    }

    /**
     * Set the plan name
     * @param name new name
     */
    public void setName(String name){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mOwnerId)){
            mName = name;
        }
    }

    /**
     * Set the plan description
     * @param description new description
     */
    public void setDescription(String description){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mOwnerId)){
            mDescription = description;
        }
    }

    @Override
    protected HashMap<String, String> getCreateDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("name", mName);
        data.put("state", HelperService.toString(mState));
        data.put("description", mDescription);
        data.put("imageurl", mImageUrl);
        return data;
    }

    @Override
    protected String getItemsUrlFormat() {
        try {
            return "/data/plantask/private/findbyconditions/id/{skip}/{take}/" + URLEncoder.encode("planid=" + getId(), "UTF-8");
        }catch(UnsupportedEncodingException e){
            System.err.println("[Plan.getItemsUrlFormat] encoding error");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void setupItems(CallbackParams params) {
        JSONArray items = params.getApiResult().getItems();
        getItems().clear();
        for(int i=0; i<items.length(); i++){
            try {
                PlanTask planTask = PlanTask.parse(this, items.get(i));
                getItems().add(planTask);
            }catch(JSONException e){
                System.err.println("[PlanCollectionAdapter.processServerResponse] an error occurred while trying to get plans at index " + i);
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String getCreateUrl() {
        return "/data/plan";
    }

    @Override
    protected String getUpdateUrl() {
        return "/data/plan/" + getId();
    }

    @Override
    protected String getDeleteUrl() {
        return "/data/plan/" + getId();
    }
}
