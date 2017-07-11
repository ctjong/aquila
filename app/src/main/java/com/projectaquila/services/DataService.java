package com.projectaquila.services;

import android.os.AsyncTask;

import com.projectaquila.R;
import com.projectaquila.models.ApiResult;
import com.projectaquila.models.Callback;
import com.projectaquila.AppContext;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.CallbackParams;
import com.projectaquila.views.LoginView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A service that handles requests to the server
 */
public class DataService {
    private static final int TIMEOUT = 7000;
    private static final int ITEMS_PER_PART = 100;

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
        DataServiceBatchRequest request = new DataServiceBatchRequest(urlFormat, callback);
        request.execute();
    }

    /**
     * A request object to retrieve paginated data
     */
    private class DataServiceBatchRequest {
        private String mUrlFormat;
        private Callback mCallback;
        private int mDownloadCount;
        private List<JSONArray> mResult;

        private DataServiceBatchRequest(String urlFormat, Callback callback){
            mUrlFormat = urlFormat;
            mCallback = callback;
            mDownloadCount = 0;
            mResult = new ArrayList<>();
        }

        private void execute(){
            retrievePart(0);
        }

        private void retrievePart(final int partNum){
            // get data URL
            int skip = partNum * ITEMS_PER_PART;
            String dataUrl = String.format(mUrlFormat, skip, ITEMS_PER_PART);

            // request data
            request(ApiTaskMethod.GET, dataUrl, null, new Callback() {
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

    /**
     * API request object
     */
    private class DataServiceRequest extends AsyncTask<Void, Void, ApiResult>{
        private ApiTaskMethod mMethod;
        private String mApiUrl;
        private HashMap<String,String> mData;
        private Callback mCallback;

        /**
         * Create a new API request object
         * @param method request method
         * @param apiUrl request URL
         * @param data request data
         * @param callback requestCallback
         */
        private DataServiceRequest(ApiTaskMethod method, String apiUrl, HashMap<String, String> data, Callback callback){
            mMethod = method;
            mApiUrl = apiUrl;
            mData = data;
            mCallback = callback;
        }

        /**
         * Execute the request asynchronously
         * @param params request params
         * @return request result
         */
        @Override
        protected ApiResult doInBackground(Void... params) {
            try {
                System.out.println("[DataServiceRequest.doInBackground] " + mMethod.name() + " " + mApiUrl);
                URL url = new URL(mApiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(TIMEOUT);

                // set token header
                String token = AppContext.getCurrent().getAuthService().getAccessToken();
                if(token != null) {
                    conn.addRequestProperty("Authorization", "Bearer " + token);
                }

                // set request method
                if(mMethod != ApiTaskMethod.GET){
                    conn.setRequestMethod(mMethod.name());
                }

                // bind POST/PUT data
                if(mMethod == ApiTaskMethod.POST || mMethod == ApiTaskMethod.PUT) {
                    for(Map.Entry entry : mData.entrySet()){
                        System.out.println("[DataServiceRequest.doInBackground] param['" + entry.getKey() + "'] = '" + entry.getValue() + "'");
                    }
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(15000);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(mData));
                    writer.flush();
                    writer.close();
                    os.close();
                }

                // get the incoming response
                int responseCode = conn.getResponseCode();
                if(responseCode != 200) {
                    return new ApiResult(responseCode, 0, null);
                }
                String line;
                String responseStr = "";
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    responseStr += line;
                }

                // prepare outgoing response
                if(!responseStr.equals("")) {
                    Object responseRaw = new JSONTokener(responseStr).nextValue();
                    if (responseRaw instanceof JSONObject) {
                        JSONObject responseObj = (JSONObject) responseRaw;
                        if (responseObj.isNull("count")) {
                            return new ApiResult(responseCode, responseObj);
                        } else {
                            int count = Integer.parseInt(responseObj.getString("count"));
                            JSONArray items = (JSONArray) responseObj.get("items");
                            return new ApiResult(responseCode, count, items);
                        }
                    }
                }
                return new ApiResult(responseCode, 0, null);
            }catch(SocketTimeoutException e){
                System.err.println("[DataServiceRequest.doInBackground] timed out");
                return new ApiResult(404, 0, null);
            } catch (Exception e) {
                System.err.println("[DataServiceRequest.doInBackground] exception");
                e.printStackTrace();
                return new ApiResult(e);
            }
        }

        /**
         * Invoked when request has been completed
         * @param result request result
         */
        @Override
        protected void onPostExecute(ApiResult result) {
            int statusCode = result.getStatusCode();
            System.out.println("[DataServiceRequest.onPostExecute] found statusCode " + statusCode);
            if(statusCode == 404) {
                AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_connection);
            }else if(statusCode == 401) {
                if(mMethod == ApiTaskMethod.GET) {
                    AppContext.getCurrent().getAuthService().logOut();
                    AppContext.getCurrent().getNavigationService().navigate(LoginView.class, null);
                }else{
                    AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unauthorized);
                }
            }else if(statusCode != 200){
                AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
            }else if(mCallback != null){
                mCallback.execute(new CallbackParams(result));
            }
        }

        /**
         * Get data string from the given parameters
         * @param params parameters map
         * @return parameters string
         * @throws JSONException on failure to generate parameters string
         */
        private String getPostDataString(HashMap<String, String> params) throws JSONException {
            JSONObject json = new JSONObject(params);
            return json.toString(0);
        }
    }

}
