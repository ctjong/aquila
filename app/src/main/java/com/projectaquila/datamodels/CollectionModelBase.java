package com.projectaquila.datamodels;

import com.projectaquila.common.ApiTaskMethod;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;

import java.util.HashMap;

public abstract class CollectionModelBase<T> extends DataModelBase {
    private HashMap<String, T> mItems;

    public CollectionModelBase(String id){
        super(id);
        mItems = new HashMap<>();
    }

    protected abstract String getItemsUrlFormat();
    protected abstract void setupItems(CallbackParams params);

    public HashMap<String, T> getItems(){
        return mItems;
    }

    public Task get(String key){
        if(mItems.containsKey(key))
            mItems.get(key);
        return null;
    }

    public void remove(String key){
        if(mItems.containsKey(key))
            mItems.remove(key);
    }

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

    @Override
    protected void write(ApiTaskMethod method, String url, HashMap<String, String> data, final Callback cb){
        //TODO
    }
}
