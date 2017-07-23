package com.projectaquila.datamodels;

import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

public class Plan extends CollectionModelBase<PlanItem> {
    private String mOwnerId;
    private boolean mIsPublic;
    private String mName;
    private String mDescription;
    private String mImageUrl;

    public Plan(String id, String ownerId, boolean isPublic, String name, String description, String imageUrl){
        super(id);
        mOwnerId = ownerId;
        mIsPublic = isPublic;
        mName = name;
        mDescription = description;
        mImageUrl = imageUrl;
    }

    public static Plan parse(Object object){
        if(!(object instanceof JSONObject)){
            return null;
        }
        JSONObject json = (JSONObject)object;
        try{
            String id = json.getString("id");
            String ownerId = json.getString("ownerid");
            boolean isPublic = json.getBoolean("ispublic");
            String name = json.getString("name");
            String description = json.getString("description");
            String imageUrl = json.getString("imageurl");
            if(id == null || ownerId == null || name == null){
                System.err.println("[Plan.parse] failed to parse plan");
                return null;
            }
            return new Plan(id, ownerId, isPublic, name, description, imageUrl);
        }catch(JSONException e){
            System.err.println("[Task.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    public String getName(){
        return mName;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public boolean isPublic(){
        return mIsPublic;
    }

    public void setName(String name){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mOwnerId)){
            mName = name;
        }
    }

    public void setDescription(String description){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mOwnerId)){
            mDescription = description;
        }
    }

    public void setImageUrl(String imageUrl){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mOwnerId)){
            mImageUrl = imageUrl;
        }
    }

    public void setIsPublic(boolean isPublic){
        if(AppContext.getCurrent().getActiveUser().getId().equals(mOwnerId)){
            mIsPublic = isPublic;
        }
    }

    @Override
    protected HashMap<String, String> getDataMap() {
        HashMap<String, String> data = new HashMap<>();
        data.put("name", mName);
        data.put("description", mDescription);
        data.put("imageurl", mImageUrl);
        return data;
    }

    @Override
    protected String getItemsUrlFormat() {
        try {
            if (mIsPublic)
                return "/data/planitem/public/findbyconditions/id/%d/%d/" + URLEncoder.encode("planid=" + getId(), "UTF-8");
            else
                return "/data/planitem/private/findbyconditions/id/%d/%d/" + URLEncoder.encode("planid=" + getId(), "UTF-8");
        }catch(UnsupportedEncodingException e){
            System.err.println("[Plan.getItemsUrlFormat] encoding error");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void setupItems(CallbackParams params) {
        List result = (List)params.get("result");
        getItems().clear();
        if(result == null)
            return;
        for(Object planItemsObj : result){
            JSONArray planItems = (JSONArray)planItemsObj;
            for(int i=0; i<planItems.length(); i++){
                try {
                    PlanItem planItem = PlanItem.parse(this, planItems.get(i));
                    getItems().put(planItem.getId(), planItem);
                }catch(JSONException e){
                    System.err.println("[PlanCollectionAdapter.processServerResponse] an error occurred while trying to get plans at index " + i);
                    e.printStackTrace();
                }
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
