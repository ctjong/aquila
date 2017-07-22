package com.projectaquila.datamodels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PlanItem extends DataModelBase {
    private Plan mPlan;
    private int mOrder;
    private String mName;
    private String mDescription;

    public PlanItem(String id, Plan plan, int order, String name, String description){
        super(id);
        mPlan = plan;
        mOrder = order;
        mName = name;
        mDescription = description;
    }

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
            return new PlanItem(id, plan, order, name, description);
        }catch(JSONException e){
            System.err.println("[PlanItem.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected HashMap<String, String> getDataMap() {
        //TODO
        return null;
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
