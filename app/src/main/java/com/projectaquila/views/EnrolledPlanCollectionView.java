package com.projectaquila.views;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanCollection;

public class EnrolledPlanCollectionView extends PlanCollectionView {
    private PlanCollection mPlans;

    @Override
    protected int getTitleBarStringId() {
        return R.string.menu_enrolled_plans;
    }

    @Override
    protected PlanCollection getPlans() {
        if(mPlans == null) mPlans = AppContext.getCurrent().getEnrollments().getPlans();
        return mPlans;
    }

    @Override
    protected int getNullTextStringId() {
        return R.string.plans_enrolled_null;
    }

    @Override
    protected void initializeView(){
        System.out.println("[EnrolledPlanCollectionView.initializeView] started");
        if(!tryInitVars()) return;
        mAdapter.sync();
        mLoadCallback.execute(null);
    }
}
