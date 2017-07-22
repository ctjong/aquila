package com.projectaquila.common;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiResult {
    private int mStatusCode;
    private int mCount;
    private JSONArray mItems;
    private JSONObject mObject;
    private Exception mException;

    public ApiResult(int statusCode, int count, JSONArray items){
        mStatusCode = statusCode;
        mCount = count;
        mItems = items;
        mException = null;
    }

    public ApiResult(int statusCode, JSONObject object){
        mStatusCode = statusCode;
        mObject = object;
        mItems = null;
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

    public JSONObject getObject(){
        return mObject;
    }
}
