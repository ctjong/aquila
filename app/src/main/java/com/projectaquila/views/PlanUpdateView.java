package com.projectaquila.views;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.PlanTaskComparator;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.controls.PlanTaskControl;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.datamodels.PlanTask;
import com.projectaquila.services.HelperService;

import java.util.Collections;

public class PlanUpdateView extends ViewBase {
    private Plan mPlan;
    private LinearLayout mItemsView;
    private EditText mNameText;
    private EditText mDescText;
    private PlanTaskComparator mComparator;

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
        if(mPlan.getId() != null){
            return R.string.planupdate_title;
        }
        return R.string.plancreate_title;
    }

    /**
     * Initialize the view
     */
    @Override
    protected void initializeView(){
        mPlan = (Plan)getNavArgObj("plan");
        System.out.println("[PlanUpdateView.initializeView] starting. planId=" + HelperService.safePrint(mPlan.getId()));
        mNameText = (EditText)findViewById(R.id.planupdate_name);
        mDescText = (EditText)findViewById(R.id.planupdate_desc);
        mItemsView = (LinearLayout)findViewById(R.id.planupdate_schedule);
        mComparator = new PlanTaskComparator();
        View addButton = findViewById(R.id.planupdate_add_btn);

        if(mPlan.getState() == 0) {
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(getAddButtonClickHandler());
        }else{
            addButton.setVisibility(View.GONE);
        }
        findViewById(R.id.planupdate_save_btn).setOnClickListener(getSaveButtonClickHandler());
        findViewById(R.id.planupdate_cancel_btn).setOnClickListener(getCancelButtonClickHandler());

        if(mPlan.getId() != null) {
            AppContext.getCurrent().getActivity().showLoadingScreen();
            mPlan.load(new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    updateView();
                    AppContext.getCurrent().getActivity().hideLoadingScreen();
                }
            });
        } else {
            updateView();
        }
    }

    /**
     * Update the view elements based on current plan data
     */
    private void updateView(){
        mNameText.setText(mPlan.getName());
        mDescText.setText(mPlan.getDescription());
        mItemsView.removeAllViews();
        Collections.sort(mPlan.getItems(), mComparator);
        for(final PlanTask planTask : mPlan.getItems()){
            planTask.addChangedHandler(new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    planTask.removeChangedHandler(this);
                    updateView();
                    //TODO reorder plan task days
                }
            });
            mItemsView.addView(PlanTaskControl.create(planTask, null, mPlan.getState() == 0));
        }
    }

    /**
     * Get click handler for the add task button
     * @return click handler
     */
    public View.OnClickListener getAddButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PlanTask planTask = new PlanTask(mPlan, null, 0, mPlan.getItems().size() + 1, "", "");
                planTask.addChangedHandler(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        planTask.removeChangedHandler(this);
                        String name = planTask.getName();
                        if (name == null || name.equals("")) return;
                        mPlan.getItems().add(planTask);
                        updateView();
                    }
                });
                AppContext.getCurrent().getNavigationService().navigateChild(PlanTaskUpdateView.class, HelperService.getSinglePairMap("plantask", planTask));
            }
        };
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
                AppContext.getCurrent().getActivity().showLoadingScreen();
                String newName = mNameText.getText().toString();
                if(!newName.equals("")){
                    mPlan.setName(newName);
                    mPlan.setDescription(mDescText.getText().toString());
                    mPlan.submitUpdate(new Callback() {
                        @Override
                        public void execute(CallbackParams params) {
                            System.out.println("[PlanUpdateView.getSaveButtonClickHandler] saving done. exiting.");                AppContext.getCurrent().getActivity().hideLoadingScreen();
                            AppContext.getCurrent().getActivity().hideLoadingScreen();
                            AppContext.getCurrent().getActivity().onBackPressed();
                            mPlan.notifyListeners();
                        }
                    });
                }else{
                    System.out.println("[PlanUpdateView.getSaveButtonClickHandler] missing name. exiting without saving.");
                    AppContext.getCurrent().getActivity().hideLoadingScreen();
                    AppContext.getCurrent().getActivity().onBackPressed();
                }
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
                AppContext.getCurrent().getActivity().onBackPressed();
            }
        };
    }
}
