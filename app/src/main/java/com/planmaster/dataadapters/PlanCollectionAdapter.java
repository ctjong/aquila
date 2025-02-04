package com.planmaster.dataadapters;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.planmaster.contexts.AppContext;
import com.planmaster.R;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.datamodels.Plan;
import com.planmaster.datamodels.PlanCollection;
import com.planmaster.datamodels.User;
import com.planmaster.services.HelperService;
import com.planmaster.views.PlanDetailView;
import com.planmaster.views.UserPlanCollectionView;

import java.util.HashMap;

/**
 * Adapter for listing out plans data on a View
 */
public class PlanCollectionAdapter extends CollectionAdapter<Plan>{
    private PlanCollection mPlans;
    private boolean mDisableUserLink;

    /**
     * Initialize new plans adapter
     * @param plans plan collection to view
     * @param disableUserLink whether or not we should disable user link
     */
    public PlanCollectionAdapter(PlanCollection plans, boolean disableUserLink) throws UnsupportedOperationException {
        super(R.layout.control_plancontrol);
        mPlans = plans;
        mDisableUserLink = disableUserLink;
        mPlans.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                sync();
            }
        });
    }

    /**
     * Sync the adapter's items list with the data model
     */
    public void sync(){
        clear();
        addAll(mPlans.getItems());
        notifyDataSetChanged();
    }

    /**
     * Set the adapter's data model to the specified plans
     */
    public void setPlans(PlanCollection plans){
        mPlans = plans;
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
                HelperService.logError("[PlanCollectionAdapter.getView] failed to get view at index" + position);
                return new TextView(getContext());
            }
        }
        final Plan plan = getItem(position);
        if(plan == null){
            HelperService.logError("[PlanCollectionAdapter.getView] failed to get plan at position " + position);
            return new TextView(getContext());
        }

        updateCardView(plan, convertView);
        final View controlView = convertView;
        plan.addChangedHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateCardView(plan, controlView);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> navParams = HelperService.getSinglePairMap("plan", plan);
                navParams.put("disableUserLink", mDisableUserLink);
                AppContext.getCurrent().getNavigationService().navigateChild(PlanDetailView.class, navParams);
            }
        });
        return convertView;
    }

    /**
     * Update the values of the view elements
     * @param plan Plan data
     * @param convertView control view
     */
    private void updateCardView(final Plan plan, View convertView){
        ((TextView)convertView.findViewById(R.id.plancontrol_name)).setText(plan.getName());
        convertView.findViewById(R.id.plancontrol_draft_label).setVisibility(plan.getState() == 0 ? View.VISIBLE : View.GONE);
        convertView.findViewById(R.id.plancontrol_private_label).setVisibility(plan.getState() == 1 ? View.VISIBLE : View.GONE);

        // set author line
        TextView userLine = ((TextView) convertView.findViewById(R.id.plancontrol_secondline));
        if(mDisableUserLink) {
            userLine.setVisibility(View.GONE);
        } else {
            final User creator = plan.getCreator();
            String createdByLine = getContext().getString(R.string.common_createdby).replace("{name}", creator.getFirstName() + " " + creator.getLastName());
            userLine.setText(createdByLine);
            userLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppContext.getCurrent().getNavigationService().navigateChild(UserPlanCollectionView.class, HelperService.getSinglePairMap("user", creator));
                }
            });
        }
    }
}
