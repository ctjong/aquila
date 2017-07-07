package com.projectaquila.controls;

import android.view.View;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.Callback;
import com.projectaquila.models.CallbackParams;
import com.projectaquila.models.Task;
import com.projectaquila.models.TaskDate;
import com.projectaquila.views.TaskDetailView;

import java.util.HashMap;

public class TaskControl {
    private Task mTask;
    private TaskDate mDate;

    public TaskControl(Task task, TaskDate date){
        mTask = task;
        mDate = date;
    }

    public Task getTask(){
        return mTask;
    }

    public void renderView(View view){
        TextView text = (TextView) view.findViewById(R.id.taskcontrol_text);
        text.setText(mTask.getName());

        Callback openTaskAction = getOpenTaskAction();
        View slider = view.findViewById(R.id.taskcontrol_slider);
        if(mTask.getRecurrence() == null){
            slider.setBackgroundResource(R.drawable.taskcontrol_normal);
        }else{
            slider.setBackgroundResource(R.drawable.taskcontrol_recurrence);
        }
        Callback completeTaskAction = getCompleteTaskAction();
        if(mTask.getRecurrence() == null) {
            Callback postponeTaskAction = getPostponeTaskAction();
            SwipeListener.listen(slider, slider, completeTaskAction, postponeTaskAction, openTaskAction);
        }else{
            SwipeListener.listen(slider, slider, completeTaskAction, null, openTaskAction);
        }
    }

    private Callback getPostponeTaskAction(){
        return new Callback() {
            @Override
            public void execute(CallbackParams params) {
                System.out.println("[TaskListItem.getPostponeTaskAction] postponing task " + mTask.getId());
                TaskDate postponedDate = mTask.getDate().getModified(1);
                mTask.setDate(postponedDate);
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/public/" + mTask.getId(), mTask.getDataMap(), null);
                mTask.notifyListeners();
            }
        };
    }

    private Callback getCompleteTaskAction(){
        return new Callback() {
            @Override
            public void execute(CallbackParams params) {
                System.out.println("[TaskListItem.getCompleteTaskAction] completing task " + mTask.getId());
                if(mTask.getRecurrence() == null) {
                    mTask.complete(null);
                }else{
                    mTask.completeOccurrence(mDate, null);
                }
            }
        };
    }

    private Callback getOpenTaskAction(){
        return new Callback() {
            @Override
            public void execute(CallbackParams params) {
                System.out.println("[TaskListItem.getOpenTaskAction] opening task " + mTask.getId());
                HashMap<String, String> navParams = new HashMap<>();
                navParams.put("id", mTask.getId());
                navParams.put("activedatekey", mDate.toDateKey());
                AppContext.getCurrent().getNavigationService().navigateChild(TaskDetailView.class, navParams);
            }
        };
    }
}
