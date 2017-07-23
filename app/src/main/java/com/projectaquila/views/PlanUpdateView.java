package com.projectaquila.views;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.datamodels.PlanItem;
import com.projectaquila.services.HelperService;

public class PlanUpdateView extends ViewBase {
    private Plan mPlan;
    private LinearLayout mItemsView;

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
        if(getNavArgStr("id") != null){
            return R.string.planupdate_title;
        }
        return R.string.plancreate_title;
    }

    /**
     * Initialize the view
     */
    @Override
    protected void initializeView(){
        String planId = getNavArgStr("id");
        if(planId == null) {
            System.out.println("[PlanUpdateView.initializeView] mode=create");
            mPlan = new Plan(null, AppContext.getCurrent().getActiveUser().getId(), false, null, null, null);
        }else{
            System.out.println("[PlanUpdateView.initializeView] mode=update");
        }

        mItemsView = (LinearLayout)findViewById(R.id.planupdate_schedule);
        for(PlanItem planItem : mPlan.getItems().values()){
            View control = getItemControl(planItem);
            mItemsView.addView(control);
        }
        findViewById(R.id.planupdate_add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PlanItem planItem = new PlanItem(null, mPlan.getItems().size() + 1, "", "");
                planItem.addChangedHandler(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        String name = planItem.getName();
                        if(name == null || name.equals("")) return;
                        String planItemKey = HelperService.toString(planItem.getOrder());
                        if(!mPlan.getItems().containsKey(planItemKey)){
                            mPlan.getItems().put(planItemKey, planItem);
                            View control = getItemControl(planItem);
                            mItemsView.addView(control);
                        }
                    }
                });
                AppContext.getCurrent().getNavigationService().navigateChild(PlanItemUpdateView.class, HelperService.getSinglePairMap("planitem", planItem));
            }
        });
        findViewById(R.id.planupdate_save_btn).setOnClickListener(getSaveButtonClickHandler());
        findViewById(R.id.planupdate_cancel_btn).setOnClickListener(getCancelButtonClickHandler());
        AppContext.getCurrent().getActivity().showContentScreen();
    }

    /**
     * Get a control to view a plan item
     * @param planItem plan item object
     * @return control view object
     */
    public View getItemControl (final PlanItem planItem){
        final View control = View.inflate(AppContext.getCurrent().getActivity(), R.layout.control_planitemcontrol, null);
        String label = AppContext.getCurrent().getActivity().getString(R.string.planitemcontrol_label_format).replace("{day}", HelperService.toString(planItem.getOrder()));
        ((TextView)control.findViewById(R.id.planitemcontrol_label)).setText(label);
        ((TextView)control.findViewById(R.id.planitemcontrol_name)).setText(planItem.getName());
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanUpdateView.getItemControl] opening update page for plan item #" + planItem.getOrder());
                planItem.addChangedHandler(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        String name = planItem.getName();
                        if(name == null || name.equals("")) return;
                        ((TextView)control.findViewById(R.id.planitemcontrol_name)).setText(planItem.getName());
                    }
                });
                AppContext.getCurrent().getNavigationService().navigateChild(PlanItemUpdateView.class, HelperService.getSinglePairMap("planitem", planItem));
            }
        });
        return control;
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
