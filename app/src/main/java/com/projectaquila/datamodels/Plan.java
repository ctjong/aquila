package com.projectaquila.datamodels;

import com.projectaquila.common.ApiTaskMethod;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.TaskDate;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.services.HelperService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A plan object, which is also a collection of plan tasks.
 */
public class Plan extends CollectionModelBase<PlanTask> {
    private int mState;
    private String mName;
    private String mDescription;
    private String mImageUrl;
    private User mCreator;
    private TaskDate mCreatedTime;

    /**
     * Instantiate a new plan
     */
    public Plan(String id, int state, String name, String description, String imageUrl, User creator, TaskDate createdTime){
        super(id);
        mState = state;
        mName = name;
        mDescription = description;
        mImageUrl = imageUrl;
        mCreator = creator;
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
            int state = json.getInt("state");
            String name = json.getString("name");
            String description = json.getString("description");
            String imageUrl = json.getString("imageurl");
            TaskDate createdTime = new TaskDate(json.getLong("createdtime"));
            if(id == null || name == null){
                System.err.println("[Plan.parse] failed to parse plan");
                return null;
            }
            User creator = null;
            if(json.has("owner")){
                creator = User.parse(json.getJSONObject("owner"));
            }else{
                System.err.println("[Plan.parse] owner info doesn't exist in plan data. deferring its load.");
            }
            return new Plan(id, state, name, description, imageUrl, creator, createdTime);
        }catch(JSONException e){
            System.err.println("[Task.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the plan creator user
     * @return creator user
     */
    public User getCreator(){
        return mCreator;
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
        if(AppContext.getCurrent().getActiveUser().getId().equals(mCreator.getId())){
            mName = name;
        }
    }

    /**
     * Set the plan description
     * @param description new description
     */
    public void setDescription(String description){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mCreator.getId())){
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
            return "/data/plantask/private/findbycondition/id/{skip}/{take}/" + URLEncoder.encode("planid=" + getId(), "UTF-8");
        }catch(UnsupportedEncodingException e){
            System.err.println("[Plan.getItemsUrlFormat] encoding error");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected List<PlanTask> processServerResponse(CallbackParams params) {
        List<PlanTask> list = new ArrayList<>();
        JSONArray items = params.getApiResult().getItems();
        for(int i=0; i<items.length(); i++){
            try {
                PlanTask planTask = PlanTask.parse(this, items.get(i));
                list.add(planTask);
            }catch(JSONException e){
                System.err.println("[PlanCollectionAdapter.processServerResponse] an error occurred while trying to get plans at index " + i);
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    protected void loadSelf(final Callback cb){
        String loadSelfUrl = "/data/plan/public/findbyid/" + getId();
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.GET, loadSelfUrl, null, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                JSONArray items = params.getApiResult().getItems();
                if(items.length() == 0){
                    System.err.println("[Plan.loadSelf] loadSelf returns no data");
                    return;
                }
                try {
                    Object obj = items.get(0);
                    Plan plan = Plan.parse(obj);
                    mCreator = plan.getCreator();
                }catch(JSONException e){
                    System.err.println("[Plan.loadSelf] an error occurred");
                    e.printStackTrace();
                }
                cb.execute(null);
            }
        });
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
