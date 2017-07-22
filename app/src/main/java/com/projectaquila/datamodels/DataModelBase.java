package com.projectaquila.datamodels;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.common.ApiTaskMethod;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.Event;

import java.util.HashMap;

public abstract class DataModelBase {
    private String mId;
    private Event mChangedEvent;

    public DataModelBase(String id){
        mId = id;
        mChangedEvent = new Event();
    }

    public void addChangedHandler(Callback handler){
        mChangedEvent.addHandler(handler);
    }

    public void notifyListeners(){
        mChangedEvent.invoke(null);
    }

    public void submitUpdate(Callback cb){
        if(mId == null)
            write(ApiTaskMethod.POST, getCreateUrl(), getDataMap(), cb);
        else
            write(ApiTaskMethod.PUT, getUpdateUrl(), getDataMap(), cb);
    }

    public void submitDelete(Callback cb){
        write(ApiTaskMethod.DELETE, getDeleteUrl(), null, cb);
    }

    public String getId() {
        return mId;
    }

    protected void write(ApiTaskMethod method, String url, HashMap<String, String> data, final Callback cb){
        if(url == null) {
            if (cb != null) cb.execute(null);
            return;
        }
        AppContext.getCurrent().getDataService().request(method, url, data, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                if(cb != null)
                    cb.execute(params);
            }
        });
    }

    protected HashMap<String, String> getDataMap() {
        System.err.println("[DataModelBase.getDataMap] not implemented");
        return null;
    }

    protected String getCreateUrl() {
        System.err.println("[DataModelBase.getCreateUrl] not implemented");
        return null;
    }

    protected String getUpdateUrl() {
        System.err.println("[DataModelBase.getUpdateUrl] not implemented");
        return null;
    }

    protected String getDeleteUrl() {
        System.err.println("[DataModelBase.getDeleteUrl] not implemented");
        return null;
    }
}
