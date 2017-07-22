package com.projectaquila.datamodels;

import com.projectaquila.common.CallbackParams;

import org.json.JSONArray;

import java.util.List;

public class TaskCollection extends CollectionModelBase<Task> {
    public TaskCollection(){
        super(null);
    }

    @Override
    protected String getItemsUrlFormat() {
        return "/data/task/private/findall/id/%d/%d";
    }

    @Override
    protected void setupItems(CallbackParams params) {
        List result = (List)params.get("result");
        getItems().clear();
        if(result == null)
            return;
        for(Object tasksObj : result){
            JSONArray tasks = (JSONArray)tasksObj;
            for(int i=0; i<tasks.length(); i++){
                try {
                    Object taskObj = tasks.get(i);
                    Task task = Task.parse(taskObj);
                    if(task == null){
                        System.err.println("[TaskCollection.setupItems] failed to parse task object. skipping.");
                        continue;
                    }
                    getItems().put(task.getId(), task);
                } catch (Exception e) {
                    System.err.println("[TaskCollection.setupItems] an exception occurred. skipping.");
                    e.printStackTrace();
                }
            }
        }
    }
}
