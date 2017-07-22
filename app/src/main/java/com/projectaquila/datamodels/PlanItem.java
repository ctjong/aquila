package com.projectaquila.datamodels;

import com.projectaquila.services.HelperService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PlanItem extends DataModelBase {
    private int mOrder;
    private String mName;
    private String mDescription;

    public PlanItem(String id, int order, String name, String description){
        super(id);
        mOrder = order;
        mName = name;
        mDescription = description;
    }

    public static PlanItem parse(Object object){
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
            return new PlanItem(id, order, name, description);
        }catch(JSONException e){
            System.err.println("[PlanItem.parse] received JSONException.");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected HashMap<String, String> getDataMap() {
        HashMap<String, String> data = new HashMap<>();
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
