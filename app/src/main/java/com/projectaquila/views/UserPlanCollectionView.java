package com.projectaquila.views;

import android.view.View;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.dataadapters.PlanCollectionAdapter;
import com.projectaquila.datamodels.PlanCollection;
import com.projectaquila.datamodels.User;
import com.projectaquila.datamodels.UserPlanCollection;

public class UserPlanCollectionView extends PlanCollectionView {
    @Override
    protected int getTitleBarStringId() {
        return R.string.plans_user_title;
    }

    @Override
    protected int getNullTextStringId() {
        return R.string.plans_user_null;
    }

    @Override
    protected void setupPlanCollectionView() throws UnsupportedOperationException {
        User user = (User)getNavArgObj("user");
        System.out.println("[UserPlanCollectionView.setupPlanCollectionView] started. user id=" + user.getId());
        PlanCollection plans = new UserPlanCollection(user.getId());
        mAdapter = new PlanCollectionAdapter(plans, true);

        // show user profile
        ((TextView)findViewById(R.id.view_plans_user_name)).setText(user.getFirstName() + " " + user.getLastName());
        findViewById(R.id.view_plans_user).setVisibility(View.VISIBLE);

        // load items
        AppContext.getCurrent().getActivity().showLoadingScreen();
        plans.loadItems(mLoadCallback);
    }
}
