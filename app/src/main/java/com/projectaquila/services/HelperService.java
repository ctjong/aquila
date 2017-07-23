package com.projectaquila.services;

import android.content.Context;
import android.os.Build;

import com.projectaquila.contexts.AppContext;

import java.util.HashMap;
import java.util.Locale;

public class HelperService {
    /**
     * Convert the given integer to string
     * @param i integer
     * @return string
     */
    public static String toString(int i){
        Locale currentLocale;
        Context ctx = AppContext.getCurrent().getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale = ctx.getResources().getConfiguration().getLocales().get(0);
        }else{
            currentLocale = ctx.getResources().getConfiguration().locale;
        }
        return String.format(currentLocale, "%d", i);
    }

    /**
     * Create a map with one string-object pair
     * @param key pair key
     * @param val pair value
     * @return map
     */
    public static HashMap<String, Object> getSinglePairMap(String key, Object val){
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, val);
        return map;
    }
}
