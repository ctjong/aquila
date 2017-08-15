package com.projectaquila.datamodels;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * A collection of public plans
 */
public class PublicPlanCollection extends PlanCollection {
    private String mSortByField;
    private String mSearchQuery;

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
     * Get the search query that results in this collection
     * @return search query string
     */
    public String getSearchQuery(){
        return mSortByField;
    }

    /**
     * Set a new sort-by field.
     * loadItems() needs to be called after invoking this, for the items update to take effect.
     * @param sortByField new sort-by field
     */
    public void setSortByField(String sortByField){
        mSortByField = sortByField;
    }


    /**
     * Set a search query string to update the collection items.
     * loadItems() needs to be called after invoking this, for the items update to take effect.
     * @param searchQuery new search query
     */
    public void setSearchQuery(String searchQuery){
        mSearchQuery = searchQuery;
    }

    /**
     * Get the url format to get the items data
     * @return items URL format
     */
    @Override
    protected String getItemsUrlFormat() {
        if(mSearchQuery != null && !mSearchQuery.equals("")) {
            try {
                String condition = URLEncoder.encode("name~" + mSearchQuery + "|description~" + mSearchQuery, "UTF-8");
                return "/data/plan/public/findbycondition/" + mSortByField + "/{skip}/{take}/" + condition;
            } catch (UnsupportedEncodingException e) {
                System.err.println("[PublicPlanCollection.getItemsUrlFormat] exception");
                e.printStackTrace();
            }
        }
        return "/data/plan/public/findall/" + mSortByField + "/{skip}/{take}";
    }
}
