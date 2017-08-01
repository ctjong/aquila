package com.projectaquila.views;

import android.view.View;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanTask;
import com.projectaquila.services.HelperService;

public class PlanTaskDetailView extends ViewBase {
    private PlanTask mPlanTask;
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
        System.out.println("[PlanTaskDetailView.initializeView] starting. planTaskId=" + HelperService.safePrint(mPlanTask.getId()));

        mNameText = (TextView)findViewById(R.id.plantaskdetail_name);
        mDescText = (TextView)findViewById(R.id.plantaskdetail_desc);
        updateView();

        if(mPlanTask.getParent().getOwnerId().equals(AppContext.getCurrent().getActiveUser().getId())){
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
