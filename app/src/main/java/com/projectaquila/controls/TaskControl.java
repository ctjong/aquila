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
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/private/" + mTask.getId(), mTask.getDataMap(), null);
                mTask.notifyListeners();
            }
        };
    }

    private Callback getCompleteTaskAction(){
        return new Callback() {
            @Override
            public void execute(CallbackParams params) {
                if(mTask.getRecurrence() == null) {
                    System.out.println("[TaskListItem.getCompleteTaskAction] completing normal task " + mTask.getId());
                    AppContext.getCurrent().getTasks().remove(mTask.getId());
                    AppContext.getCurrent().getDataService().request(ApiTaskMethod.DELETE, "/data/task/private/" + mTask.getId(), null, null);
                }else{
                    System.out.println("[TaskListItem.getCompleteTaskAction] completing recurrence task " + mTask.getId() + " at " + mDate.toDateKey());
                    if(mTask.getDate().toDateKey().equals(mDate.toDateKey())){
                        if(mTask.getRecurrence().shiftToNextOccurrence()){
                            System.out.println("[TaskListItem.getCompleteTaskAction] shifting recurrence series to " + mTask.getDateKey());
                            AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/private/" + mTask.getId(), mTask.getDataMap(), null);
                        }else{
                            System.out.println("[TaskListItem.getCompleteTaskAction] completing recurrence series " + mTask.getId());
                            AppContext.getCurrent().getTasks().remove(mTask.getId());
                            AppContext.getCurrent().getDataService().request(ApiTaskMethod.DELETE, "/data/task/private/" + mTask.getId(), null, null);
                        }
                    }else{
                        mTask.getRecurrence().getHoles().add(mDate.toDateKey());
                        AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/private/" + mTask.getId(), mTask.getDataMap(), null);
                    }
                }
                mTask.notifyListeners();
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
                AppContext.getCurrent().getNavigationService().navigateChild(TaskDetailView.class, navParams);
            }
        };
    }
}
