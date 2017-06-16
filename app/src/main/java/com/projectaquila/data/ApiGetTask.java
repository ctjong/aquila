package com.projectaquila.data;

import android.os.AsyncTask;

import com.projectaquila.common.Callback;
import com.projectaquila.common.Helper;
import com.projectaquila.context.AppContext;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiGetTask extends AsyncTask<Void, Void, AsyncTaskResult<JSONObject>> {
    private String mSourceUrl;
    private Callback mCallback;

    public static void execute(String sourceUrl, Callback callback){
        ApiGetTask task = new ApiGetTask();
        task.mSourceUrl = sourceUrl;
        task.mCallback = callback;
        task.execute();
    }

    @Override
    protected AsyncTaskResult<JSONObject> doInBackground(Void... params) {
        try {
            System.out.println("ApiGetTask.doInBackground requesting " + mSourceUrl);
            URL url = new URL(mSourceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                int responseCode = conn.getResponseCode();
                if(responseCode != 200) {
                    throw new Exception("ApiGetTask.doInBackground status code = " + responseCode);
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String result = stringBuilder.toString();
                return new AsyncTaskResult<>((JSONObject) new JSONTokener(result).nextValue());
            }
            finally{
                conn.disconnect();
            }
        }
        catch(Exception e) {
            System.err.println("ApiGetTask.doInBackground exception");
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
            System.err.println("ApiGetTask.onPostExecute exception");
            e.printStackTrace();
            mCallback.execute(null);
        }
    }
}
