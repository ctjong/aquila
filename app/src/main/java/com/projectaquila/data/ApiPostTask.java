package com.projectaquila.data;

import android.os.AsyncTask;

import com.projectaquila.common.Callback;
import com.projectaquila.common.Helper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ApiPostTask extends AsyncTask<Void, Void, String> {
    private String mSourceUrl;
    private HashMap<String,String> mData;
    private Callback mCallback;

    public static void execute(String sourceUrl, HashMap<String,String> data, Callback callback){
        ApiPostTask task = new ApiPostTask();
        task.mSourceUrl = sourceUrl;
        task.mData = data;
        task.mCallback = callback;
        task.execute();
    }

    @Override
    protected String doInBackground(Void... urls) {
        URL url;
        String response = "";
        try {
            url = new URL(mSourceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response += line;
                }
                return response;
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        try{
            JSONObject object = (JSONObject) new JSONTokener(result).nextValue();
            mCallback.execute(Helper.ConvertJson(object));
        }catch(JSONException e){
            mCallback.execute(null);
        }
    }

    private String getPostDataString(HashMap<String, String> params) throws JSONException {
        JSONObject json = new JSONObject(params);
        return json.toString(0);
    }
}
