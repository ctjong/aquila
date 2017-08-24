package com.projectaquila.controls;

import android.view.View;
import android.widget.RelativeLayout;
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
        // get UI elements
        View postponeBtn = view.findViewById(R.id.taskcontrol_postpone);
        View completeBtn = view.findViewById(R.id.taskcontrol_complete);
        TextView text = (TextView) view.findViewById(R.id.taskcontrol_text);

        // set up UI elements visibility
        int btnWidth = (int)AppContext.getCurrent().getActivity().getResources().getDimension(R.dimen.taskcontrol_btn_width);
        RelativeLayout.LayoutParams textLayout = (RelativeLayout.LayoutParams)text.getLayoutParams();
        if(mTask == null || mTask.getRecurrence() != null){
            postponeBtn.setVisibility(View.GONE);
            if(!mPlanTask.isReadyToComplete(mEnrollment)) {
                completeBtn.setVisibility(View.GONE);
                textLayout.setMargins(0, 0, 0, 0);
            }else{
                completeBtn.setVisibility(View.VISIBLE);
                textLayout.setMargins(0, 0, btnWidth, 0);
            }
        }else{
            postponeBtn.setVisibility(View.VISIBLE);
            textLayout.setMargins(0, 0, btnWidth * 2, 0);
        }

        // set up click handlers
        postponeBtn.setOnClickListener(getPostponeClickHandler());
        completeBtn.setOnClickListener(getCompleteClickHandler());
        text.setOnClickListener(getOpenTaskClickHandler());

        // show task text
        if(mTask != null) {
            text.setText(mTask.getName());
        }else{
            String str = AppContext.getCurrent().getActivity().getString(R.string.taskcontrol_plantask);
            str = str.replace("{planId}", mPlanTask.getParent().getId()).replace("{planTaskName}", mPlanTask.getName());
            text.setText(str);
        }

        // set the background color
        View slider = view.findViewById(R.id.taskcontrol_slider);
        if (mPlanTask != null) {
            slider.setBackgroundResource(R.drawable.taskcontrol_plantask);
        }else if (mTask.getRecurrence() == null) {
            slider.setBackgroundResource(R.drawable.taskcontrol_normal);
        } else {
            slider.setBackgroundResource(R.drawable.taskcontrol_recurrence);
        }
    }

    /**
     * Get handler for postpone action
     * @return handler callback
     */
    private View.OnClickListener getPostponeClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperService.showAlert(R.string.prompt_postponetask_title, R.string.prompt_postponetask_msg, new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        if(mTask == null) {
                            HelperService.logError("[TaskListItem.getPostponeClickHandler] postponing a plan task is unsupported. aborting.");
                            return;
                        }
                        System.out.println("[TaskListItem.getPostponeClickHandler] postponing task " + mTask.getId());
                        if(mTask.getRecurrence() != null){
                            HelperService.logError("[TaskListItem.getPostponeClickHandler] task is recurred. aborting.");
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
    private View.OnClickListener getCompleteClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int promptTitle = (mTask != null && mTask.getRecurrence() != null) ? R.string.prompt_completeocc_title : R.string.prompt_completetask_title;
                int promptMsg = (mTask != null && mTask.getRecurrence() != null) ? R.string.prompt_completeocc_msg : R.string.prompt_completetask_msg;
                HelperService.showAlert(promptTitle, promptMsg, new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        if(mTask == null) {
                            System.out.println("[TaskListItem.getCompleteClickHandler] completing plan task " + mPlanTask.getId());
                            mEnrollment.setCompletedDays(mPlanTask.getDay());
                            mEnrollment.submitUpdate(null);
                        }else {
                            System.out.println("[TaskListItem.getCompleteClickHandler] completing task " + mTask.getId());
                            if (mTask.getRecurrence() == null) {
                                mTask.complete(null);
                            } else {
                                mTask.completeOccurrence(mDate, null);
                            }
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
    private View.OnClickListener getOpenTaskClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTask != null) {
                    System.out.println("[TaskListItem.getOpenTaskClickHandler] opening task " + mTask.getId());
                    HashMap<String, Object> navParams = new HashMap<>();
                    navParams.put("task", mTask);
                    navParams.put("activedatekey", mDate.toDateKey());
                    AppContext.getCurrent().getNavigationService().navigateChild(TaskDetailView.class, navParams);
                }else{
                    System.out.println("[TaskListItem.getOpenTaskClickHandler] opening plan task " + mPlanTask.getId());
                    HashMap<String, Object> navParams = new HashMap<>();
                    navParams.put("plantask", mPlanTask);
                    navParams.put("enrollment", mEnrollment);
                    AppContext.getCurrent().getNavigationService().navigateChild(PlanTaskDetailView.class, navParams);
                }
            }
        };
    }
}
