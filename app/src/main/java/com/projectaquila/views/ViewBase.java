package com.projectaquila.views;

import android.os.Bundle;
import android.view.View;

import com.projectaquila.AppContext;

/**
 * Base class for all views in this project
 */
public abstract class ViewBase {
    protected Bundle mNavArgs;

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
     * Invoked when this view is started
     * @param navArgs navigation arguments
     */
    public void onStart(Bundle navArgs) {
        mNavArgs = navArgs;
        AppContext.getCurrent().getActivity().loadView(getLayoutId());
        initializeView();
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
     * Get navigation argument with the given key
     * @param key navigation argument key
     * @return navigation argument value, or null if not found
     */
    protected String getNavArg(String key){
        if(mNavArgs != null && mNavArgs.containsKey(key)){
            return mNavArgs.getString(key);
        }
        return null;
    }
}
