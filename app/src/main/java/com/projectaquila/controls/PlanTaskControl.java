package com.projectaquila.controls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanTask;
import com.projectaquila.services.HelperService;

public class PlanTaskControl extends LinearLayout {
    private PlanTask mPlanTask;
    private Class mClickTarget;

    /**
     * Construct a new plan task control
     * @param planTask plan task to show on this control
     * @param clickTarget view class to navigate to on click
     */
    public PlanTaskControl(PlanTask planTask, Class clickTarget){
        super(AppContext.getCurrent().getActivity());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_plantaskcontrol, this);

        mPlanTask = planTask;
        mClickTarget = clickTarget;
        String label = AppContext.getCurrent().getActivity().getString(R.string.plantaskcontrol_label_format).replace("{day}", HelperService.toString(planTask.getDay()));
        ((TextView)findViewById(R.id.plantaskcontrol_label)).setText(label);
        ((TextView)findViewById(R.id.plantaskcontrol_name)).setText(planTask.getName());
        setOnClickListener(getClickHandler());
        mPlanTask.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                String name = mPlanTask.getName();
                if(name == null || name.equals("")) return;
                ((TextView)findViewById(R.id.plantaskcontrol_name)).setText(mPlanTask.getName());
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
                System.out.println("[PlanTaskControl.getItemControl] opening " + mClickTarget.getName() + " for plan task #" + mPlanTask.getDay());
                AppContext.getCurrent().getNavigationService().navigateChild(mClickTarget, HelperService.getSinglePairMap("plantask", mPlanTask));
            }
        };
    }
}
