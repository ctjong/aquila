package com.projectaquila.controls;

import android.view.View;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.models.Task;
import com.projectaquila.models.TaskDate;
import com.projectaquila.views.TaskDetailView;

import java.util.HashMap;

public class TaskControl {
    private Task mTask;

    public TaskControl(Task task){
        mTask = task;
    }

    public Task getTask(){
        return mTask;
    }

    public View renderView(View view){
        TextView text = (TextView) view.findViewById(R.id.taskcontrol_text);
        text.setText(mTask.getName());

        Callback completeTaskAction = getCompleteTaskAction();
        Callback postponeTaskAction = getPostponeTaskAction();
        Callback openTaskAction = getOpenTaskAction();
        View slider = view.findViewById(R.id.taskcontrol_slider);
        SwipeListener.listen(slider, slider, completeTaskAction, postponeTaskAction, openTaskAction);
        return view;
    }

    private Callback getPostponeTaskAction(){
        return new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                System.out.println("[TaskListItem.getPostponeTaskAction] postponing task " + mTask.getId());
                TaskDate postponedDate = mTask.getDate().getModified(1);
                mTask.setDate(postponedDate);
                HashMap<String, String> data = new HashMap<>();
                data.put("taskdate", postponedDate.toDateKey());
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/private/" + mTask.getId(), data, null);
                mTask.notifyListeners();
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
                mTask.notifyListeners();
            }
        };
    }

    private Callback getOpenTaskAction(){
        return new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                System.out.println("[TaskListItem.getOpenTaskAction] opening task " + mTask.getId());
                HashMap<String, String> navParams = new HashMap<>();
                navParams.put("id", mTask.getId());
                AppContext.getCurrent().getNavigationService().navigateChild(TaskDetailView.class, navParams);
            }
        };
    }
}
