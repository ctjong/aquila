package com.planmaster.views;

import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.planmaster.R;
import com.planmaster.activities.ShellActivity;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.common.PlanTaskComparator;
import com.planmaster.common.TaskDate;
import com.planmaster.contexts.AppContext;
import com.planmaster.controls.EnrollmentProgressControl;
import com.planmaster.controls.PlanTaskControl;
import com.planmaster.datamodels.Plan;
import com.planmaster.datamodels.PlanEnrollment;
import com.planmaster.datamodels.PlanTask;
import com.planmaster.services.HelperService;

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
    private EnrollmentProgressControl mProgressControl;

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
        final ShellActivity ctx = AppContext.getCurrent().getActivity();
        ctx.showLoadingScreen();
        mPlan = (Plan)getNavArgObj("plan");
        Object enrollmentObj = getNavArgObj("enrollment");
        boolean disableUserLink = getNavArgObj("disableUserLink") != null && (boolean)getNavArgObj("disableUserLink");
        if(enrollmentObj != null){
            mEnrollment = (PlanEnrollment) enrollmentObj;
        } else {
            for(PlanEnrollment e : AppContext.getCurrent().getEnrollments().getItems()){
                if(!e.getPlan().getId().equals(mPlan.getId())) continue;
                mEnrollment = e;
                break;
            }
        }
        System.out.println("[PlanDetailView.initializeView] starting. planId=" + HelperService.safePrint(mPlan.getId()));
        System.out.println("[PlanDetailView.initializeView] enrollment=" + (mEnrollment == null ? "null" : mEnrollment.getId()));

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
        mProgressControl = new EnrollmentProgressControl(mEnrollment, (TextView) findViewById(R.id.plandetail_enrollstatustext), findViewById(R.id.plandetail_shifttasksbtn));

        // plan id
        ((TextView)findViewById(R.id.plandetail_id)).setText(ctx.getString(R.string.plandetail_id).replace("{id}", mPlan.getId()));

        // show plan creator
        String creatorTextFormat = ctx.getString(R.string.common_createdby);
        TextView creatorLine = ((TextView) findViewById(R.id.plandetail_creator));
        creatorLine.setText(creatorTextFormat.replace("{name}", mPlan.getCreator().getFirstName() + " " + mPlan.getCreator().getLastName()));
        if(disableUserLink){
            creatorLine.setTextColor(ContextCompat.getColor(ctx, R.color.gray));
        }else {
            creatorLine.setTextColor(ContextCompat.getColor(ctx, R.color.colorPrimary));
            creatorLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppContext.getCurrent().getNavigationService().navigateChild(UserPlanCollectionView.class, HelperService.getSinglePairMap("user", mPlan.getCreator()));
                }
            });
        }

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
                ctx.hideLoadingScreen();
            }
        });
    }

    /**
     * Update the view elements based on current plan data
     */
    private void updateView(){
        mProgressControl.updateView();

        // initialize buttons
        mEditBtn.setVisibility(View.GONE);
        mDeleteBtn.setVisibility(View.GONE);
        mSubmitBtn.setVisibility(View.GONE);
        mPublishBtn.setVisibility(View.GONE);
        mUnenrollBtn.setVisibility(View.GONE);
        mEnrollBtn.setVisibility(View.GONE);
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
            if (mEnrollment != null) {
                mUnenrollBtn.setVisibility(View.VISIBLE);
            } else {
                mEnrollBtn.setVisibility(View.VISIBLE);
            }
        }

        // update state labels
        mDraftText.setVisibility(mPlan.getState() == 0 ? View.VISIBLE : View.GONE);
        mPrivateText.setVisibility(mPlan.getState() == 1 ? View.VISIBLE : View.GONE);

        // update name and descriptions
        mNameText.setText(mPlan.getName());
        if(mPlan.getDescription() != null && !mPlan.getDescription().equals("")){
            mDescText.setText(HelperService.fromHtml(mPlan.getDescription()));
            mDescText.setMovementMethod(LinkMovementMethod.getInstance());
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
                                updateView();
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
                                updateView();
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
                        AppContext.getCurrent().getActivity().showLoadingScreen();
                        mEnrollment.submitDelete(new Callback() {
                            @Override
                            public void execute(CallbackParams params) {
                                AppContext.getCurrent().getEnrollments().getItems().remove(mEnrollment);
                                AppContext.getCurrent().getEnrollments().notifyListeners();
                                mEnrollment = null;
                                updateView();
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
                        mEnrollment = new PlanEnrollment(null, mPlan, (new TaskDate()).toDateKey(), 0, null);
                        mEnrollment.submitUpdate(new Callback() {
                            @Override
                            public void execute(CallbackParams params) {
                                AppContext.getCurrent().getEnrollments().getItems().add(mEnrollment);
                                AppContext.getCurrent().getEnrollments().notifyListeners();
                                updateView();
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
                        AppContext.getCurrent().getActivity().showLoadingScreen();
                        mPlan.submitDelete(new Callback() {
                            @Override
                            public void execute(CallbackParams params) {
                                AppContext.getCurrent().getNavigationService().goToMainActivity(CreatedPlanCollectionView.class, null);
                            }
                        });
                    }
                }, null);
            }
        };
    }
}
