package com.projectaquila.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ApiResult {
    private int mStatusCode;
    private int mCount;
    private JSONArray mItems;
    private Exception mException;

    public ApiResult(int statusCode, int count, JSONArray items){
        mStatusCode = statusCode;
        mCount = count;
        mItems = items;
        mException = null;
    }

    public ApiResult(Exception e){
        mStatusCode = -1;
        mCount = 0;
        mItems = null;
        mException = e;
    }

    public int getStatusCode(){
        return mStatusCode;
    }

    public int getCount(){
        return mCount;
    }

    public JSONArray getItems(){
        return mItems;
    }

    public boolean isSuccess(){
        return mStatusCode == 200;
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
