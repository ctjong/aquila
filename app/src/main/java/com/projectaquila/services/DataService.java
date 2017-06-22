package com.projectaquila.services;

import android.os.AsyncTask;

import com.projectaquila.models.Callback;
import com.projectaquila.AppContext;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.AsyncTaskResult;

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
import java.net.URL;
import java.util.HashMap;

public class DataService extends AsyncTask<Void, Void, AsyncTaskResult<JSONObject>> {
    private ApiTaskMethod mMethod;
    private String mSourceUrl;
    private HashMap<String,String> mData;
    private Callback mCallback;

    public void request(ApiTaskMethod method, String sourceUrl, HashMap<String,String> data, Callback callback){
        DataService task = new DataService();
        task.mMethod = method;
        task.mSourceUrl = sourceUrl;
        task.mData = data;
        task.mCallback = callback;
        task.execute();
    }

    @Override
    protected AsyncTaskResult<JSONObject> doInBackground(Void... params) {
        try {
            System.out.println("[DataService.doInBackground] " + mMethod.name() + " " + mSourceUrl);
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
                throw new Exception("DataService.doInBackground status code = " + responseCode);
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
            System.err.println("[DataService.doInBackground] exception");
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
            mCallback.execute(convertJson(result.getResult()));
        } catch(Exception e) {
            System.err.println("[DataService.onPostExecute] exception");
            e.printStackTrace();
            mCallback.execute(null);
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws JSONException {
        JSONObject json = new JSONObject(params);
        return json.toString(0);
    }

    private HashMap<String, Object> convertJson(JSONObject json){
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
