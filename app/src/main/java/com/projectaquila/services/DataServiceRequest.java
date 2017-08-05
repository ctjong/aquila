package com.projectaquila.services;

import android.os.AsyncTask;

import com.projectaquila.R;
import com.projectaquila.common.ApiResult;
import com.projectaquila.common.ApiTaskMethod;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
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
import java.util.HashMap;
import java.util.Map;

/**
 * API request object
 */
public class DataServiceRequest extends AsyncTask<Void, Void, ApiResult> {
    private static final int TIMEOUT = 7000;

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
    public DataServiceRequest(ApiTaskMethod method, String apiUrl, HashMap<String, String> data, Callback callback){
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
            if(AppContext.getCurrent().getActiveUser() != null) {
                String token = AppContext.getCurrent().getActiveUser().getToken();
                conn.addRequestProperty("Authorization", "Bearer " + token);
                System.out.println("[DataServiceRequest.doInBackground] header['Authorization'] = " + "Bearer " + token);
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
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("value", responseStr);
                    return new ApiResult(responseCode, obj);
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