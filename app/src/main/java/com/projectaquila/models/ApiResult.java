package com.projectaquila.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ApiResult {
    private int mStatusCode;
    private HashMap<String, Object> mData;

    public ApiResult(int statusCode, JSONObject dataJson){
        mStatusCode = statusCode;
        mData = convertJson(dataJson);
    }

    public int getStatusCode(){
        return mStatusCode;
    }

    public HashMap<String, Object> getData(){
        return mData;
    }

    private HashMap<String, Object> convertJson(JSONObject json){
        if(json == null) return null;
        JSONArray names = json.names();
        HashMap<String, Object> map = new HashMap<>();
        for(int i=0; i<names.length(); i++){
            try {
                String name = (String)names.get(i);
                Object value = json.get(name);
                if(value instanceof JSONObject){
                    value = convertJson((JSONObject)value);
                }
                map.put(name, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
