package com.projectaquila.views;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.R;
import com.projectaquila.adapters.PlanCollectionAdapter;
import com.projectaquila.common.PlanCollectionType;
import com.projectaquila.datamodels.Plan;
import com.projectaquila.services.HelperService;

public class PlanCollectionView extends ViewBase {
    private ListView mList;
    private PlanCollectionAdapter mAdapter;
    private PlanCollectionType mMode;
    private TextView mNullText;
    private View mNullView;

    @Override
    protected int getLayoutId() {
        return R.layout.view_plans;
    }

    @Override
    protected int getTitleBarStringId() {
        if(mMode == PlanCollectionType.ENROLLED){
            return R.string.menu_enrolled_plans;
        }else if(mMode == PlanCollectionType.BROWSE) {
            return R.string.menu_browse_plans;
        }else{
            return R.string.menu_created_plans;
        }
    }

    @Override
    protected void initializeView(){
        try {
            final String modeStr = getNavArgStr("mode");
            System.out.println("[PlanCollectionView.initializeView] mode=" + modeStr);
            mMode = PlanCollectionType.parse(modeStr);
            mAdapter = new PlanCollectionAdapter(mMode);
            mList = (ListView) findViewById(R.id.view_plans_list);
            mNullText = (TextView) findViewById(R.id.view_plans_null_text);
            mNullView = findViewById(R.id.view_plans_null);
            mList.setAdapter(mAdapter);
            final Callback loadCallback = getLoadCallback();
            if (mMode == PlanCollectionType.BROWSE) {
                //TODO pagination
                mAdapter.loadPart(0, 20, loadCallback);
            } else if (mMode == PlanCollectionType.CREATED) {
                Button addBtn = (Button) findViewById(R.id.view_plans_add);
                addBtn.setVisibility(View.VISIBLE);
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Plan plan = new Plan(null, AppContext.getCurrent().getActiveUser().getId(), 0, null, null, null, null);
                        AppContext.getCurrent().getNavigationService().navigateChild(PlanUpdateView.class, HelperService.getSinglePairMap("plan", plan));
                        plan.addChangedHandler(new Callback() {
                            @Override
                            public void execute(CallbackParams params) {
                                mAdapter.load(loadCallback);
                            }
                        });
                    }
                });
                mAdapter.load(loadCallback);
            } else {
                mAdapter.load(loadCallback);
                AppContext.getCurrent().getEnrollments().addChangedHandler(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        mAdapter.load(loadCallback);
                    }
                });
            }
        }catch(UnsupportedOperationException e){
            System.err.println("[PlanCollectionView.initializeView] exception");
            e.printStackTrace();
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
        }
    }

    private Callback getLoadCallback(){
        return new Callback(){
            @Override
            public void execute(CallbackParams params) {
                if(mAdapter.getCount() == 0) {
                    mList.setVisibility(View.GONE);
                    if(mMode == PlanCollectionType.ENROLLED) {
                        mNullText.setText(R.string.plans_enrolled_null);
                    }else if(mMode == PlanCollectionType.CREATED) {
                        mNullText.setText(R.string.plans_created_null);
                    }else{
                        mNullText.setText(R.string.plans_browse_null);
                    }
                    mNullView.setVisibility(View.VISIBLE);
                }else{
                    mList.setVisibility(View.VISIBLE);
                    mNullView.setVisibility(View.GONE);
                }
            }
        };
    }
}
