package com.projectaquila.views;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanItem;
import com.projectaquila.services.HelperService;

public class PlanItemDetailView extends ViewBase {
    private PlanItem mPlanItem;
    private TextView mNameText;
    private TextView mDescText;

    /**
     * Get layout id
     * @return layout id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_planitemdetail;
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
        mPlanItem = (PlanItem)getNavArgObj("planitem");
        System.out.println("[PlanItemDetailView.initializeView] starting. planItemId=" + HelperService.safePrint(mPlanItem.getId()));

        mNameText = (TextView)findViewById(R.id.planitemdetail_name);
        mDescText = (TextView)findViewById(R.id.planitemdetail_desc);
        updateView();

        if(mPlanItem.getParent().getOwnerId().equals(AppContext.getCurrent().getActiveUser().getId())){
            findViewById(R.id.planitemdetail_editbar).setVisibility(View.VISIBLE);
            findViewById(R.id.planitemdetail_edit_btn).setOnClickListener(getEditButtonClickHandler());
            findViewById(R.id.planitemdetail_delete_btn).setOnClickListener(getDeleteButtonClickHandler());
            mPlanItem.addChangedHandler(new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    updateView();
                }
            });
        }
    }

    /**
     * Update the view elements based on current plan item data
     */
    private void updateView(){
        mNameText.setText(mPlanItem.getName());
        mDescText.setText(mPlanItem.getName());
    }

    /**
     * Get click handler for the edit button
     * @return click handler
     */
    private View.OnClickListener getEditButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanItemDetailView.getEditButtonClickHandler] opening edit page");
                AppContext.getCurrent().getNavigationService().navigateChild(PlanItemUpdateView.class, HelperService.getSinglePairMap("planitem", mPlanItem));
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
                System.out.println("[PlanItemDetailView.getDeleteButtonClickHandler] deleting");
                //TODO
            }
        };
    }
}
