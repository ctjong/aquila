package com.planmaster.datamodels;

import com.planmaster.services.HelperService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A collection of plans enrolled by the current user
 */
public class EnrolledPlanCollection extends PlanCollection {
    /**
     * Get the url format to get the items data
     * @return items URL format
     */
    @Override
    protected String getItemsUrlFormat() {
        return null;
    }

    /**
     * Get plan JSON from the given raw object
     * @param rawObj raw object
     * @return plan JSON object
     */
    @Override
    protected JSONObject getPlanJson(Object rawObj){
        try{
            return ((JSONObject)rawObj).getJSONObject("plan");
        }catch(JSONException e){
            HelperService.logError("[EnrolledPlanCollection.getPlanJson] failed to get plan json");
            e.printStackTrace();
            return null;
        }
    }
}
