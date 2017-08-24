package com.planmaster.datamodels;

import com.planmaster.services.HelperService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * A collection of plans created by certain user
 */
public class UserPlanCollection extends PlanCollection {
    private String mUserId;

    /**
     * Construct a new user plan collection
     * @param userId collection's author user id
     */
    public UserPlanCollection(String userId){
        mUserId = userId;
    }

    /**
     * Get the url format to get the items data
     * @return items URL format
     */
    @Override
    protected String getItemsUrlFormat() {
        try {
            String condition = URLEncoder.encode("ownerid=" + mUserId, "UTF-8");
            return "/data/plan/public/findbycondition/id/{skip}/{take}/" + condition;
        } catch (UnsupportedEncodingException e) {
            HelperService.logError("[UserPlanCollection.getItemsUrlFormat] exception " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
