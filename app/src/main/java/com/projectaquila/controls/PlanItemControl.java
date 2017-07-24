package com.projectaquila.controls;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanItem;
import com.projectaquila.services.HelperService;

public class PlanItemControl extends LinearLayout {
    private PlanItem mPlanItem;
    private Class mClickTarget;

    /**
     * Construct a new plan item control
     * @param planItem plan item to show on this control
     * @param clickTarget view class to navigate to on click
     */
    public PlanItemControl(PlanItem planItem, Class clickTarget){
        super(AppContext.getCurrent().getActivity());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_planitemcontrol, this);

        mPlanItem = planItem;
        mClickTarget = clickTarget;
        String label = AppContext.getCurrent().getActivity().getString(R.string.planitemcontrol_label_format).replace("{day}", HelperService.toString(planItem.getDay()));
        ((TextView)findViewById(R.id.planitemcontrol_label)).setText(label);
        ((TextView)findViewById(R.id.planitemcontrol_name)).setText(planItem.getName());
        setOnClickListener(getClickHandler());
        mPlanItem.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                String name = mPlanItem.getName();
                if(name == null || name.equals("")) return;
                ((TextView)findViewById(R.id.planitemcontrol_name)).setText(mPlanItem.getName());
            }
        });
    }

    /**
     * Get click event handler
     * @return click event handler
     */
    private OnClickListener getClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanItemControl.getItemControl] opening " + mClickTarget.getName() + " for plan item #" + mPlanItem.getDay());
                AppContext.getCurrent().getNavigationService().navigateChild(mClickTarget, HelperService.getSinglePairMap("planitem", mPlanItem));
            }
        };
    }
}
