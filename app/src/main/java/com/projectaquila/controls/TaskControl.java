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

import java.util.HashMap;

public class TaskControl {
    private Task mTask;
    private Event mDeleteEvent;

    public TaskControl(Task task){
        mTask = task;
        mDeleteEvent = new Event();
    }

    public Task getTask(){
        return mTask;
    }

    public View renderView(View view){
        TextView text = (TextView) view.findViewById(R.id.control_tasklistitem_text);
        text.setText(mTask.getName());

        Callback completeTaskAction = getCompleteTaskAction();
        Callback openTaskAction = getOpenTaskAction();
        View slider = view.findViewById(R.id.control_tasklistitem_slider);
        SwipeListener.listen(slider, slider, completeTaskAction, completeTaskAction, openTaskAction);
        return view;
    }

    public void addDeleteHandler(Callback cb){
        mDeleteEvent.addHandler(cb);
    }

    private Callback getCompleteTaskAction(){
        return new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                System.out.println("[TaskListItem.getCompleteTaskAction] completing task " + mTask.getId());
                HashMap<String, String> data = new HashMap<>();
                data.put("iscompleted", "1");
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/private/" + mTask.getId(), data, null);
                // update UI without waiting for API request, for seamless UI response.
                mDeleteEvent.invoke(null);
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
