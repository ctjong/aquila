package com.planmaster.services;

import android.content.Intent;

import com.planmaster.contexts.AppContext;
import com.planmaster.R;
import com.planmaster.activities.ChildActivity;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.views.ViewBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A service that handles navigation between views
 */
public class NavigationService {
    private Map<String, Object> mParamsPendingLoad;
    private ViewBase mViewPendingLoad;
    private Stack<ChildActivity> mChildStack;
    private ViewBase mActiveView;

    /**
     * Instantiate a new navigation service
     */
    public NavigationService(){
        mChildStack = new Stack<>();
    }

    /**
     * Navigate to the specified view
     * @param viewClass class of the view to navigate to
     * @param parameters navigation parameters
     */
    public void navigate(Class viewClass, Map<String, Object> parameters){
        if(mActiveView != null) mActiveView.onNavigatedFrom();
        updateCurrentView(viewClass, parameters);
        mViewPendingLoad.onStart(mParamsPendingLoad);
        mActiveView = mViewPendingLoad;
        mViewPendingLoad = null;
        mParamsPendingLoad = null;
    }

    /**
     * Navigate to the specified view in a child activity
     * @param viewClass class of the view to navigate to
     * @param parameters navigation parameters
     */
    public void navigateChild(Class viewClass, Map<String, Object> parameters){
        updateCurrentView(viewClass, parameters);
        Intent intent = new Intent(AppContext.getCurrent().getActivity(), ChildActivity.class);
        AppContext.getCurrent().getActivity().startActivity(intent);
        AppContext.getCurrent().getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_stay);
    }

    /**
     * Invoked when a child activity is loaded
     */
    public void onChildActivityLoad(ChildActivity activity){
        mChildStack.push(activity);
        mViewPendingLoad.onStart(mParamsPendingLoad);
        mActiveView = mViewPendingLoad;
        mViewPendingLoad = null;
        mParamsPendingLoad = null;
        activity.addBackPressedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                if(mChildStack.isEmpty())
                    return;
                mChildStack.pop();
            }
        });
    }

    /**
     * Exit all child activities and go to main
     */
    public void goToMainActivity(){
        if(mChildStack.isEmpty()) return;
        mChildStack.peek().onBackPressed();
        while(!mChildStack.isEmpty()){
            mChildStack.pop().finish();
        }
    }

    /**
     * Exit all child activities, go to main, and load the given view with the given nav params
     */
    public void goToMainActivity(Class viewClass, HashMap<String, Object> viewParams){
        goToMainActivity();
        navigate(viewClass, viewParams);
    }

    /**
     * Get the active child activity, or null if none is active
     * @return child activity or null
     */
    public ChildActivity getActiveChildActivity(){
        if(mChildStack.isEmpty())
            return null;
        return mChildStack.peek();
    }

    /**
     * Update current view based on the given navigation details
     * @param viewClass class of the view to navigate to
     * @param parameters navigation parameters
     */
    private void updateCurrentView(Class viewClass, Map<String, Object> parameters){
        System.out.println("[NavigationService.updateCurrentView] " + viewClass.getName());
        try {
            mViewPendingLoad = (ViewBase)viewClass.newInstance();
            mParamsPendingLoad = parameters;
        } catch (Exception e) {
            HelperService.logError("[NavigationService.updateCurrentView] failed to instantiate view");
            e.printStackTrace();
            return;
        }
    }
}
