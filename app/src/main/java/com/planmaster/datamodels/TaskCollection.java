package com.planmaster.datamodels;

import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.services.HelperService;

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
                    HelperService.logError("[TaskCollection.processServerResponse] failed to parse task object. skipping.");
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
                HelperService.logError("[TaskCollection.processServerResponse] an exception occurred. skipping.");
                e.printStackTrace();
            }
        }
        return list;
    }
}
