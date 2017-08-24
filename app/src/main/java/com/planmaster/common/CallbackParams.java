package com.planmaster.common;

import java.util.HashMap;

public class CallbackParams {
    private ApiResult mApiResult;
    private HashMap<String, Object> mFunctionResult;

    public CallbackParams(){
        mApiResult = null;
        mFunctionResult = new HashMap<>();
    }

    public CallbackParams(String key, Object value){
        mApiResult = null;
        mFunctionResult = new HashMap<>();
        set(key, value);
    }

    public CallbackParams(ApiResult apiResult){
        mApiResult = apiResult;
        mFunctionResult = null;
    }

    public void set(String key, Object value){
        if(mFunctionResult != null){
            mFunctionResult.put(key, value);
        }
    }

    public Object get(String key){
        if(mFunctionResult != null){
            return mFunctionResult.get(key);
        }
        return null;
    }

    public ApiResult getApiResult(){
        return mApiResult;
    }
}
