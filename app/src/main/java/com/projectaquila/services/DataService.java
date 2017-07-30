package com.projectaquila.services;

import com.projectaquila.common.Callback;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.common.ApiTaskMethod;

import java.util.HashMap;

/**
 * A service that handles requests to the server
 */
public class DataService {
    /**
     * Fires a request with the given data
     * @param method request method
     * @param urlPath request URL path
     * @param data request data
     * @param callback request callback
     */
    public void request(ApiTaskMethod method, String urlPath, HashMap<String,String> data, Callback callback){
        DataServiceRequest request = new DataServiceRequest(method, AppContext.getCurrent().getApiBase() + urlPath, data, callback);
        request.execute();
    }

    /**
     * Fires a request until all parts of a paginated set are retrieved
     * @param urlFormat format of the paginated data URL
     * @param callback request callback
     */
    public void requestAll(String urlFormat, Callback callback) {
        DataServiceBatchRequest request = new DataServiceBatchRequest(this, urlFormat, callback);
        request.execute();
    }
}
