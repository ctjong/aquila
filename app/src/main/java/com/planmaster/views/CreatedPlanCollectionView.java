package com.planmaster.views;

import android.view.View;
import android.widget.Button;

import com.planmaster.R;
import com.planmaster.common.Callback;
import com.planmaster.common.CallbackParams;
import com.planmaster.contexts.AppContext;
import com.planmaster.dataadapters.PlanCollectionAdapter;
import com.planmaster.datamodels.CreatedPlanCollection;
import com.planmaster.datamodels.Plan;
import com.planmaster.datamodels.PlanCollection;
import com.planmaster.services.HelperService;

public class CreatedPlanCollectionView extends PlanCollectionView {
    @Override
    protected int getTitleBarStringId() {
        return R.string.menu_created_plans;
    }

    @Override
    protected int getNullTextStringId() {
        return R.string.plans_created_null;
    }

    @Override
    protected void setupPlanCollectionView() throws UnsupportedOperationException {
        System.out.println("[CreatedPlanCollectionView.setupPlanCollectionView] started");
        final PlanCollection plans = new CreatedPlanCollection();
        mAdapter = new PlanCollectionAdapter(plans, true);
        Button addBtn = (Button) findViewById(R.id.view_plans_add);
        addBtn.setVisibility(View.VISIBLE);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Plan plan = new Plan(null, 0, null, null, AppContext.getCurrent().getActiveUser(), null);
                AppContext.getCurrent().getNavigationService().navigateChild(PlanUpdateView.class, HelperService.getSinglePairMap("plan", plan));
                plan.addChangedHandler(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        plans.loadItems(mLoadCallback);
                    }
                });
            }
        });
        AppContext.getCurrent().getActivity().showLoadingScreen();
        plans.loadItems(mLoadCallback);
    }
}
