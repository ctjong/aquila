package com.projectaquila.services;

import android.os.AsyncTask;

import com.projectaquila.R;
import com.projectaquila.models.ApiResult;
import com.projectaquila.models.Callback;
import com.projectaquila.AppContext;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.CallbackParams;
import com.projectaquila.views.MainView;

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

public class DataService extends AsyncTask<Void, Void, ApiResult> {
    private static final int TIMEOUT = 7000;

    private ApiTaskMethod mMethod;
    private String mSourceUrl;
    private HashMap<String,String> mData;
    private Callback mCallback;

    public void request(ApiTaskMethod method, String urlPath, HashMap<String,String> data, Callback callback){
        DataService task = new DataService();
        task.mMethod = method;
        task.mSourceUrl = AppContext.getCurrent().getApiBase() + urlPath;
        task.mData = data;
        task.mCallback = callback;
        task.execute();
    }

    @Override
    protected ApiResult doInBackground(Void... params) {
        try {
            System.out.println("[DataService.doInBackground] " + mMethod.name() + " " + mSourceUrl);
            URL url = new URL(mSourceUrl);
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
                    System.out.println("[DataService.doInBackground] param['" + entry.getKey() + "'] = '" + entry.getValue() + "'");
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
            if(mMethod == ApiTaskMethod.GET){
                JSONObject responseObj = (JSONObject)new JSONTokener(responseStr).nextValue();
                int count = Integer.parseInt(responseObj.getString("count"));
                JSONArray items = (JSONArray)responseObj.get("items");
                return new ApiResult(responseCode, count, items);
            }
            return new ApiResult(responseCode, 0, null);
        }catch(SocketTimeoutException e){
            return new ApiResult(404, 0, null);
        } catch (Exception e) {
            System.err.println("[DataService.doInBackground] exception");
            e.printStackTrace();
            return new ApiResult(e);
        }
    }

    @Override
    protected void onPostExecute(ApiResult result) {
        int statusCode = result.getStatusCode();
        System.out.println("[DataService.onPostExecute] found statusCode " + statusCode);
        if(statusCode == 404) {
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_connection);
        }else if(statusCode == 401) {
            if(mMethod == ApiTaskMethod.GET) {
                AppContext.getCurrent().getAuthService().logOut();
                AppContext.getCurrent().getNavigationService().navigate(MainView.class, null);
            }else{
                AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unauthorized);
            }
        }else if(statusCode != 200){
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
        }else if(mCallback != null){
            mCallback.execute(new CallbackParams(result));
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws JSONException {
        JSONObject json = new JSONObject(params);
        return json.toString(0);
    }
}
