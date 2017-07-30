package com.projectaquila.services;

import com.projectaquila.common.ApiResult;
import com.projectaquila.common.ApiTaskMethod;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * A request object to retrieve paginated data
 */
public class DataServiceBatchRequest {
    private static final int ITEMS_PER_PART = 100;

    private DataService mService;
    private String mUrlFormat;
    private Callback mCallback;
    private int mDownloadCount;
    private List<JSONArray> mResult;

    /**
     * Create a new batch request
     * @param service data service to fire the requests
     * @param urlFormat target url format
     * @param callback callback to execute after completion
     */
    public DataServiceBatchRequest(DataService service, String urlFormat, Callback callback){
        mService = service;
        mUrlFormat = urlFormat;
        mCallback = callback;
        mDownloadCount = 0;
        mResult = new ArrayList<>();
    }

    /**
     * Execute this batch request
     */
    public void execute(){
        retrievePart(0);
    }

    /**
     * Retrieve items at the given part number
     * @param partNum part number to retrieve
     */
    private void retrievePart(final int partNum){
        // get data URL
        int skip = partNum * ITEMS_PER_PART;
        String dataUrl = mUrlFormat.replace("{skip}", HelperService.toString(skip)).replace("{take}", HelperService.toString(ITEMS_PER_PART));

        // request data
        mService.request(ApiTaskMethod.GET, dataUrl, null, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                ApiResult res = params.getApiResult();
                JSONArray tasks = res.getItems();
                mDownloadCount += tasks.length();
                int count = res.getCount();
                System.out.println("[DataServiceBatchRequest.retrievePart] retrieved " + mDownloadCount + "/" + count);
                mResult.add(res.getItems());
                if(mDownloadCount < count){
                    retrievePart(partNum + 1);
                }else{
                    mCallback.execute(new CallbackParams("result", mResult));
                }
            }
        });
    }
}