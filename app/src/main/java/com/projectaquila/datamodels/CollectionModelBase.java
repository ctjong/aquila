package com.projectaquila.datamodels;

import com.projectaquila.common.ApiTaskMethod;
import com.projectaquila.common.AsyncTaskPool;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.services.HelperService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for all collection type data model
 * @param <T> type of item in the collection
 */
public abstract class CollectionModelBase<T extends DataModelBase> extends DataModelBase {
    private List<T> mItems;
    private List<T> mRemoveList;

    /**
     * Construct a new collection data model
     * @param id id of the model
     */
    public CollectionModelBase(String id){
        super(id);
        mItems = new ArrayList<>();
        mRemoveList = new ArrayList<>();
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
     * @return list of collection items
     */
    public List<T> getItems(){
        return mItems;
    }

    /**
     * Get item with the specified id
     * @param id item id
     * @return item, or null if not found
     */
    public T get(String id){
        for(int i=0; i<mItems.size(); i++){
            if(mItems.get(i).getId().equals(id)) {
                return mItems.get(i);
            }
        }
        return null;
    }

    /**
     * Remove the given object from the collection
     * @param item item to remove
     */
    public void remove(T item){
        if(!mItems.contains(item)) return;
        mItems.remove(item);
        if(item.getId() != null) {
            mRemoveList.add(item);
        }
    }

    /**
     * Check whether or not this collection has an item with the given id
     * @param id item id
     * @return true if found, false otherwise
     */
    public boolean contains(String id){
        for(int i=0; i<mItems.size(); i++){
            if(mItems.get(i).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Load items in this collection
     * @param cb callback to execute after it's done
     */
    public void loadItems(final Callback cb){
        String urlFormat = getItemsUrlFormat();
        if(urlFormat == null) {
            if (cb != null) cb.execute(null);
            return;
        }
        AppContext.getCurrent().getDataService().requestAll(urlFormat, getLoadCallback(cb));
    }

    /**
     * Load items in this collection at the specified part
     * @param partNum part number of the items to take
     * @param take number of items to load
     * @param cb callback to execute after it's done
     */
    public void loadItemsPart(int partNum, int take, final Callback cb){
        String urlFormat = getItemsUrlFormat();
        if(urlFormat == null) {
            if (cb != null) cb.execute(null);
            return;
        }
        String url = urlFormat.replace("{skip}", HelperService.toString(partNum * take)).replace("{take}", HelperService.toString(take));
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.GET, url, null, getLoadCallback(cb));
    }

    /**
     * Submit this model data to the server
     * @param method request method
     * @param url target server URL
     * @param data data to submit
     * @param cb callback to execute after it's done
     */
    @Override
    protected void write(final ApiTaskMethod method, String url, HashMap<String, String> data, final Callback cb){
        super.write(method, url, data, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                AsyncTaskPool pool = new AsyncTaskPool(cb);
                for(T item : getItems()){
                    final T currentItem = item;
                    pool.addTask(new Callback() {
                        @Override
                        public void execute(CallbackParams params) {
                            if(method == ApiTaskMethod.DELETE){
                                currentItem.submitDelete((Callback)params.get("cb"));
                            }else{
                                currentItem.submitUpdate((Callback)params.get("cb"));
                            }
                        }
                    });
                }
                for(T item : mRemoveList){
                    final T currentItem = item;
                    mRemoveList.remove(item);
                    pool.addTask(new Callback() {
                        @Override
                        public void execute(CallbackParams params) {
                            currentItem.submitDelete((Callback)params.get("cb"));
                        }
                    });
                }
                pool.execute();
            }
        });
    }

    /**
     * Get callback for items load
     * @return callback
     */
    private Callback getLoadCallback(final Callback cb){
        return new Callback() {
            @Override
            public void execute(CallbackParams params) {
                if(params == null) {
                    if(cb != null) cb.execute(null);
                }
                setupItems(params);
                AsyncTaskPool pool = new AsyncTaskPool(cb);
                for(T item : mItems){
                    final T currentItem = item;
                    pool.addTask(new Callback() {
                        @Override
                        public void execute(CallbackParams params) {
                            currentItem.loadNestedItems((Callback)params.get("cb"));
                        }
                    });
                }
                pool.execute();
            }
        };
    }
}
