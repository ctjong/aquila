package com.projectaquila.views;

import android.os.Bundle;
import android.view.View;

import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;

import java.util.Map;

/**
 * Base class for all views in this project
 */
public abstract class ViewBase {
    private Map<String, Object> mNavArgs;

    /**
     * Get layout ID
     * @return ID of the layout to use for the current view
     */
    protected abstract int getLayoutId();

    /**
     * Initialize the view for the current view
     */
    protected abstract void initializeView();

    /**
     * Get the string ID to be shown on the title bar
     * @return string ID
     */
    protected int getTitleBarStringId() { return -1; }

    /**
     * Invoked when this view is started
     * @param navArgs navigation arguments
     */
    public void onStart(final Map<String, Object> navArgs) {
        Callback cb = new Callback(){
            @Override
            public void execute(CallbackParams params) {
                mNavArgs = navArgs;
                AppContext.getCurrent().getActivity().loadView(getLayoutId());
                initializeView();
                int titleBarStringId = getTitleBarStringId();
                if(titleBarStringId >= 0) {
                    AppContext.getCurrent().getActivity().setToolbarText(titleBarStringId);
                }
            }
        };
        if(AppContext.getCurrent().getActiveUser() != null && AppContext.getCurrent().getEnrollments() == null){
            AppContext.getCurrent().loadEnrollments(cb);
        }else{
            cb.execute(null);
        }
    }

    /**
     * Find an element in this view with the given id
     * @param viewId view id
     * @return matched view object
     */
    public View findViewById(int viewId){
        return AppContext.getCurrent().getActivity().getCurrentView().findViewById(viewId);
    }

    /**
     * Get navigation argument string with the given key
     * @param key navigation argument key
     * @return navigation argument string value, or null if not found
     */
    protected String getNavArgStr(String key){
        if(mNavArgs != null && mNavArgs.containsKey(key)){
            return (String)mNavArgs.get(key);
        }
        return null;
    }

    /**
     * Get navigation argument object with the given key
     * @param key navigation argument key
     * @return navigation argument object value, or null if not found
     */
    protected Object getNavArgObj(String key){
        if(mNavArgs != null && mNavArgs.containsKey(key)){
            return mNavArgs.get(key);
        }
        return null;
    }
}
