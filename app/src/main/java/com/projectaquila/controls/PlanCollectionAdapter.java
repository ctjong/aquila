package com.projectaquila.controls;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.common.PlanCollectionType;
import com.projectaquila.datamodels.PlanCollection;
import com.projectaquila.services.HelperService;
import com.projectaquila.views.PlanDetailView;
import com.squareup.picasso.Picasso;

/**
 * Adapter for listing out plans data on a View
 */
public class PlanCollectionAdapter extends ArrayAdapter<Plan>{
    private PlanCollectionType mType;
    private PlanCollection mPlans;

    /**
     * Initialize new plans adapter
     * @param type collection type to view
     */
    public PlanCollectionAdapter(PlanCollectionType type){
        super(AppContext.getCurrent().getActivity(), R.layout.control_plancontrol);
        mType = type;
        if (type == PlanCollectionType.ENROLLED) {
            mPlans = AppContext.getCurrent().getEnrollments().getPlans();
        } else {
            mPlans = new PlanCollection(type);
        }
    }

    /**
     * Load the whole plans set for the current view
     */
    public void load(final Callback cb){
        mPlans.load(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                clear();
                if(mType == PlanCollectionType.ENROLLED){
                    if(cb != null) cb.execute(params);
                    return;
                }
                addAll(mPlans.getItems());
                if(cb != null) cb.execute(params);
            }
        });
    }

    /**
     * Load a part of the whole plans set
     * @param partNum part number
     * @param take number of plans to take
     */
    public void loadPart(int partNum, int take, final Callback cb){
        mPlans.loadPart(partNum * take, take, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                clear();
                if(mType == PlanCollectionType.ENROLLED){
                    if(cb != null) cb.execute(params);
                    return;
                }
                addAll(mPlans.getItems());
                if(cb != null) cb.execute(params);
            }
        });
    }

    /**
     * Get view for item at the given position
     * @param position item position
     * @param convertView convert view
     * @param parent parent view
     * @return view object
     */
    @NonNull
    @Override
    public View getView (int position, View convertView, @NonNull ViewGroup parent){
        if(convertView == null || convertView instanceof TextView) {
            convertView = View.inflate(getContext(), R.layout.control_plancontrol, null);
            if (convertView == null) {
                System.err.println("[PlanCollectionAdapter.getView] failed to get view at index" + position);
                return new TextView(getContext());
            }
        }
        final Plan plan = getItem(position);
        if(plan == null){
            System.err.println("[PlanCollectionAdapter.getView] failed to get plan at position " + position);
            return new TextView(getContext());
        }

        final TextView nameText = (TextView)convertView.findViewById(R.id.plancontrol_name);
        final TextView descText = (TextView)convertView.findViewById(R.id.plancontrol_description);
        final ImageView planImg = (ImageView)convertView.findViewById(R.id.plancontrol_img);
        updateView(plan, nameText, descText, planImg);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getCurrent().getNavigationService().navigateChild(PlanDetailView.class, HelperService.getSinglePairMap("plan", plan));
            }
        });
        return convertView;
    }

    /**
     * Update the values of the view elements
     * @param plan Plan data
     * @param nameText name TextView
     * @param descText description TextView
     * @param planImg plan image ImageView
     */
    private void updateView(Plan plan, TextView nameText, TextView descText, ImageView planImg){
        nameText.setText(plan.getName());
        descText.setText(plan.getDescription());
        String imageUrl = plan.getImageUrl();
        if(imageUrl != null && !imageUrl.equals("") && !imageUrl.equals("null")) {
            Picasso.with(getContext()).load(plan.getImageUrl()).into(planImg);
        }else{
            planImg.setImageResource(R.drawable.noimage);
        }
    }
}
