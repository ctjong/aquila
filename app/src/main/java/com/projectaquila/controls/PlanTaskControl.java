package com.projectaquila.controls;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.Event;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanEnrollment;
import com.projectaquila.datamodels.PlanTask;
import com.projectaquila.services.HelperService;
import com.projectaquila.views.PlanTaskDetailView;
import com.projectaquila.views.PlanTaskUpdateView;

public class PlanTaskControl extends LinearLayout {
    private Event mFocusLostEvent;
    private PlanTask mPlanTask;
    private boolean mIsEditable;

    /**
     * Create a new plan control
     * @param planTask plan task to show on this control
     * @param enrollment enrollment that contains the given task
     * @param isEditable a flag that determines whether or not this is update mode
     */
    public static PlanTaskControl create(PlanTask planTask, PlanEnrollment enrollment, boolean isEditable) {
        PlanTaskControl control = new PlanTaskControl(AppContext.getCurrent().getActivity(), null);
        control.initialize(planTask, enrollment, isEditable);
        return control;
    }

    /**
     * Construct a new plan task control
     * @param context The Context the view is running in
     */
    private PlanTaskControl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Initialize the control
     * @param planTask plan task to show on this control
     * @param enrollment enrollment that contains the given task
     * @param isEditable a flag that determines whether or not this is update mode
     */
    private void initialize(PlanTask planTask, PlanEnrollment enrollment, boolean isEditable){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_plantaskcontrol, this);

        // initialize variables and event handlers
        mFocusLostEvent = new Event();
        mPlanTask = planTask;
        mIsEditable = isEditable;
        setOnClickListener(getClickHandler());

        // initialize view
        String label = AppContext.getCurrent().getActivity().getString(R.string.plantaskcontrol_label_format).replace("{day}", HelperService.toString(mPlanTask.getDay()));
        ((TextView)findViewById(R.id.plantaskcontrol_label)).setText(label);
        String name = mPlanTask.getName();
        if(name != null && !name.equals("")) {
            ((TextView) findViewById(R.id.plantaskcontrol_name)).setText(mPlanTask.getName());
        }

        // show/hide completion check mark
        View checkImage = findViewById(R.id.plantaskcontrol_check);
        if(isEditable) {
            checkImage.setVisibility(GONE);
        }else if(enrollment == null){
            checkImage.setVisibility(GONE);
        }else if(enrollment.getPlan().getId().equals(mPlanTask.getParent().getId()) && mPlanTask.getDay() <= enrollment.getCompletedDays()){
            checkImage.setVisibility(VISIBLE);
        }else{
            checkImage.setVisibility(INVISIBLE);
        }

        //TODO show/hide delete, move up, move down buttons based on isEditable
    }

    /**
     * Add a handler to the focus lost event
     * @param handler event handler
     */
    public void addFocusLostHandler(Callback handler){
        mFocusLostEvent.addHandler(handler);
    }

    /**
     * Get click event handler
     * @return click event handler
     */
    private OnClickListener getClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFocusLostEvent.invoke(null);
                Class clickTarget = mIsEditable ? PlanTaskUpdateView.class : PlanTaskDetailView.class;
                System.out.println("[PlanTaskControl.getClickHandler] opening " + clickTarget.getName() + " for plan task #" + mPlanTask.getDay());
                AppContext.getCurrent().getNavigationService().navigateChild(clickTarget, HelperService.getSinglePairMap("plantask", mPlanTask));
            }
        };
    }
}
