package com.projectaquila.datamodels;

import com.projectaquila.common.ApiTaskMethod;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Base class for all collection type data model
 * @param <T> type of item in the collection
 */
public abstract class CollectionModelBase<T extends DataModelBase> extends DataModelBase {
    private HashMap<String, T> mItems;
    private int mOngoingRequestCount;
    private boolean mRequestsSubmitted;

    /**
     * Construct a new collection data model
     * @param id id of the model
     */
    public CollectionModelBase(String id){
        super(id);
        mItems = new HashMap<>();
    }

    /**
     * Get the url format to get the items data
     * @return items URL format
     */
    protected abstract String getItemsUrlFormat();

    /**
     * Set up the items in this collection from the given response from the server
     * @param params callback params containing data from server
     */
    protected abstract void setupItems(CallbackParams params);

    /**
     * Get the items in the collection
     * @return map of collection items
     */
    public HashMap<String, T> getItems(){
        return mItems;
    }

    /**
     * Get item with the specified key
     * @param key item key
     * @return item object
     */
    public Task get(String key){
        if(mItems.containsKey(key))
            mItems.get(key);
        return null;
    }

    /**
     * Remove item with the specified key
     * @param key item key
     */
    public void remove(String key){
        if(mItems.containsKey(key))
            mItems.remove(key);
    }

    /**
     * Load items in this collection
     * @param cb callback to execute after it's done
     */
    public void load(final Callback cb){
        String urlFormat = getItemsUrlFormat();
        if(urlFormat == null) {
            if (cb != null) cb.execute(null);
            return;
        }
        AppContext.getCurrent().getDataService().requestAll(urlFormat, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                if(params == null) {
                    if(cb != null) cb.execute(null);
                }
                setupItems(params);
                if(cb != null)
                    cb.execute(params);
            }
        });
    }

    /**
     * Load items in this collection at the specified part
     * @param partNum part number of the items to take
     * @param take number of items to load
     * @param cb callback to execute after it's done
     */
    public void loadPart(int partNum, int take, final Callback cb){
        String urlFormat = getItemsUrlFormat();
        if(urlFormat == null) {
            if (cb != null) cb.execute(null);
            return;
        }
        String url = String.format(urlFormat, partNum * take, take);
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.GET, url, null, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                if(params == null) {
                    if(cb != null) cb.execute(null);
                }
                setupItems(params);
                if(cb != null)
                    cb.execute(params);
            }
        });
    }

    /**
     * Submit this model data to the server
     * @param method request method
     * @param url target server URL
     * @param data data to submit
     * @param cb callback to execute after it's done
     */
    @Override
    protected void write(ApiTaskMethod method, String url, HashMap<String, String> data, final Callback cb){
        super.write(method, url, data, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                try {
                    mRequestsSubmitted = false;
                    mOngoingRequestCount = 0;
                    if(getId() == null){
                        initializeId(params.getApiResult().getObject().getString("value"));
                    }
                    for(DataModelBase item : getItems().values()){
                        mOngoingRequestCount++;
                        Callback itemCallback = new Callback(){
                            @Override
                            public void execute(CallbackParams params) {
                                mOngoingRequestCount--;
                                if(mRequestsSubmitted && mOngoingRequestCount <= 0) {
                                    System.out.println("[CollectionModelBase.write] all requests completed. executing callback.");
                                    cb.execute(null);
                                    mRequestsSubmitted = false;
                                }
                            }
                        };
                        if(method == ApiTaskMethod.DELETE){
                            item.submitDelete(itemCallback);
                        }else{
                            item.submitUpdate(itemCallback);
                        }
                    }
                    mRequestsSubmitted = true;
                    System.out.println("[CollectionModelBase.write] all requests submitted");
                } catch (JSONException e) {
                    System.err.println("[CollectionModelBase.write] exception");
                    e.printStackTrace();
                }
            }
        });
    }
}
