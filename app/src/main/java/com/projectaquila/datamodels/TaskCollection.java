package com.projectaquila.datamodels;

import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class TaskCollection extends CollectionModelBase<Task> {
    public TaskCollection(){
        super(null);
    }

    @Override
    protected String getItemsUrlFormat() {
        return "/data/task/private/findall/id/{skip}/{take}";
    }

    @Override
    protected List<Task> processServerResponse(CallbackParams params) {
        List<Task> list = new ArrayList<>();
        JSONArray items = params.getApiResult().getItems();
        for(int i=0; i<items.length(); i++){
            try {
                Object taskObj = items.get(i);
                final Task task = Task.parse(taskObj);
                if(task == null){
                    System.err.println("[TaskCollection.processServerResponse] failed to parse task object. skipping.");
                    continue;
                }
                task.addChangedHandler(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        if(task.isDeleted()){
                            getItems().remove(task);
                        }
                    }
                });
                list.add(task);
            } catch (Exception e) {
                System.err.println("[TaskCollection.processServerResponse] an exception occurred. skipping.");
                e.printStackTrace();
            }
        }
        return list;
    }
}
