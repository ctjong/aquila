package com.projectaquila;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskListItem {
    private String mId;
    private String mTitle;

    public TaskListItem(String id, String title){
        mId = id;
        mTitle = title;
    }

    public static TaskListItem parse(JSONObject object){
        try{
            String id = object.getString("id");
            String title = object.getString("title");
            return new TaskListItem(id, title);
        }catch(JSONException e){
            return null;
        }
    }

    public LinearLayout getView(Activity page){
        LinearLayout view = (LinearLayout) LayoutInflater.from(page).inflate(R.layout.view_tasklistitem, null);
        TextView text = (TextView) view.findViewById(R.id.view_tasklistitem_text);
        text.setText(mTitle);
        Button btn = (Button) view.findViewById(R.id.view_tasklistitem_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTestClick();
            }
        });
        return view;
    }

    private void onTestClick(){
        System.out.println("clicked: {id: '" + mId + "', title: '" + mTitle + "'}");
    }
}
