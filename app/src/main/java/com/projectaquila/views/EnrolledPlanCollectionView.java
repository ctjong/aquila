package com.projectaquila.views;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.dataadapters.PlanCollectionAdapter;

public class EnrolledPlanCollectionView extends PlanCollectionView {
    @Override
    protected int getTitleBarStringId() {
        return R.string.menu_enrolled_plans;
    }

    @Override
    protected int getNullTextStringId() {
        return R.string.plans_enrolled_null;
    }

    @Override
    protected void setupPlanCollectionView() throws UnsupportedOperationException {
        System.out.println("[EnrolledPlanCollectionView.setupPlanCollectionView] started");
        mAdapter = new PlanCollectionAdapter(AppContext.getCurrent().getEnrollments().getPlans(), false);
        mAdapter.sync();
        mLoadCallback.execute(null);
    }
}
