package com.projectaquila.adapters;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.common.PlanCollectionType;
import com.projectaquila.datamodels.PlanCollection;
import com.projectaquila.datamodels.User;
import com.projectaquila.services.HelperService;
import com.projectaquila.views.PlanDetailView;
import com.squareup.picasso.Picasso;

/**
 * Adapter for listing out plans data on a View
 */
public class PlanCollectionAdapter extends CollectionAdapter<Plan>{
    private PlanCollectionType mType;
    private PlanCollection mPlans;

    /**
     * Initialize new plans adapter
     * @param type collection type to view
     */
    public PlanCollectionAdapter(PlanCollectionType type) throws UnsupportedOperationException {
        super(R.layout.control_plancontrol);
        mType = type;
        if (type != PlanCollectionType.ENROLLED) {
            mPlans = new PlanCollection(type);
        }
    }

    /**
     * Load the whole plans set for the current view
     */
    public void loadItems(final Callback cb){
        if(mType == PlanCollectionType.ENROLLED){
            // if load is called on an adapter that is viewing enrolled collection,
            // we can directly load the items to the array because data has been loaded in the context
            mPlans = AppContext.getCurrent().getEnrollments().getPlans();
            clear();
            addAll(mPlans.getItems());
            if(cb != null) cb.execute(null);
        }else {
            AppContext.getCurrent().getActivity().showLoadingScreen();
            mPlans.loadItems(new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    clear();
                    addAll(mPlans.getItems());
                    if (cb != null) cb.execute(params);
                    AppContext.getCurrent().getActivity().hideLoadingScreen();
                }
            });
        }
    }

    /**
     * Load a part of the whole plans set
     * @param partNum part number
     * @param take number of plans to take
     */
    public void loadItemsPart(int partNum, int take, final Callback cb){
        if(mType == PlanCollectionType.ENROLLED){
            // if load is called on an adapter that is viewing enrolled collection,
            // we get the data from the app context instead of from a server request
            mPlans = AppContext.getCurrent().getEnrollments().getPlans();
            clear();
            addAll(mPlans.getItems());
            if(cb != null) cb.execute(null);
        }else {
            AppContext.getCurrent().getActivity().showLoadingScreen();
            mPlans.loadItemsPart(partNum * take, take, new Callback() {
                @Override
                public void execute(CallbackParams params) {
                    clear();
                    addAll(mPlans.getItems());
                    if (cb != null) cb.execute(params);
                    AppContext.getCurrent().getActivity().hideLoadingScreen();
                }
            });
        }
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
                AppContext.getCurrent().getNavigationService().navigateChild(PlanDetailView.class, HelperService.getSinglePairMap("plan", plan));
            }
        });
        return convertView;
    }

    /**
     * Update the values of the view elements
     * @param plan Plan data
     * @param convertView control view
     */
    private void updateCardView(Plan plan, View convertView){
        ((TextView)convertView.findViewById(R.id.plancontrol_name)).setText(plan.getName());
        User creator = plan.getCreator();
        String createdByLine = getContext().getString(R.string.plancontrol_createdby).replace("{name}", creator.getFirstName() + " " + creator.getLastName());
        ((TextView)convertView.findViewById(R.id.plancontrol_secondline)).setText(createdByLine);
        convertView.findViewById(R.id.plancontrol_draft_label).setVisibility(plan.getState() == 0 ? View.VISIBLE : View.GONE);
        convertView.findViewById(R.id.plancontrol_private_label).setVisibility(plan.getState() == 1 ? View.VISIBLE : View.GONE);

        // update image
        String imageUrl = plan.getImageUrl();
        ImageView planImg = ((ImageView)convertView.findViewById(R.id.plancontrol_img));
        if(imageUrl != null && !imageUrl.equals("") && !imageUrl.equals("null")) {
            Picasso.with(getContext()).load(plan.getImageUrl()).into(planImg);
        }else{
            planImg.setImageResource(R.drawable.noimage);
        }
    }
}
