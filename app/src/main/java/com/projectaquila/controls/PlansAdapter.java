package com.projectaquila.controls;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.Callback;
import com.projectaquila.models.CallbackParams;
import com.projectaquila.models.Plan;
import com.projectaquila.models.PlansViewMode;
import com.projectaquila.views.PlansView;
import com.projectaquila.views.ViewBase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Adapter for listing out plans data on a View
 */
public class PlansAdapter extends ArrayAdapter<Plan>{
    private PlansViewMode mViewMode;

    /**
     * Initialize new plans adapter
     * @param viewMode current view mode
     */
    public PlansAdapter(PlansViewMode viewMode){
        super(AppContext.getCurrent().getActivity(), R.layout.control_plancontrol);
        mViewMode = viewMode;
    }

    /**
     * Load the whole plans set for the current view
     */
    public void load(){
        String dataUrlFormat = getDataUrlFormat();
        clear();
        AppContext.getCurrent().getDataService().requestAll(dataUrlFormat, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                List result = (List)params.get("result");
                for(Object plansObj : result){
                    processServerResponse((JSONArray)plansObj);
                }
                AppContext.getCurrent().getActivity().showContentScreen();
            }
        });
    }

    /**
     * Load a part of the whole plans set
     * @param partNum part number
     * @param take number of plans to take
     */
    public void loadPart(int partNum, int take){
        String dataUrl = String.format(getDataUrlFormat(), partNum * take, take);
        clear();
        AppContext.getCurrent().getDataService().request(ApiTaskMethod.GET, dataUrl, null, new Callback() {
            @Override
            public void execute(CallbackParams params) {
                processServerResponse(params.getApiResult().getItems());
                AppContext.getCurrent().getActivity().showContentScreen();
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
                System.err.println("[PlansAdapter.getView] failed to get view at index" + position);
                return new TextView(getContext());
            }
        }
        final Plan plan = getItem(position);
        if(plan == null){
            System.err.println("[PlansAdapter.getView] failed to get plan at position " + position);
            return new TextView(getContext());
        }
        ((TextView)convertView.findViewById(R.id.plancontrol_title)).setText(plan.getTitle());
        ((TextView)convertView.findViewById(R.id.plancontrol_description)).setText(plan.getDescription());
        if(plan.getImageUrl() != null) {
            ImageView planImg = (ImageView) convertView.findViewById(R.id.plancontrol_img);
            Picasso.with(getContext()).load(plan.getImageUrl()).into(planImg);
        }
        return convertView;
    }

    /**
     * Get API URL format for the data on the current view
     * @return API URL format
     */
    private String getDataUrlFormat(){
        if(mViewMode == PlansViewMode.ENROLLED){
            return "/data/planenrollment/private/findall/id/%d/%d";
        }else if(mViewMode == PlansViewMode.CREATED){
            return "/data/plan/private/findall/id/%d/%d";
        }else{
            return "/data/plan/public/findall/id/%d/%d";
        }
    }

    /**
     * Process plans array from the server
     * @param plans plans JSON array
     */
    private void processServerResponse(JSONArray plans){
        for(int i=0; i<plans.length(); i++){
            try {
                Plan plan = Plan.parse(plans.get(i));
                add(plan);
            }catch(JSONException e){
                System.err.println("[PlansAdapter.processServerResponse] an error occurred while trying to get plans at index " + i);
                e.printStackTrace();
            }
        }
    }
}
