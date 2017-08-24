package com.planmaster.datamodels;

import com.planmaster.contexts.AppContext;
import com.planmaster.common.ApiTaskMethod;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.common.Event;
import com.planmaster.services.HelperService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for all data models
 */
public abstract class DataModelBase {
    private String mId;
    private Event mChangedEvent;

    /**
     * Construct a new data model
     * @param id the model id
     */
    public DataModelBase(String id){
        mId = id;
        mChangedEvent = new Event();
    }

    /**
     * Add the given handler to the changed event
     * @param handler changed event handler
     */
    public void addChangedHandler(Callback handler){
        mChangedEvent.addHandler(handler);
    }

    /**
     * Remove the given handler from the changed event
     * @param handler handler to remove
     */
    public void removeChangedHandler(Callback handler){
        mChangedEvent.removeHandler(handler);
    }

    /**
     * Remove all changed event handlers
     */
    public void removeChangedHandlers(){
        mChangedEvent.removeAllHandlers();
    }

    /**
     * Notify all listeners to the changed event
     */
    public void notifyListeners(){
        mChangedEvent.invoke(null);
    }

    /**
     * Submit all updates made to this model to the server
     * @param cb callback to execute after it's done
     */
    public void submitUpdate(Callback cb){
        if(mId == null)
            write(ApiTaskMethod.POST, getCreateUrl(), getCreateDataMap(), cb);
        else
            write(ApiTaskMethod.PUT, getUpdateUrl(), getUpdateDataMap(), cb);
    }

    /**
     * Submit deletion of this model to the server
     * @param cb callback to execute after it's done
     */
    public void submitDelete(Callback cb){
        write(ApiTaskMethod.DELETE, getDeleteUrl(), null, cb);
    }

    /**
     * Get this model's id
     * @return this model's id
     */
    public String getId() {
        return mId;
    }

    /**
     * Submit this model data to the server
     * @param method request method
     * @param url target server URL
     * @param data data to submit
     * @param cb callback to execute after it's done
     */
    protected void write(final ApiTaskMethod method, String url, HashMap<String, String> data, final Callback cb){
        if(url == null) {
            if (cb != null) cb.execute(null);
            return;
        }
        AppContext.getCurrent().getDataService().request(method, url, data, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                if(method == ApiTaskMethod.POST && mId == null && params != null){
                    try {
                        mId = params.getApiResult().getObject().getString("value");
                    } catch (JSONException e) {
                        HelperService.logError("[DataModelBase.write] failed to init model id");
                        e.printStackTrace();
                    }
                }
                if(cb != null)
                    cb.execute(params);
            }
        });
    }

    /**
     * Load nested items in this data item. This will do the following:
     * - resolve second level important foreign keys that are not resolved on the collection level load.
     * - if this is also a collection (nested collection), load the collection items
     * @param cb callback
     */
    protected void loadNestedItems(final Callback cb){
        List<DataModelBase> nestedItems = getNestedItems();
        if(nestedItems.isEmpty()){
            cb.execute(null);
            return;
        }
        for(DataModelBase nestedItem : nestedItems){
            final DataModelBase currentItem = nestedItem;
            currentItem.loadSelf(new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    if(currentItem instanceof CollectionModelBase){
                        final CollectionModelBase collection = ((CollectionModelBase)currentItem);
                        collection.loadItems(cb);
                    }else{
                        cb.execute(null);
                    }
                }
            });
        }
    }

    /**
     * Load this data item individually
     * @param cb load callback
     */
    protected void loadSelf(final Callback cb){
        cb.execute(null);
    }

    /**
     * Get a list of nested items to load separately. This is empty by default.
     * If there are nested items, this should be overridden.
     * @return nested items
     */
    protected List<DataModelBase> getNestedItems(){
        return new ArrayList<>();
    }

    /**
     * Get a set of key-value pairs of this model's data for create request
     * @return data map
     */
    protected HashMap<String, String> getCreateDataMap() {
        HelperService.logError("[DataModelBase.getCreateDataMap] not implemented");
        return null;
    }

    /**
     * Get a set of key-value pairs of this model's data for update request
     * @return data map
     */
    protected HashMap<String, String> getUpdateDataMap() {
        return getCreateDataMap();
    }

    /**
     * Get server URL used to submit creation request
     * @return create URL
     */
    protected String getCreateUrl() {
        HelperService.logError("[DataModelBase.getCreateUrl] not implemented");
        return null;
    }

    /**
     * Get server URL used to submit update request
     * @return create URL
     */
    protected String getUpdateUrl() {
        HelperService.logError("[DataModelBase.getUpdateUrl] not implemented");
        return null;
    }

    /**
     * Get server URL used to submit deletion request
     * @return create URL
     */
    protected String getDeleteUrl() {
        HelperService.logError("[DataModelBase.getDeleteUrl] not implemented");
        return null;
    }
}
