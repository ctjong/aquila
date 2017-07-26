package com.projectaquila.datamodels;

import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.PlanCollectionType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PlanCollection extends CollectionModelBase<Plan> {
    private PlanCollectionType mType;

    public PlanCollection(PlanCollectionType type){
        super(null);
        mType = type;
    }

    @Override
    protected String getItemsUrlFormat() {
        if(mType == PlanCollectionType.ENROLLED){
            return "/data/planenrollment/private/findall/id/{skip}/{take}";
        }else if(mType == PlanCollectionType.CREATED){
            return "/data/plan/private/findall/id/{skip}/{take}";
        }else{
            return "/data/plan/public/findall/id/{skip}/{take}";
        }
    }

    @Override
    protected void setupItems(CallbackParams params) {
        List result = (List)params.get("result");
        getItems().clear();
        if(result == null)
            return;
        for(Object plansObj : result){
            JSONArray plans = (JSONArray)plansObj;
            for(int i=0; i<plans.length(); i++){
                try {
                    Object planJson = plans.get(i);
                    if(mType == PlanCollectionType.ENROLLED && planJson instanceof JSONObject){
                        planJson = ((JSONObject)planJson).getJSONObject("plan");
                    }
                    Plan plan = Plan.parse(planJson);
                    if(plan != null){
                        getItems().add(plan);
                    }else{
                        System.err.println("[PlanCollectionAdapter.processServerResponse] failed to parse plan, null found");
                    }
                }catch(JSONException e){
                    System.err.println("[PlanCollectionAdapter.processServerResponse] an error occurred while trying to get plan at index " + i);
                    e.printStackTrace();
                }
            }
        }
    }
}
