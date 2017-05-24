package com.projectaquila.data;

import android.os.AsyncTask;

import com.projectaquila.common.Callback;
import com.projectaquila.common.Helper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiGetTask extends AsyncTask<Void, Void, String> {
    private String mSourceUrl;
    private Callback mCallback;

    public static void execute(String sourceUrl, Callback callback){
        ApiGetTask task = new ApiGetTask();
        task.mSourceUrl = sourceUrl;
        task.mCallback = callback;
        task.execute();
    }

    @Override
    protected String doInBackground(Void... urls) {
        try {
            URL url = new URL(mSourceUrl);
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
            mCallback.execute(Helper.ConvertJson(object));
        }catch(JSONException e){
            mCallback.execute(null);
        }
    }
}
