package com.planmaster.views;

import android.view.View;

import com.planmaster.R;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.contexts.AppContext;
import com.planmaster.services.HelperService;

import java.util.Map;

/**
 * Base class for all views in this project
 */
public abstract class ViewBase {
    private Map<String, Object> mNavArgs;
    protected boolean mViewLoadAborted;

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
    protected abstract int getTitleBarStringId();

    /**
     * Invoked when this view is started
     * @param navArgs navigation arguments
     */
    public void onStart(final Map<String, Object> navArgs) {
        try {
            mViewLoadAborted = false;
            Callback cb = new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    try {
                        mNavArgs = navArgs;
                        AppContext.getCurrent().getActivity().loadView(getLayoutId());
                        AppContext.getCurrent().getActivity().hideLoadingScreen();
                        initializeView();
                        if (mViewLoadAborted) return;
                        int titleBarStringId = getTitleBarStringId();
                        AppContext.getCurrent().getActivity().setToolbarText(titleBarStringId);
                    }catch(Exception e){
                        HelperService.logError("[ViewBase.onStart] exception in inner logic");
                        e.printStackTrace();
                        AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
                    }
                }
            };
            if (AppContext.getCurrent().getActiveUser() != null && AppContext.getCurrent().getEnrollments() == null) {
                AppContext.getCurrent().getActivity().showLoadingScreen();
                AppContext.getCurrent().loadEnrollments(cb);
            } else {
                cb.execute(null);
            }
        }catch(Exception e){
            HelperService.logError("[ViewBase.onStart] exception in outer logic");
            e.printStackTrace();
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
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
     * Invoked when we are navigating away from this view
     */
    public void onNavigatedFrom(){
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
