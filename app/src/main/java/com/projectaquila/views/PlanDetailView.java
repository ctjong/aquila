package com.projectaquila.views;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.PlanTaskComparator;
import com.projectaquila.common.TaskDate;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.controls.PlanTaskControl;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.datamodels.PlanEnrollment;
import com.projectaquila.datamodels.PlanTask;
import com.projectaquila.services.HelperService;

import java.util.Collections;

public class PlanDetailView extends ViewBase {
    private Plan mPlan;
    private LinearLayout mItemsList;
    private LinearLayout mItemsParent;
    private TextView mNameText;
    private TextView mDescText;
    private PlanTaskComparator mComparator;
    private View mEnrollBtn;
    private View mUnenrollBtn;

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
        AppContext.getCurrent().getActivity().showLoadingScreen();
        mPlan = (Plan)getNavArgObj("plan");
        System.out.println("[PlanDetailView.initializeView] starting. planId=" + HelperService.safePrint(mPlan.getId()));
        mItemsList = (LinearLayout)findViewById(R.id.plandetail_items);
        mItemsParent = (LinearLayout)findViewById(R.id.plandetail_itemsparent);
        mNameText = (TextView)findViewById(R.id.plandetail_name);
        mDescText = (TextView)findViewById(R.id.plandetail_desc);
        mComparator = new PlanTaskComparator();
        mEnrollBtn = findViewById(R.id.plandetail_enroll_btn);
        mUnenrollBtn = findViewById(R.id.plandetail_unenroll_btn);
        mEnrollBtn.setOnClickListener(getEnrollButtonClickHandler());
        mUnenrollBtn.setOnClickListener(getUnenrollButtonClickHandler());

        if (mPlan.getOwnerId().equals(AppContext.getCurrent().getActiveUser().getId())) {
            findViewById(R.id.plandetail_ownerbtns).setVisibility(View.VISIBLE);
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
        if (AppContext.getCurrent().getEnrollments().containsPlan(mPlan.getId())) {
            mEnrollBtn.setVisibility(View.GONE);
            mUnenrollBtn.setVisibility(View.VISIBLE);
        } else {
            mEnrollBtn.setVisibility(View.VISIBLE);
            mUnenrollBtn.setVisibility(View.GONE);
        }

        mPlan.load(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateView();
                AppContext.getCurrent().getActivity().hideLoadingScreen();
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
            for(PlanTask planTask : mPlan.getItems()){
                mItemsList.addView(new PlanTaskControl(planTask, PlanTaskDetailView.class));
            }
        }
    }

    /**
     * Get click handler for the un-enroll button
     * @return click handler
     */
    private View.OnClickListener getUnenrollButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanDetailView.getUnenrollButtonClickHandler] un-enrolling plan " + mPlan.getId());
                PlanEnrollment enrollment = null;
                for(final PlanEnrollment e : AppContext.getCurrent().getEnrollments().getItems()){
                    if(e.getPlan().getId().equals(mPlan.getId())) {
                        enrollment = e;
                        break;
                    }
                }
                if(enrollment == null){
                    System.out.println("[PlanDetailView.getUnenrollButtonClickHandler] failed to un-enroll, enrollment not found");
                    return;
                }
                AppContext.getCurrent().getActivity().showLoadingScreen();
                final PlanEnrollment toBeRemoved = enrollment;
                enrollment.submitDelete(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        mEnrollBtn.setVisibility(View.VISIBLE);
                        mUnenrollBtn.setVisibility(View.GONE);
                        AppContext.getCurrent().getEnrollments().getItems().remove(toBeRemoved);
                        AppContext.getCurrent().getEnrollments().notifyListeners();
                        AppContext.getCurrent().getActivity().hideLoadingScreen();
                    }
                });
            }
        };
    }

    /**
     * Get click handler for the enroll button
     * @return click handler
     */
    private View.OnClickListener getEnrollButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanDetailView.getEnrollButtonClickHandler] enrolling plan " + mPlan.getId());
                AppContext.getCurrent().getActivity().showLoadingScreen();
                final PlanEnrollment enrollment = new PlanEnrollment(null, mPlan, (new TaskDate()).toDateKey(), 0, null);
                enrollment.submitUpdate(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        mEnrollBtn.setVisibility(View.GONE);
                        mUnenrollBtn.setVisibility(View.VISIBLE);
                        AppContext.getCurrent().getEnrollments().getItems().add(enrollment);
                        AppContext.getCurrent().getEnrollments().notifyListeners();
                        AppContext.getCurrent().getActivity().hideLoadingScreen();
                    }
                });
            }
        };
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
