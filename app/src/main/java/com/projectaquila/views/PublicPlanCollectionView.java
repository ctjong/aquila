package com.projectaquila.views;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanCollection;
import com.projectaquila.datamodels.PublicPlanCollection;

public class PublicPlanCollectionView extends PlanCollectionView {
    private PlanCollection mPlans;

    @Override
    protected int getTitleBarStringId() {
        return R.string.menu_browse_plans;
    }

    @Override
    protected PlanCollection getPlans() {
        if(mPlans == null) mPlans = new PublicPlanCollection();
        return mPlans;
    }

    @Override
    protected int getNullTextStringId() {
        return R.string.plans_browse_null;
    }

    @Override
    protected void initializeView(){
        System.out.println("[PublicPlanCollectionView.initializeView] started");
        if(!tryInitVars()) return;
        Callback loadCallback = getLoadCallback();
        AppContext.getCurrent().getActivity().showLoadingScreen();
        //TODO pagination
        mPlans.loadItems(loadCallback);
    }
}
