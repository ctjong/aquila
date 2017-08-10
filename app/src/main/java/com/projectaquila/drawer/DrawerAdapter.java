package com.projectaquila.drawer;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.views.CreatedPlanCollectionView;
import com.projectaquila.views.EnrolledPlanCollectionView;
import com.projectaquila.views.PublicPlanCollectionView;
import com.projectaquila.views.TaskCollectionView;
import com.squareup.picasso.Picasso;

/**
 * Adapter for populating drawer item
 */
public class DrawerAdapter extends ArrayAdapter<DrawerItem>{
    /**
     * Instantiate a new drawer adapter
     */
    public DrawerAdapter(){
        super(AppContext.getCurrent().getActivity(), R.layout.control_draweritem);
        updateDrawer();

        AppContext.getCurrent().getAuthService().addAuthStateChangeHandler(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                updateDrawer();
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
            convertView = View.inflate(getContext(), R.layout.control_draweritem, null);
            if (convertView == null) {
                System.err.println("[DrawerAdapter.getView] failed to get view at index" + position);
                return new TextView(getContext());
            }
        }
        final DrawerItem item = getItem(position);
        if(item == null){
            System.err.println("[PlanCollectionAdapter.getView] failed to get plan at position " + position);
            return new TextView(getContext());
        }
        item.updateLayout(convertView);
        TextView line1View = (TextView) convertView.findViewById(R.id.draweritem_line1);
        TextView line2View = (TextView) convertView.findViewById(R.id.draweritem_line2);
        ImageView imgView = (ImageView) convertView.findViewById(R.id.draweritem_img);
        String line1 = item.getLine1String();
        String line2 = item.getLine2String();
        String imgUrl = item.getImageUrl();
        line1View.setVisibility(line1 != null ? View.VISIBLE : View.GONE);
        if(line1 != null) line1View.setText(line1);
        line2View.setVisibility(line2 != null ? View.VISIBLE : View.GONE);
        if(line2 != null) line2View.setText(line1);
        if(imgUrl != null && !imgUrl.equals("") && !imgUrl.equals("null")) {
            imgView.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(imgUrl).into(imgView);
        }else{
            imgView.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.invoke();
            }
        });
        return convertView;
    }

    /**
     * Update drawer items based on app state
     */
    private void updateDrawer(){
        if(AppContext.getCurrent().getActiveUser() != null){
            if(getCount() == 0) {
                add(new UserDrawerItem());
                add(new NavDrawerItem(R.string.menu_tasks, null, TaskCollectionView.class, null));
                add(new NavDrawerItem(R.string.menu_enrolled_plans, null, EnrolledPlanCollectionView.class, null));
                add(new NavDrawerItem(R.string.menu_browse_plans, null, PublicPlanCollectionView.class, null));
                add(new NavDrawerItem(R.string.menu_created_plans, null, CreatedPlanCollectionView.class, null));
                add(new LogoutDrawerItem());
            }
            AppContext.getCurrent().getActivity().toggleToolbarIcon(true);
            notifyDataSetChanged();
        }else{
            AppContext.getCurrent().getActivity().toggleToolbarIcon(false);
        }
    }
}
