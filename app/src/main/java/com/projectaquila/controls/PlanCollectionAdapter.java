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
import com.squareup.picasso.Picasso;

/**
 * Adapter for listing out plans data on a View
 */
public class PlanCollectionAdapter extends ArrayAdapter<Plan>{
    private PlanCollection mPlans;

    /**
     * Initialize new plans adapter
     * @param viewMode current view mode
     */
    public PlanCollectionAdapter(PlanCollectionType viewMode){
        super(AppContext.getCurrent().getActivity(), R.layout.control_plancontrol);
        if (viewMode == PlanCollectionType.ENROLLED) {
            mPlans = AppContext.getCurrent().getData().getEnrolledPlans();
        } else if (viewMode == PlanCollectionType.BROWSE) {
            mPlans = AppContext.getCurrent().getData().getCreatedPlans();
        } else {
            mPlans = new PlanCollection(PlanCollectionType.CREATED);
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
                addAll(mPlans.getItems().values());
                AppContext.getCurrent().getActivity().showContentScreen();
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
                addAll(mPlans.getItems().values());
                AppContext.getCurrent().getActivity().showContentScreen();
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
        ((TextView)convertView.findViewById(R.id.plancontrol_title)).setText(plan.getName());
        ((TextView)convertView.findViewById(R.id.plancontrol_description)).setText(plan.getDescription());
        ImageView planImg = (ImageView) convertView.findViewById(R.id.plancontrol_img);
        String imageUrl = plan.getImageUrl();
        if(imageUrl != null && !imageUrl.equals("") && !imageUrl.equals("null")) {
            Picasso.with(getContext()).load(plan.getImageUrl()).into(planImg);
        }else{
            planImg.setImageResource(R.drawable.noimage);
        }
        return convertView;
    }
}
