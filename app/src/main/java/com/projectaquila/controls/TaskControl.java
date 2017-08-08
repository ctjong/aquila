package com.projectaquila.controls;

import android.view.View;
import android.widget.TextView;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.datamodels.DataModelBase;
import com.projectaquila.datamodels.PlanEnrollment;
import com.projectaquila.datamodels.PlanTask;
import com.projectaquila.datamodels.Task;
import com.projectaquila.common.TaskDate;
import com.projectaquila.services.HelperService;
import com.projectaquila.views.PlanTaskDetailView;
import com.projectaquila.views.TaskDetailView;

import java.util.HashMap;

/**
 * A control to view task data
 */
public class TaskControl {
    // variables to store non-plan task data
    private Task mTask;
    private TaskDate mDate;

    // variables to store plan task data
    private PlanTask mPlanTask;
    private PlanEnrollment mEnrollment;

    /**
     * Instantiate a control to view non-plan task data
     * @param task task data
     * @param date active date
     */
    public TaskControl(Task task, TaskDate date){
        mTask = task;
        mDate = date;
    }

    /**
     * Instantiate a control to view plan task data
     * @param planTask plan task
     * @param enrollment plan enrollment
     */
    public TaskControl(PlanTask planTask, PlanEnrollment enrollment){
        mPlanTask = planTask;
        mEnrollment = enrollment;
    }

    /**
     * Get the data that is being viewed by this control
     * @return data object
     */
    public DataModelBase getData(){
        return mTask != null ? mTask : mPlanTask;
    }

    /**
     * Render the control on the given view
     * @param view view object
     */
    public void renderView(View view){
        TextView text = (TextView) view.findViewById(R.id.taskcontrol_text);
        Callback openTaskAction = getOpenTaskAction();
        Callback completeTaskAction = getCompleteTaskAction();
        Callback postponeTaskAction = getPostponeTaskAction();
        View slider = view.findViewById(R.id.taskcontrol_slider);

        // render the text shown on the control
        if(mTask != null) {
            text.setText(mTask.getName());
        }else{
            text.setText(mPlanTask.getName());
        }

        // set the background color
        if (mPlanTask != null) {
            slider.setBackgroundResource(R.drawable.taskcontrol_plantask);
        }else if (mTask.getRecurrence() == null) {
            slider.setBackgroundResource(R.drawable.taskcontrol_normal);
        } else {
            slider.setBackgroundResource(R.drawable.taskcontrol_recurrence);
        }

        // setup swipe/click handlers
        if (mTask != null) {
            if(mTask.getRecurrence() == null) {
                // non-plan task, non recurred
                SwipeListener.listen(slider, slider, completeTaskAction, postponeTaskAction, openTaskAction);
            }else{
                // non-plan task, recurred
                SwipeListener.listen(slider, slider, completeTaskAction, null, openTaskAction);
            }
        } else {
            // plan task
            SwipeListener.listen(slider, slider, null, null, openTaskAction);
        }
    }

    /**
     * Get handler for postpone action
     * @return handler callback
     */
    private Callback getPostponeTaskAction(){
        return new Callback() {
            @Override
            public void execute(CallbackParams params) {
                HelperService.showAlert(R.string.prompt_postponetask_title, R.string.prompt_postponetask_msg, new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        if(mTask == null) {
                            System.err.println("[TaskListItem.getPostponeTaskAction] postponing a plan task is unsupported. aborting.");
                            return;
                        }
                        System.out.println("[TaskListItem.getPostponeTaskAction] postponing task " + mTask.getId());
                        if(mTask.getRecurrence() != null){
                            System.err.println("[TaskListItem.getPostponeTaskAction] task is recurred. aborting.");
                            return;
                        }
                        TaskDate postponedDate = mTask.getDate().getModified(1);
                        mTask.setDate(postponedDate);
                        mTask.submitUpdate(null);
                        mTask.notifyListeners();
                    }
                }, null);
            }
        };
    }

    /**
     * Get handler for complete action
     * @return handler callback
     */
    private Callback getCompleteTaskAction(){
        return new Callback() {
            @Override
            public void execute(CallbackParams params) {
                int promptTitle = (mTask != null && mTask.getRecurrence() != null) ? R.string.prompt_completeocc_title : R.string.prompt_completetask_title;
                int promptMsg = (mTask != null && mTask.getRecurrence() != null) ? R.string.prompt_completeocc_msg : R.string.prompt_completetask_msg;
                HelperService.showAlert(promptTitle, promptMsg, new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        if(mTask == null) {
                            System.err.println("[TaskListItem.getCompleteTaskAction] task=null. abort.");
                            return;
                        }
                        System.out.println("[TaskListItem.getCompleteTaskAction] completing task " + mTask.getId());
                        if (mTask.getRecurrence() == null) {
                            mTask.complete(null);
                        } else {
                            mTask.completeOccurrence(mDate, null);
                        }
                    }
                }, null);
            }
        };
    }

    /**
     * Get handler for open task action
     * @return handler callback
     */
    private Callback getOpenTaskAction(){
        return new Callback() {
            @Override
            public void execute(CallbackParams params) {
                if(mTask != null) {
                    System.out.println("[TaskListItem.getOpenTaskAction] opening task " + mTask.getId());
                    HashMap<String, Object> navParams = new HashMap<>();
                    navParams.put("task", mTask);
                    navParams.put("activedatekey", mDate.toDateKey());
                    AppContext.getCurrent().getNavigationService().navigateChild(TaskDetailView.class, navParams);
                }else{
                    System.out.println("[TaskListItem.getOpenTaskAction] opening plan task " + mPlanTask.getId());
                    HashMap<String, Object> navParams = new HashMap<>();
                    navParams.put("plantask", mPlanTask);
                    navParams.put("enrollment", mEnrollment);
                    AppContext.getCurrent().getNavigationService().navigateChild(PlanTaskDetailView.class, navParams);
                }
            }
        };
    }
}
