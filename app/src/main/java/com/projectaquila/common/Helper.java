package com.projectaquila.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Helper {

    public static HashMap<String, Object> ConvertJson(JSONObject json){
        JSONArray names = json.names();
        HashMap<String, Object> map = new HashMap<>();
        for(int i=0; i<names.length(); i++){
            try {
                String name = (String)names.get(i);
                Object value = json.get(name);
                if(value instanceof JSONObject){
                    value = ConvertJson((JSONObject)value);
                }
                map.put(name, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
