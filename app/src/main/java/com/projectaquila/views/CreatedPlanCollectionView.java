package com.projectaquila.views;

import android.view.View;
import android.widget.Button;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.dataadapters.PlanCollectionAdapter;
import com.projectaquila.datamodels.CreatedPlanCollection;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.datamodels.PlanCollection;
import com.projectaquila.services.HelperService;

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
                Plan plan = new Plan(null, 0, null, null, null, AppContext.getCurrent().getActiveUser(), null);
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
