package com.projectaquila.views;

import android.view.View;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.datamodels.Plan;

public class PlanUpdateView extends ViewBase {
    private Plan mPlan;

    /**
     * Get layout id
     * @return layout id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_planupdate;
    }

    /**
     * Get title bar string id
     * @return title bar string id
     */
    @Override
    protected int getTitleBarStringId(){
        if(getNavArg("id") != null){
            return R.string.planupdate_title;
        }
        return R.string.plancreate_title;
    }

    /**
     * Initialize the view
     */
    @Override
    protected void initializeView(){
        String planId = getNavArg("id");
        if(planId == null) {
            System.out.println("[PlanUpdateView.initializeView] mode=create");
            mPlan = new Plan(null, AppContext.getCurrent().getActiveUser().getId(), false, null, null, null);
        }else{
            System.out.println("[PlanUpdateView.initializeView] mode=update");
        }

        findViewById(R.id.planupdate_save_btn).setOnClickListener(getSaveButtonClickHandler());
        findViewById(R.id.planupdate_cancel_btn).setOnClickListener(getCancelButtonClickHandler());
        AppContext.getCurrent().getActivity().showContentScreen();
    }

    /**
     * Get click handler for the save button
     * @return click handler
     */
    private View.OnClickListener getSaveButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanUpdateView.getSaveButtonClickHandler] saving");
                //TODO
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
                System.out.println("[PlanUpdateView.getCancelButtonClickHandler] cancelling");
                AppContext.getCurrent().getNavigationService().goBack();
            }
        };
    }
}
