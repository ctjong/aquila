package com.projectaquila.views;

import android.view.View;
import android.widget.EditText;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanTask;
import com.projectaquila.services.HelperService;

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
        findViewById(R.id.plantaskupdate_save_btn).setOnClickListener(getSaveButtonClickHandler());
        findViewById(R.id.plantaskupdate_cancel_btn).setOnClickListener(getCancelButtonClickHandler());
    }

    /**
     * Get click handler for the save button
     * @return click handler
     */
    private View.OnClickListener getSaveButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanTaskUpdateView.getSaveButtonClickHandler] saving");
                mPlanTask.setName(mNameText.getText().toString());
                mPlanTask.setDescription(mDescText.getText().toString());
                AppContext.getCurrent().getNavigationService().goBack();
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
                System.out.println("[PlanTaskUpdateView.getCancelButtonClickHandler] cancelling");
                AppContext.getCurrent().getNavigationService().goBack();
            }
        };
    }
}
