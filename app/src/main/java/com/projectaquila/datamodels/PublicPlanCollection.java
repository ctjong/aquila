package com.projectaquila.datamodels;

/**
 * A collection of public plans
 */
public class PublicPlanCollection extends PlanCollection {
    /**
     * Get the url format to get the items data
     * @return items URL format
     */
    @Override
    protected String getItemsUrlFormat() {
        return "/data/plan/public/findall/id/{skip}/{take}";
    }
}
