package com.projectaquila.views;

import android.view.View;
import android.widget.Button;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.CreatedPlanCollection;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.datamodels.PlanCollection;
import com.projectaquila.services.HelperService;

public class CreatedPlanCollectionView extends PlanCollectionView {
    private PlanCollection mPlans;

    @Override
    protected int getTitleBarStringId() {
        return R.string.menu_created_plans;
    }

    @Override
    protected PlanCollection getPlans() {
        if(mPlans == null) mPlans = new CreatedPlanCollection();
        return mPlans;
    }

    @Override
    protected int getNullTextStringId() {
        return R.string.plans_created_null;
    }

    @Override
    protected void initializeView(){
        System.out.println("[CreatedPlanCollectionView.initializeView] started");
        if(!tryInitVars()) return;
        Button addBtn = (Button) findViewById(R.id.view_plans_add);
        addBtn.setVisibility(View.VISIBLE);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Plan plan = new Plan(null, 0, null, null, null, AppContext.getCurrent().getActiveUser(), null);
                AppContext.getCurrent().getNavigationService().navigateChild(PlanUpdateView.class, HelperService.getSinglePairMap("plan", plan));
                plan.addChangedHandler(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        mPlans.loadItems(mLoadCallback);
                    }
                });
            }
        });
        AppContext.getCurrent().getActivity().showLoadingScreen();
        mPlans.loadItems(mLoadCallback);
    }
}
