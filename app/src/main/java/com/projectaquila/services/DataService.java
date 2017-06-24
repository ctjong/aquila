package com.projectaquila.services;

import android.os.AsyncTask;

import com.projectaquila.R;
import com.projectaquila.models.ApiResult;
import com.projectaquila.models.Callback;
import com.projectaquila.AppContext;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.AsyncTaskResult;
import com.projectaquila.models.S;
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
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;

public class DataService extends AsyncTask<Void, Void, AsyncTaskResult<ApiResult>> {
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
    protected AsyncTaskResult<ApiResult> doInBackground(Void... params) {
        try {
            System.out.println("[DataService.doInBackground] " + mMethod.name() + " " + mSourceUrl);
            URL url = new URL(mSourceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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
                return new AsyncTaskResult<>(new ApiResult(responseCode, null));
            }
            String line;
            String responseStr = "";
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                responseStr += line;
            }

            // prepare outgoing response
            Object responseObj;
            try {
                responseObj = new JSONTokener(responseStr).nextValue();
            }catch(JSONException e){
                responseObj = responseStr;
            }
            JSONObject res;
            if(responseObj instanceof JSONObject){
                res = (JSONObject)responseObj;
            }else{
                res = new JSONObject();
                res.put("value", responseObj);
            }
            return new AsyncTaskResult<>(new ApiResult(responseCode, res));
        }catch(SocketException e){
            return new AsyncTaskResult<>(new ApiResult(404, null));
        } catch (Exception e) {
            System.err.println("[DataService.doInBackground] exception");
            e.printStackTrace();
            return new AsyncTaskResult<>(e);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<ApiResult> result) {
        if(result.getError() != null) {
            AppContext.getCurrent().getShell().showErrorScreen(R.string.shell_error_unknown);
            mCallback.execute(null, S.Error);
        }else{
            ApiResult res = result.getResult();
            int statusCode = res.getStatusCode();
            System.out.println("[DataService.onPostExecute] found statusCode " + statusCode);
            if(statusCode == 404) {
                AppContext.getCurrent().getShell().showErrorScreen(R.string.shell_error_connection);
                mCallback.execute(null, S.Error);
            }else if(statusCode == 401) {
                AppContext.getCurrent().getNavigationService().navigate(MainView.class, null);
                mCallback.execute(null, S.Error);
            }else if(statusCode != 200){
                AppContext.getCurrent().getShell().showErrorScreen(R.string.shell_error_unknown);
                mCallback.execute(null, S.Error);
            }else{
                HashMap<String, Object> data = res.getData();
                mCallback.execute(data, S.OK);
            }
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws JSONException {
        JSONObject json = new JSONObject(params);
        return json.toString(0);
    }
}
