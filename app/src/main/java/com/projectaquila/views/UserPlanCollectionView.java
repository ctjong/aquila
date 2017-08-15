package com.projectaquila.views;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.activities.ShellActivity;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanCollection;
import com.projectaquila.datamodels.User;
import com.projectaquila.datamodels.UserPlanCollection;

public class UserPlanCollectionView extends PlanCollectionView {
    private User mUser;
    private PlanCollection mPlans;

    @Override
    protected int getTitleBarStringId() {
        return R.string.plans_user_title;
    }

    @Override
    protected PlanCollection getPlans() {
        if(mPlans == null) mPlans = new UserPlanCollection(mUser.getId());
        return mPlans;
    }

    @Override
    protected int getNullTextStringId() {
        return R.string.plans_user_null;
    }

    @Override
    protected void initializeView(){
        mUser = (User)getNavArgObj("user");
        System.out.println("[UserPlanCollectionView.initializeView] started. user id=" + mUser.getId());
        if(!tryInitVars()) return;

        // show user profile
        ShellActivity shell = AppContext.getCurrent().getActivity();
        ((TextView)findViewById(R.id.view_plans_user_name)).setText(mUser.getFirstName() + " " + mUser.getLastName());
        findViewById(R.id.view_plans_user).setVisibility(View.VISIBLE);
        int userProfileHeight = (int)shell.getResources().getDimension(R.dimen.plans_user_top_height);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mList.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin + userProfileHeight, params.rightMargin, params.bottomMargin);
        mList.setLayoutParams(params);

        // load items
        AppContext.getCurrent().getActivity().showLoadingScreen();
        mPlans.loadItems(mLoadCallback);
    }
}
