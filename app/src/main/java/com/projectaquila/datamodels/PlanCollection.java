package com.projectaquila.datamodels;

import com.projectaquila.common.CallbackParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A collection of plans
 */
public abstract class PlanCollection extends CollectionModelBase<Plan> {
    /**
     * Instantate a new plan collection
     */
    public PlanCollection(){
        super(null);
    }

    /**
     * Set up the items in this collection from the given response from the server
     * @param params callback params containing data from server
     */
    @Override
    protected void setupItems(CallbackParams params) {
        JSONArray items = params.getApiResult().getItems();
        getItems().clear();
        for(int i=0; i<items.length(); i++){
            try {
                Plan plan = Plan.parse(getPlanJson(items.get(i)));
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

    /**
     * Get plan JSON from the given raw object
     * @param rawObj raw object
     * @return plan JSON object
     */
    protected JSONObject getPlanJson(Object rawObj){
        return (JSONObject)rawObj;
    }
}
