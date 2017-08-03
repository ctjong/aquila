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
import com.projectaquila.datamodels.PlanEnrollment;
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

        // get UI elements and bind event handlers
        mPlanTask = planTask;
        mClickTarget = clickTarget;
        setOnClickListener(getClickHandler());

        // initialize view
        String label = AppContext.getCurrent().getActivity().getString(R.string.plantaskcontrol_label_format).replace("{day}", HelperService.toString(mPlanTask.getDay()));
        ((TextView)findViewById(R.id.plantaskcontrol_label)).setText(label);
        String name = mPlanTask.getName();
        if(name != null && !name.equals("")) {
            ((TextView) findViewById(R.id.plantaskcontrol_name)).setText(mPlanTask.getName());
        }

        // show/hide completion check mark
        for(PlanEnrollment e : AppContext.getCurrent().getEnrollments().getItems()){
            if(e.getPlan().getId().equals(mPlanTask.getParent().getId()) && mPlanTask.getDay() <= e.getCompletedDays()){
                findViewById(R.id.plantaskcontrol_check).setVisibility(VISIBLE);
                break;
            }
        }
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
