package com.projectaquila.views;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.dataadapters.PlanCollectionAdapter;

public class EnrolledPlanCollectionView extends PlanCollectionView {
    private Callback mEnrollmentChangeHandler;

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
        mLoadCallback.execute(null);

        // listen to changes to the enrollment list
        mEnrollmentChangeHandler = new Callback() {
            @Override
            public void execute(CallbackParams params) {
                mAdapter.setPlans(AppContext.getCurrent().getEnrollments().getPlans());
                mLoadCallback.execute(null);
            }
        };
        AppContext.getCurrent().getEnrollments().addChangedHandler(mEnrollmentChangeHandler);
    }

    @Override
    public void onNavigatedFrom(){
        AppContext.getCurrent().getEnrollments().removeChangedHandler(mEnrollmentChangeHandler);
    }
}
