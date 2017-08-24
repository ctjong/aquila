package com.planmaster.views;

import com.planmaster.R;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.contexts.AppContext;
import com.planmaster.dataadapters.PlanCollectionAdapter;

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
