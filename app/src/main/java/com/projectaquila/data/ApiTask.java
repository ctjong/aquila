package com.projectaquila.data;

import android.os.AsyncTask;

import com.projectaquila.common.Callback;
import com.projectaquila.common.Helper;
import com.projectaquila.context.AppContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ApiTask extends AsyncTask<Void, Void, AsyncTaskResult<JSONObject>> {
    private ApiTaskMethod mMethod;
    private String mSourceUrl;
    private HashMap<String,String> mData;
    private Callback mCallback;

    public static void execute(ApiTaskMethod method, String sourceUrl, HashMap<String,String> data, Callback callback){
        ApiTask task = new ApiTask();
        task.mMethod = method;
        task.mSourceUrl = sourceUrl;
        task.mData = data;
        task.mCallback = callback;
        task.execute();
    }

    @Override
    protected AsyncTaskResult<JSONObject> doInBackground(Void... params) {
        try {
            System.out.println("ApiTask.doInBackground | " + mMethod.name() + " " + mSourceUrl);
            URL url = new URL(mSourceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(AppContext.current.getAccessToken() != null) {
                conn.addRequestProperty("Authorization", "Bearer " + AppContext.current.getAccessToken());
            }
            if(mMethod == ApiTaskMethod.POST) {
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
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
            int responseCode = conn.getResponseCode();
            if(responseCode != 200) {
                throw new Exception("ApiTask.doInBackground status code = " + responseCode);
            }
            String line;
            String responseStr = "";
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                responseStr += line;
            }
            Object responseObj = new JSONTokener(responseStr).nextValue();
            if(responseObj instanceof JSONObject) {
                return new AsyncTaskResult<>((JSONObject)responseObj);
            }else{
                JSONObject response = new JSONObject();
                response.put("value", responseObj);
                return new AsyncTaskResult<>(response);
            }
        } catch (Exception e) {
            System.err.println("ApiTask.doInBackground exception");
            e.printStackTrace();
            return new AsyncTaskResult<>(e);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<JSONObject> result) {
        if(result.getError() != null) {
            mCallback.execute(null);
            return;
        }
        try {
            mCallback.execute(Helper.ConvertJson(result.getResult()));
        } catch(Exception e) {
            System.err.println("ApiTask.onPostExecute exception");
            e.printStackTrace();
            mCallback.execute(null);
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws JSONException {
        JSONObject json = new JSONObject(params);
        return json.toString(0);
    }
}
