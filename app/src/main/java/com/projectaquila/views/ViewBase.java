package com.projectaquila.views;

import android.os.Bundle;
import android.view.View;

import com.projectaquila.AppContext;
import com.projectaquila.R;

/**
 * Base class for all views in this project
 */
public abstract class ViewBase {
    private Bundle mNavArgs;

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
    public void onStart(Bundle navArgs) {
        mNavArgs = navArgs;
        AppContext.getCurrent().getActivity().loadView(getLayoutId());
        int titleBarStringId = getTitleBarStringId();
        if(titleBarStringId >= 0) {
            AppContext.getCurrent().getActivity().setToolbarText(titleBarStringId);
        }
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
