package com.projectaquila.views;

import android.view.View;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.Plan;
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

        if(mEnrollment == null) {
            // initialize buttons if this is not in enrollment view mode, parent plan is in draft mode, and the user is the parent plan creator
            Plan parent = mPlanTask.getParent();
            if (parent.getState() == 0 && parent.getOwnerId().equals(AppContext.getCurrent().getActiveUser().getId())) {
                findViewById(R.id.plantaskdetail_editbar).setVisibility(View.VISIBLE);
                findViewById(R.id.plantaskdetail_edit_btn).setOnClickListener(getEditButtonClickHandler());
                findViewById(R.id.plantaskdetail_delete_btn).setOnClickListener(getDeleteButtonClickHandler());
                mPlanTask.addChangedHandler(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        updateView();
                    }
                });
            }
        }else{
            // for enrollment view mode, we want to show enrollment details, completion buttons, and link to parent plan
            //TODO
        }

        // initialize view
        updateView();
    }

    /**
     * Update the view elements based on current plan task data
     */
    private void updateView(){
        mNameText.setText(mPlanTask.getName());
        mDescText.setText(mPlanTask.getName());
    }

    /**
     * Get click handler for the edit button
     * @return click handler
     */
    private View.OnClickListener getEditButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanTaskDetailView.getEditButtonClickHandler] opening edit page");
                AppContext.getCurrent().getNavigationService().navigateChild(PlanTaskUpdateView.class, HelperService.getSinglePairMap("plantask", mPlanTask));
            }
        };
    }

    /**
     * Get click handler for the delete button
     * @return click handler
     */
    private View.OnClickListener getDeleteButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanTaskDetailView.getDeleteButtonClickHandler] deleting");
                //TODO
            }
        };
    }
}
