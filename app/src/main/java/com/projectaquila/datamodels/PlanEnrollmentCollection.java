package com.projectaquila.datamodels;

import com.projectaquila.common.CallbackParams;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A collection of plan enrollments
 */
public class PlanEnrollmentCollection extends CollectionModelBase<PlanEnrollment> {
    /**
     * Construct a new collection of plan enrollments
     */
    public PlanEnrollmentCollection(){
        super(null);
    }

    /**
     * Get a collection of enrolled plans from the enrollments
     * @return enrolled plans collection
     */
    public PlanCollection getPlans(){
        PlanCollection plans = new EnrolledPlanCollection();
        for(PlanEnrollment enrollment : getItems()){
            plans.getItems().add(enrollment.getPlan());
        }
        return plans;
    }

    /**
     * Check whether a plan with the specified id is enrolled
     * @param planId plan ID
     * @return true if the plan is enrolled, false otherwise
     */
    public boolean containsPlan(String planId){
        for(PlanEnrollment enrollment : getItems()){
            if(enrollment.getPlan().getId().equals(planId)) return true;
        }
        return false;
    }

    @Override
    protected String getItemsUrlFormat() {
        return "/data/planenrollment/private/findall/id/{skip}/{take}";
    }

    @Override
    protected void setupItems(CallbackParams params) {
        JSONArray items = params.getApiResult().getItems();
        getItems().clear();
        for(int i=0; i<items.length(); i++){
            try {
                PlanEnrollment enrollment = PlanEnrollment.parse(items.get(i));
                if(enrollment != null){
                    getItems().add(enrollment);
                }else{
                    System.err.println("[PlanEnrollmentCollections.setupItems] failed to parse plan enrollment");
                }
            }catch(JSONException e){
                System.err.println("[PlanEnrollmentCollections.setupItems] an error occurred while trying to get plan at index " + i);
                e.printStackTrace();
            }
        }
    }
}
