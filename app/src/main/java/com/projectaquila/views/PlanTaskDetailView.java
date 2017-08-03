package com.projectaquila.views;

import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.datamodels.PlanEnrollment;
import com.projectaquila.datamodels.PlanTask;
import com.projectaquila.services.HelperService;

public class PlanTaskDetailView extends ViewBase {
    private PlanTask mPlanTask;
    private PlanEnrollment mEnrollment;
    private TextView mNameText;
    private TextView mDescText;

    /**
     * Get layout id
     * @return layout id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_plantaskdetail;
    }

    /**
     * Get title bar string id
     * @return title bar string id
     */
    @Override
    protected int getTitleBarStringId(){
        return R.string.plandetail_title;
    }

    /**
     * Initialize the view
     */
    @Override
    protected void initializeView(){
        mPlanTask = (PlanTask)getNavArgObj("plantask");
        Object enrollmentObj = getNavArgObj("enrollment");
        mEnrollment = enrollmentObj == null ? null : (PlanEnrollment)enrollmentObj;
        System.out.println("[PlanTaskDetailView.initializeView] starting. planTaskId=" + HelperService.safePrint(mPlanTask.getId()));

        // get UI elements
        mNameText = (TextView)findViewById(R.id.plantaskdetail_name);
        mDescText = (TextView)findViewById(R.id.plantaskdetail_desc);

        // setup event handlers
        mPlanTask.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateView();
            }
        });

        // initialize view
        updateView();
    }

    /**
     * Update the view elements based on current plan task data
     */
    private void updateView(){
        // for enrollment view mode, we want to show enrollment details, completion buttons, and link to parent plan
        if(mEnrollment != null) {
            //TODO
        }

        // update name and description
        mNameText.setText(mPlanTask.getName());
        mDescText.setText(mPlanTask.getName());
    }
}
