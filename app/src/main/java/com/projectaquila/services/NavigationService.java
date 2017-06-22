package com.projectaquila.services;


import android.os.Bundle;

import com.projectaquila.views.ViewBase;

import java.util.Iterator;
import java.util.Map;

public class NavigationService {
    /**
     * Navigate to the specified activity
     * @param newView new view to navigate to
     * @param parameters navigation parameters
     */
    public void navigate(ViewBase newView, Map<String, String> parameters){
        System.out.println("[NavigationService.navigate] " + newView.getClass().getName());
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
        newView.onStart(bundle);
    }
}
