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
    private PlanEnrollment mEnrollment;
    private LinearLayout mItemsList;
    private LinearLayout mItemsParent;
    private TextView mPrivateText;
    private TextView mDraftText;
    private TextView mNameText;
    private TextView mDescText;
    private LinearLayout mDescParent;
    private PlanTaskComparator mComparator;
    private View mEnrollBtn;
    private View mUnenrollBtn;
    private View mSubmitBtn;
    private View mPublishBtn;
    private View mEditBtn;
    private View mDeleteBtn;

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
        Object enrollmentObj = getNavArgObj("enrollment");
        mEnrollment = enrollmentObj == null ? null : (PlanEnrollment)enrollmentObj;
        System.out.println("[PlanDetailView.initializeView] starting. planId=" + HelperService.safePrint(mPlan.getId()));

        // get UI elements
        mItemsList = (LinearLayout)findViewById(R.id.plandetail_items);
        mItemsParent = (LinearLayout)findViewById(R.id.plandetail_itemsparent);
        mPrivateText = (TextView)findViewById(R.id.plandetail_private_label);
        mDraftText = (TextView)findViewById(R.id.plandetail_draft_label);
        mNameText = (TextView)findViewById(R.id.plandetail_name);
        mDescText = (TextView)findViewById(R.id.plandetail_desc);
        mDescParent = (LinearLayout)findViewById(R.id.plandetail_descparent);
        mComparator = new PlanTaskComparator();
        mEnrollBtn = findViewById(R.id.plandetail_enroll_btn);
        mUnenrollBtn = findViewById(R.id.plandetail_unenroll_btn);
        mEditBtn = findViewById(R.id.plandetail_edit_btn);
        mDeleteBtn= findViewById(R.id.plandetail_delete_btn);
        mSubmitBtn = findViewById(R.id.plandetail_submit_btn);
        mPublishBtn = findViewById(R.id.plandetail_publish_btn);

        // setup event handlers
        mEditBtn.setOnClickListener(getEditButtonClickHandler());
        mDeleteBtn.setOnClickListener(getDeleteButtonClickHandler());
        mSubmitBtn.setOnClickListener(getSubmitButtonClickHandler());
        mPublishBtn.setOnClickListener(getPublishButtonClickHandler());
        mUnenrollBtn.setOnClickListener(getUnenrollButtonClickHandler());
        mEnrollBtn.setOnClickListener(getEnrollButtonClickHandler());
        mPlan.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateView();
            }
        });

        // load plan tasks and update view after that
        mPlan.loadItems(new Callback() {
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
        // initialize buttons if this is not in enrollment view mode
        mEditBtn.setVisibility(View.GONE);
        mDeleteBtn.setVisibility(View.GONE);
        mSubmitBtn.setVisibility(View.GONE);
        mPublishBtn.setVisibility(View.GONE);
        mUnenrollBtn.setVisibility(View.GONE);
        mEnrollBtn.setVisibility(View.GONE);
        if(mEnrollment == null) {
            if (mPlan.getCreator().getId().equals(AppContext.getCurrent().getActiveUser().getId())) {
                mEditBtn.setVisibility(View.VISIBLE);
                if (mPlan.getState() == 0) {
                    mDeleteBtn.setVisibility(View.VISIBLE);
                    mSubmitBtn.setVisibility(View.VISIBLE);
                } else if (mPlan.getState() == 1) {
                    mPublishBtn.setVisibility(View.VISIBLE);
                }
            }
            if (mPlan.getState() > 0) {
                if (AppContext.getCurrent().getEnrollments().containsPlan(mPlan.getId())) {
                    mUnenrollBtn.setVisibility(View.VISIBLE);
                } else {
                    mEnrollBtn.setVisibility(View.VISIBLE);
                }
            }
        }

        // update state labels
        mDraftText.setVisibility(mPlan.getState() == 0 ? View.VISIBLE : View.GONE);
        mPrivateText.setVisibility(mPlan.getState() == 1 ? View.VISIBLE : View.GONE);

        // update name and descriptions
        mNameText.setText(mPlan.getName());
        if(mPlan.getDescription() != null && !mPlan.getDescription().equals("")){
            mDescText.setText(mPlan.getDescription());
            mDescParent.setVisibility(View.VISIBLE);
        }else{
            mDescParent.setVisibility(View.GONE);
        }

        // update plan tasks
        if(mPlan.getItems().size() == 0){
            mItemsParent.setVisibility(View.GONE);
        }else{
            Collections.sort(mPlan.getItems(), mComparator);
            mItemsParent.setVisibility(View.VISIBLE);
            mItemsList.removeAllViews();
            for(PlanTask planTask : mPlan.getItems()){
                mItemsList.addView(PlanTaskControl.create(planTask, mEnrollment, false));
            }
        }
    }

    /**
     * Get click handler for the submit button
     * @return click handler
     */
    private View.OnClickListener getSubmitButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperService.showAlert(R.string.prompt_submitplan_title, R.string.prompt_submitplan_msg, new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        System.out.println("[PlanDetailView.getSubmitButtonClickHandler] submitting plan " + mPlan.getId());
                        mPlan.setState(1);
                        AppContext.getCurrent().getActivity().showLoadingScreen();
                        mPlan.submitUpdate(new Callback() {
                            @Override
                            public void execute(CallbackParams params) {
                                mPlan.notifyListeners();
                                AppContext.getCurrent().getActivity().hideLoadingScreen();
                            }
                        });
                    }
                }, null);
            }
        };
    }

    /**
     * Get click handler for the publish button
     * @return click handler
     */
    private View.OnClickListener getPublishButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperService.showAlert(R.string.prompt_publishplan_title, R.string.prompt_publishplan_msg, new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        System.out.println("[PlanDetailView.getPublishButtonClickHandler] publishing plan " + mPlan.getId());
                        mPlan.setState(2);
                        AppContext.getCurrent().getActivity().showLoadingScreen();
                        mPlan.submitUpdate(new Callback() {
                            @Override
                            public void execute(CallbackParams params) {
                                mPlan.notifyListeners();
                                AppContext.getCurrent().getActivity().hideLoadingScreen();
                            }
                        });
                    }
                }, null);
            }
        };
    }

    /**
     * Get click handler for the un-enroll button
     * @return click handler
     */
    private View.OnClickListener getUnenrollButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperService.showAlert(R.string.prompt_unenrollplan_title, R.string.prompt_unenrollplan_msg, new Callback(){
                    @Override
                    public void execute(CallbackParams params) {
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
                }, null);
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
                HelperService.showAlert(R.string.prompt_enrollplan_title, R.string.prompt_enrollplan_msg, new Callback(){
                    @Override
                    public void execute(CallbackParams params) {
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
                }, null);
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
                HelperService.showAlert(R.string.prompt_deleteplan_title, R.string.prompt_deleteplan_msg, new Callback(){
                    @Override
                    public void execute(CallbackParams params) {
                        System.out.println("[PlanDetailView.getDeleteButtonClickHandler] deleting");
                        //TODO
                    }
                }, null);
            }
        };
    }
}
