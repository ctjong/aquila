package com.projectaquila.views;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.PlanItemComparator;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.controls.PlanItemControl;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.datamodels.PlanItem;
import com.projectaquila.services.HelperService;

import java.util.Collections;

public class PlanDetailView extends ViewBase {
    private Plan mPlan;
    private LinearLayout mItemsList;
    private LinearLayout mItemsParent;
    private TextView mNameText;
    private TextView mDescText;
    private PlanItemComparator mComparator;

    /**
     * Get layout id
     * @return layout id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_plandetail;
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
        mPlan = (Plan)getNavArgObj("plan");
        AppContext.getCurrent().getActivity().showLoadingScreen();
        System.out.println("[PlanDetailView.initializeView] starting. planId=" + HelperService.safePrint(mPlan.getId()));
        mItemsList = (LinearLayout)findViewById(R.id.plandetail_items);
        mItemsParent = (LinearLayout)findViewById(R.id.plandetail_itemsparent);
        mNameText = (TextView)findViewById(R.id.plandetail_name);
        mDescText = (TextView)findViewById(R.id.plandetail_desc);
        mComparator = new PlanItemComparator();

        if(mPlan.getOwnerId().equals(AppContext.getCurrent().getActiveUser().getId())){
            View editBtn = findViewById(R.id.plandetail_edit_btn);
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(getEditButtonClickHandler());
            View deleteBtn = findViewById(R.id.plandetail_delete_btn);
            deleteBtn.setVisibility(View.VISIBLE);
            deleteBtn.setOnClickListener(getDeleteButtonClickHandler());
            mPlan.addChangedHandler(new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    updateView();
                }
            });
        }

        mPlan.load(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateView();
                AppContext.getCurrent().getActivity().showContentScreen();
            }
        });
    }

    /**
     * Update the view elements based on current plan data
     */
    private void updateView(){
        mNameText.setText(mPlan.getName());
        if(mPlan.getDescription() != null && !mPlan.getDescription().equals("")){
            mDescText.setText(mPlan.getDescription());
            mDescText.setVisibility(View.VISIBLE);
        }else{
            mDescText.setVisibility(View.GONE);
        }
        Collections.sort(mPlan.getItems(), mComparator);
        if(mPlan.getItems().size() == 0){
            mItemsParent.setVisibility(View.GONE);
        }else{
            mItemsParent.setVisibility(View.VISIBLE);
            mItemsList.removeAllViews();
            for(PlanItem planItem : mPlan.getItems()){
                mItemsList.addView(new PlanItemControl(planItem, PlanItemDetailView.class));
            }
        }
    }

    /**
     * Get click handler for the edit button
     * @return click handler
     */
    private View.OnClickListener getEditButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanDetailView.getEditButtonClickHandler] going to edit page");
                AppContext.getCurrent().getNavigationService().navigateChild(PlanUpdateView.class, HelperService.getSinglePairMap("plan", mPlan));
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
                System.out.println("[PlanDetailView.getDeleteButtonClickHandler] deleting");
                //TODO
            }
        };
    }
}
