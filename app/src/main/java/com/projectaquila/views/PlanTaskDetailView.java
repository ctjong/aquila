package com.projectaquila.views;

import android.view.View;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.activities.ShellActivity;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.controls.EnrollmentProgressControl;
import com.projectaquila.datamodels.PlanEnrollment;
import com.projectaquila.datamodels.PlanTask;
import com.projectaquila.services.HelperService;

import org.w3c.dom.Text;

import java.util.HashMap;

public class PlanTaskDetailView extends ViewBase {
    private PlanTask mPlanTask;
    private PlanEnrollment mEnrollment;
    private TextView mNameText;
    private View mDescView;
    private TextView mDescText;
    private View mEnrollmentSection;
    private TextView mPlanIntro;
    private TextView mPlanLink;
    private View mCompleteDisabled;
    private View mCompleteBtn;
    private EnrollmentProgressControl mProgressControl;
    private ShellActivity mShell;

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
        mShell = AppContext.getCurrent().getActivity();

        // get UI elements
        mNameText = (TextView)findViewById(R.id.plantaskdetail_name);
        mDescView = findViewById(R.id.plantaskdetail_desc);
        mDescText = (TextView)findViewById(R.id.plantaskdetail_desctext);
        mEnrollmentSection = findViewById(R.id.plantaskdetail_enrollment);
        mPlanIntro = (TextView)findViewById(R.id.plantaskdetail_day);
        mPlanLink= (TextView)findViewById(R.id.plantaskdetail_planlink);
        mCompleteDisabled = findViewById(R.id.plantaskdetail_completedisabled);
        mCompleteBtn = findViewById(R.id.plantaskdetail_completebtn);
        mProgressControl = new EnrollmentProgressControl(mEnrollment, (TextView) findViewById(R.id.plantaskdetail_enrollstatustext), findViewById(R.id.plantaskdetail_shifttasksbtn));

        // setup event handlers
        mPlanTask.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateView();
            }
        });
        mPlanLink.setOnClickListener(getOpenPlanClickListener());
        mCompleteBtn.setOnClickListener(getCompleteClickListener());

        // initialize view
        updateView();
    }

    /**
     * Update the view elements based on current plan task data
     */
    private void updateView(){
        mProgressControl.updateView();

        // for enrollment view mode, we want to show enrollment details, completion buttons, and link to parent plan
        if(mEnrollment != null) {
            mEnrollmentSection.setVisibility(View.VISIBLE);
            String introText = mShell.getString(R.string.plantaskdetail_day);
            introText = introText.replace("{day}", HelperService.toString(mPlanTask.getDay()));
            mPlanIntro.setText(introText);
            String planLink = mShell.getString(R.string.plantaskdetail_planlink)
                    .replace("{planId}", mPlanTask.getParent().getId())
                    .replace("{planName}", mPlanTask.getParent().getName());
            mPlanLink.setText(planLink);
            if(mPlanTask.isReadyToComplete(mEnrollment)){
                mCompleteDisabled.setVisibility(View.GONE);
                mCompleteBtn.setVisibility(View.VISIBLE);
            }else{
                mCompleteDisabled.setVisibility(View.VISIBLE);
                mCompleteBtn.setVisibility(View.GONE);
            }
        }else{
            mEnrollmentSection.setVisibility(View.GONE);
        }

        // update name and description
        mNameText.setText(mPlanTask.getName());
        if(!mPlanTask.getDescription().equals("")) {
            mDescText.setText(mPlanTask.getDescription());
            mDescView.setVisibility(View.VISIBLE);
        }else{
            mDescView.setVisibility(View.GONE);
        }
    }

    /**
     * Get click handler for the complete button
     * @return click handler
     */
    private View.OnClickListener getCompleteClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperService.showAlert(R.string.prompt_completetask_title, R.string.prompt_completetask_msg, new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        System.out.println("[PlanTaskDetailView.getCompleteClickListener] completing plan task " + mPlanTask.getId());
                        AppContext.getCurrent().getNavigationService().goToMainActivity();
                        mShell.showLoadingScreen();
                        mEnrollment.setCompletedDays(mPlanTask.getDay());
                        final String returnDateKey = mEnrollment.getStartDate().getModified(mPlanTask.getDay() - 1).toDateKey();
                        mEnrollment.submitUpdate(new Callback() {
                            @Override
                            public void execute(CallbackParams params) {
                                AppContext.getCurrent().getNavigationService().navigate(TaskCollectionView.class, HelperService.getSinglePairMap("date", returnDateKey));
                                mShell.hideLoadingScreen();
                            }
                        });
                    }
                }, null);
            }
        };
    }

    /**
     * Get click handler for the plan link
     * @return click handler
     */
    private View.OnClickListener getOpenPlanClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanTaskDetailView.getOpenPlanClickListener] opening plan " + mPlanTask.getParent().getId());
                HashMap<String, Object> navParams = new HashMap<>();
                navParams.put("plan", mPlanTask.getParent());
                navParams.put("enrollment", mEnrollment);
                AppContext.getCurrent().getNavigationService().navigateChild(PlanDetailView.class, navParams);
            }
        };
    }
}
