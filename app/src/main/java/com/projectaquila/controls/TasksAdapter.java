package com.projectaquila.controls;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;

import java.util.HashMap;

public class TasksAdapter extends ArrayAdapter<TaskControl>{

    public TasksAdapter(){
        super(AppContext.getCurrent().getCore(), R.layout.control_tasklistitem);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        final TaskControl taskControl = getItem(position);
        if(taskControl == null){
            System.err.println("[TasksAdapter.getView] failed to get task data at position " + position);
            return null;
        }
        taskControl.addDeleteHandler(new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                remove(taskControl);
                notifyDataSetChanged();
            }
        });
        return taskControl.renderView(convertView);
    }
}
