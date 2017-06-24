package com.projectaquila.services;


import android.os.Bundle;

import com.projectaquila.views.ViewBase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A service that handles navigation between views
 */
public class NavigationService {
    private HashMap<String, ViewBase> mViews;
    private Bundle mCurrentViewParams;
    private ViewBase mCurrentView;

    /**
     * Instantiate a new navigation service
     */
    public NavigationService(){
        mViews = new HashMap<>();
    }

    /**
     * Navigate to the specified activity
     * @param viewClass class of the view to navigate to
     * @param parameters navigation parameters
     */
    public void navigate(Class viewClass, Map<String, String> parameters){
        System.out.println("[NavigationService.navigate] " + viewClass.getName());
        ViewBase view;
        if(mViews.containsKey(viewClass.getName())){
            view = mViews.get(viewClass.getName());
        }else{
            try {
                view = (ViewBase)viewClass.newInstance();
                mViews.put(viewClass.getName(), view);
            } catch (Exception e) {
                System.err.println("[NavigationService.navigate] failed to instantiate view");
                e.printStackTrace();
                return;
            }
        }
        Bundle bundle = new Bundle();
        if(parameters != null){
            Iterator it = parameters.entrySet().iterator();
            while (it.hasNext()) {
                Object pairRaw = it.next();
                if(!(pairRaw instanceof Map.Entry)) continue;
                Map.Entry pair = (Map.Entry) it.next();
                Object key = pair.getKey();
                Object value = pair.getValue();
                if(!(key instanceof String) || !(value instanceof String)) continue;
                bundle.putString((String) key, (String) value);
            }
        }
        mCurrentView = view;
        mCurrentViewParams = bundle;
        view.onStart(bundle);
    }

    /**
     * Reload current view
     */
    public void reload(){
        mCurrentView.onStart(mCurrentViewParams);
    }
}
