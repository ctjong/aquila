package com.projectaquila.controls;

import android.view.View;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.Callback;
import com.projectaquila.models.Event;
import com.projectaquila.models.S;
import com.projectaquila.models.Task;
import com.projectaquila.services.HelperService;

import java.util.Date;
import java.util.HashMap;

public class TaskControl {
    private Task mTask;
    private Event mChangedEvent;

    public TaskControl(Task task){
        mTask = task;
        mChangedEvent = new Event();
    }

    public Task getTask(){
        return mTask;
    }

    public View renderView(View view){
        TextView text = (TextView) view.findViewById(R.id.control_tasklistitem_text);
        text.setText(mTask.getName());

        Callback completeTaskAction = getCompleteTaskAction();
        Callback postponeTaskAction = getPostponeTaskAction();
        Callback openTaskAction = getOpenTaskAction();
        View slider = view.findViewById(R.id.control_tasklistitem_slider);
        SwipeListener.listen(slider, slider, completeTaskAction, postponeTaskAction, openTaskAction);
        return view;
    }

    public void addChangedHandler(Callback cb){
        mChangedEvent.addHandler(cb);
    }

    private Callback getPostponeTaskAction(){
        return new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                System.out.println("[TaskListItem.getPostponeTaskAction] postponing task " + mTask.getId());
                Date postponedDate = HelperService.getModifiedDate(mTask.getDate(), 1);
                mTask.setDate(postponedDate);
                HashMap<String, String> data = new HashMap<>();
                data.put("taskdate", HelperService.getDateKey(postponedDate));
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/private/" + mTask.getId(), data, null);
                // update UI without waiting for API request, for seamless UI response.
                mChangedEvent.invoke(null);
            }
        };
    }

    private Callback getCompleteTaskAction(){
        return new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                System.out.println("[TaskListItem.getCompleteTaskAction] completing task " + mTask.getId());
                mTask.setCompletedState(true);
                HashMap<String, String> data = new HashMap<>();
                data.put("iscompleted", "1");
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/private/" + mTask.getId(), data, null);
                // update UI without waiting for API request, for seamless UI response.
                mChangedEvent.invoke(null);
            }
        };
    }

    private Callback getOpenTaskAction(){
        return new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                System.out.println("[TaskListItem.getOpenTaskAction] opening task " + mTask.getId());
                //TODO
            }
        };
    }
}
