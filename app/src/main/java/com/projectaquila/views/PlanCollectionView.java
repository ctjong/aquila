package com.projectaquila.views;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.controls.PlanCollectionAdapter;
import com.projectaquila.common.PlanCollectionType;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.services.HelperService;

public class PlanCollectionView extends ViewBase {
    private ListView mList;
    private PlanCollectionAdapter mAdapter;
    private PlanCollectionType mMode;

    @Override
    protected int getLayoutId() {
        return R.layout.view_plans;
    }

    @Override
    protected void initializeView(){
        AppContext.getCurrent().getActivity().showLoadingScreen();
        String modeStr = getNavArgStr("mode");
        System.out.println("[PlanCollectionView.initializeView] mode=" + modeStr);
        mMode = PlanCollectionType.parse(modeStr);
        mAdapter = new PlanCollectionAdapter(mMode);
        mList = (ListView)findViewById(R.id.view_plans_list);
        mList.setAdapter(mAdapter);
        final Callback loadCallback = getLoadCallback();
        if(mMode == PlanCollectionType.BROWSE){
            mAdapter.loadPart(0, 20, loadCallback);
        }else if(mMode == PlanCollectionType.CREATED){
            Button addBtn = (Button)findViewById(R.id.view_plans_add);
            addBtn.setVisibility(View.VISIBLE);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Plan plan = new Plan(null, AppContext.getCurrent().getActiveUser().getId(), null, null, null);
                    AppContext.getCurrent().getNavigationService().navigateChild(PlanUpdateView.class, HelperService.getSinglePairMap("plan", plan));
                    plan.addChangedHandler(new Callback() {
                        @Override
                        public void execute(CallbackParams params) {
                            AppContext.getCurrent().getActivity().showLoadingScreen();
                            mAdapter.load(loadCallback);
                        }
                    });
                }
            });
            mAdapter.load(loadCallback);
        }else{
            mAdapter.load(loadCallback);
        }
    }

    private Callback getLoadCallback(){
        return new Callback(){
            @Override
            public void execute(CallbackParams params) {
                if(mAdapter.getCount() == 0) {
                    mList.setVisibility(View.GONE);
                    TextView nullText = (TextView) findViewById(R.id.view_plans_null_text);
                    if(mMode == PlanCollectionType.ENROLLED) {
                        nullText.setText(R.string.plans_enrolled_null);
                    }else if(mMode == PlanCollectionType.CREATED) {
                        nullText.setText(R.string.plans_created_null);
                    }else{
                        nullText.setText(R.string.plans_browse_null);
                    }
                    findViewById(R.id.view_plans_null).setVisibility(View.VISIBLE);
                }else{
                    mList.setVisibility(View.VISIBLE);
                    findViewById(R.id.view_plans_null).setVisibility(View.GONE);
                }
            }
        };
    }
}
