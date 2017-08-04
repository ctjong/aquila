package com.projectaquila.datamodels;

import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.PlanCollectionType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlanCollection extends CollectionModelBase<Plan> {
    private PlanCollectionType mType;

    public PlanCollection(PlanCollectionType type){
        super(null);
        mType = type;
    }

    @Override
    protected String getItemsUrlFormat() {
        if(mType == PlanCollectionType.BROWSE){
            return "/data/plan/public/findall/id/{skip}/{take}";
        }else if(mType == PlanCollectionType.CREATED){
            return "/data/plan/private/findall/id/{skip}/{take}";
        }else{
            return null;
        }
    }

    @Override
    protected void setupItems(CallbackParams params) {
        JSONArray items = params.getApiResult().getItems();
        getItems().clear();
        for(int i=0; i<items.length(); i++){
            try {
                Object planJson = items.get(i);
                if(mType == PlanCollectionType.ENROLLED && planJson instanceof JSONObject){
                    planJson = ((JSONObject)planJson).getJSONObject("plan");
                }
                Plan plan = Plan.parse(planJson);
                if(plan != null){
                    getItems().add(plan);
                }else{
                    System.err.println("[PlanCollection.setupItems] failed to parse plan, null found");
                }
            } catch (JSONException e) {
                System.err.println("[PlanCollection.setupItems] an error occurred while trying to get plan at index " + i);
                e.printStackTrace();
            }
        }
    }
}
