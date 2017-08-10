package com.projectaquila.datamodels;

/**
 * A collection of public plans
 */
public class PublicPlanCollection extends PlanCollection {
    private String mSortByField;

    /**
     * Create a new public plan collection
     */
    public PublicPlanCollection(){
        mSortByField = "id";
    }

    /**
     * Get the sort-by field of this collection
     * @return sort-by field
     */
    public String getSortByField(){
        return mSortByField;
    }

    /**
     * Set a new sort-by field
     * @param sortByField new sort-by field
     */
    public void setSortByField(String sortByField){
        mSortByField = sortByField;
    }

    /**
     * Get the url format to get the items data
     * @return items URL format
     */
    @Override
    protected String getItemsUrlFormat() {
        return "/data/plan/public/findall/" + mSortByField + "/{skip}/{take}";
    }
}
