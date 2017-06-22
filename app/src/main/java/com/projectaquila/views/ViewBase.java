package com.projectaquila.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.projectaquila.AppContext;

/**
 * Base class for all views in this project
 */
public abstract class ViewBase {
    protected Bundle mSavedState;

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
     * @param savedInstanceState saved state
     */
    public void onStart(Bundle savedInstanceState) {
        mSavedState = savedInstanceState;

        // init layout
        AppContext.current.getShell().load(getLayoutId());

        // init view
        AppContext.current.getShell().showContentScreen();
        initializeView();
    }

    /**
     * Set a key value pair to local setting
     * @param key local setting key
     * @param value local setting value
     */
    public void setLocalSetting(String key, String value){
        SharedPreferences settings = AppContext.current.getShell().getPreferences(0);
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString(key, value);
        settingsEditor.apply();
    }

    /**
     * Find an element in this view with the given id
     * @param viewId view id
     * @return matched view object
     */
    public View findViewById(int viewId){
        return AppContext.current.getShell().getCurrentView().findViewById(viewId);
    }
}
