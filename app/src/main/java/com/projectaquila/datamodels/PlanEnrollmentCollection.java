package com.projectaquila.datamodels;

import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.PlanCollectionType;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class PlanEnrollmentCollection extends CollectionModelBase<PlanEnrollment> {
    public PlanEnrollmentCollection(){
        super(null);
    }

    public PlanCollection getPlans(){
        PlanCollection plans = new PlanCollection(PlanCollectionType.ENROLLED);
        for(PlanEnrollment enrollment : getItems()){
            plans.getItems().add(enrollment.getPlan());
        }
        return plans;
    }

    @Override
    protected String getItemsUrlFormat() {
        return "/data/planenrollment/private/findall/id/{skip}/{take}";
    }

    @Override
    protected void setupItems(CallbackParams params) {
        List result = (List)params.get("result");
        getItems().clear();
        if(result == null)
            return;
        for(Object enrollmentsObj : result){
            JSONArray enrollments = (JSONArray)enrollmentsObj;
            for(int i=0; i<enrollments.length(); i++){
                try {
                    PlanEnrollment enrollment = PlanEnrollment.parse(enrollments.get(i));
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
}
