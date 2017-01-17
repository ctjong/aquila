package com.projectaquila;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiTask extends AsyncTask<Void, Void, String> {

    private String sourceUrl;
    private ApiCallback callback;

    public ApiTask(String sourceUrl, ApiCallback callback){
        this.sourceUrl = sourceUrl;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... urls) {
        try {
            URL url = new URL(sourceUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try{
            JSONObject object = (JSONObject) new JSONTokener(result).nextValue();
            callback.execute(object);
        }catch(JSONException e){
            callback.execute(null);
        }
    }
}
