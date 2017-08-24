package com.planmaster.views;

import android.view.View;
import android.widget.EditText;

import com.planmaster.R;
import com.planmaster.contexts.AppContext;
import com.planmaster.datamodels.PlanTask;
import com.planmaster.services.HelperService;

public class PlanTaskUpdateView extends ViewBase {
    private PlanTask mPlanTask;
    private EditText mNameText;
    private EditText mDescText;

    /**
     * Get layout id
     * @return layout id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_plantaskupdate;
    }

    /**
     * Get title bar string id
     * @return title bar string id
     */
    @Override
    protected int getTitleBarStringId(){
        if(mPlanTask.getParent().getId() != null){
            return R.string.planupdate_title;
        }
        return R.string.plancreate_title;
    }

    /**
     * Initialize the view
     */
    @Override
    protected void initializeView(){
        mPlanTask = (PlanTask)getNavArgObj("plantask");
        System.out.println("[PlanTaskUpdateView.initializeView] starting. planTaskId=" + HelperService.safePrint(mPlanTask.getId()));
        mNameText = (EditText)findViewById(R.id.plantaskupdate_name);
        mDescText = (EditText)findViewById(R.id.plantaskupdate_desc);
        mNameText.setText(mPlanTask.getName());
        mDescText.setText(mPlanTask.getDescription());

        // setup event handlers
        View deleteBtn = findViewById(R.id.plantaskupdate_delete_btn);
        deleteBtn.setOnClickListener(getDeleteButtonClickHandler());
        findViewById(R.id.plantaskupdate_done_btn).setOnClickListener(getDoneButtonClickHandler());
        findViewById(R.id.plantaskupdate_cancel_btn).setOnClickListener(getCancelButtonClickHandler());

        // show/hide controls based on plan state
        deleteBtn.setVisibility(mPlanTask.getParent().getState() == 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * Get click handler for the delete button
     * @return click handler
     */
    private View.OnClickListener getDeleteButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanTaskUpdateView.getDeleteButtonClickHandler] deleting. going back.");
                mPlanTask.getParent().remove(mPlanTask);
                AppContext.getCurrent().getActivity().onBackPressed();
                mPlanTask.notifyListeners();
            }
        };
    }

    /**
     * Get click handler for the done button
     * @return click handler
     */
    private View.OnClickListener getDoneButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanTaskUpdateView.getDoneButtonClickHandler] done editing. going back.");
                mPlanTask.setName(mNameText.getText().toString());
                mPlanTask.setDescription(mDescText.getText().toString());
                AppContext.getCurrent().getActivity().onBackPressed();
                mPlanTask.notifyListeners();
            }
        };
    }

    /**
     * Get click handler for the cancel button
     * @return click handler
     */
    private View.OnClickListener getCancelButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanTaskUpdateView.getCancelButtonClickHandler] cancelling. going back.");
                AppContext.getCurrent().getActivity().onBackPressed();
            }
        };
    }
}
