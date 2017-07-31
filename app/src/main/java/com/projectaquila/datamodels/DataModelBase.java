package com.projectaquila.datamodels;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.common.ApiTaskMethod;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.Event;

import org.json.JSONException;

import java.util.HashMap;

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
                        System.err.println("[DataModelBase.write] failed to init model id");
                        e.printStackTrace();
                    }
                }
                if(cb != null)
                    cb.execute(params);
            }
        });
    }

    /**
     * Get a set of key-value pairs of this model's data for create request
     * @return data map
     */
    protected HashMap<String, String> getCreateDataMap() {
        System.err.println("[DataModelBase.getCreateDataMap] not implemented");
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
        System.err.println("[DataModelBase.getCreateUrl] not implemented");
        return null;
    }

    /**
     * Get server URL used to submit update request
     * @return create URL
     */
    protected String getUpdateUrl() {
        System.err.println("[DataModelBase.getUpdateUrl] not implemented");
        return null;
    }

    /**
     * Get server URL used to submit deletion request
     * @return create URL
     */
    protected String getDeleteUrl() {
        System.err.println("[DataModelBase.getDeleteUrl] not implemented");
        return null;
    }
}
