package com.projectaquila.datamodels;

/**
 * A collection of plans created by current user
 */
public class CreatedPlanCollection extends PlanCollection {
    /**
     * Get the url format to get the items data
     * @return items URL format
     */
    @Override
    protected String getItemsUrlFormat() {
        return "/data/plan/private/findall/id/{skip}/{take}";
    }
}
