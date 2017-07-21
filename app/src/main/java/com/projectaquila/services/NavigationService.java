package com.projectaquila.services;


import android.content.Intent;
import android.os.Bundle;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.activities.ChildActivity;
import com.projectaquila.models.Callback;
import com.projectaquila.models.CallbackParams;
import com.projectaquila.views.ViewBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A service that handles navigation between views
 */
public class NavigationService {
    private HashMap<String, ViewBase> mViews;
    private Bundle mCurrentViewParams;
    private ViewBase mCurrentView;
    private Stack<ChildActivity> mChildStack;

    /**
     * Instantiate a new navigation service
     */
    public NavigationService(){
        mViews = new HashMap<>();
        mChildStack = new Stack<>();
    }

    /**
     * Navigate to the specified view
     * @param viewClass class of the view to navigate to
     * @param parameters navigation parameters
     */
    public void navigate(Class viewClass, Map<String, String> parameters){
        updateCurrentView(viewClass, parameters);
        mCurrentView.onStart(mCurrentViewParams);
    }

    /**
     * Navigate to the specified view in a child activity
     * @param viewClass class of the view to navigate to
     * @param parameters navigation parameters
     */
    public void navigateChild(Class viewClass, Map<String, String> parameters){
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
        mCurrentView.onStart(mCurrentViewParams);
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
     * Go up one level in the back stack programmatically
     */
    public void goBack() {
        if(mChildStack.isEmpty())
            return;
        mChildStack.peek().onBackPressed();
    }

    /**
     * Exit all child activities and go to main
     */
    public void goToMainActivity(){
        goBack();
        while(!mChildStack.isEmpty()){
            mChildStack.pop().finish();
        }
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
    private void updateCurrentView(Class viewClass, Map<String, String> parameters){
        System.out.println("[NavigationService.updateCurrentView] " + viewClass.getName());
        ViewBase view;
        if(mViews.containsKey(viewClass.getName())){
            view = mViews.get(viewClass.getName());
        }else{
            try {
                view = (ViewBase)viewClass.newInstance();
                mViews.put(viewClass.getName(), view);
            } catch (Exception e) {
                System.err.println("[NavigationService.updateCurrentView] failed to instantiate view");
                e.printStackTrace();
                return;
            }
        }
        Bundle bundle = new Bundle();
        if(parameters != null){
            for (Map.Entry pair : parameters.entrySet()) {
                Object key = pair.getKey();
                Object value = pair.getValue();
                if (!(key instanceof String) || !(value instanceof String)) continue;
                bundle.putString((String) key, (String) value);
            }
        }
        mCurrentView = view;
        mCurrentViewParams = bundle;
    }
}
