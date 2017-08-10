package com.projectaquila.views;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.dataadapters.PlanCollectionAdapter;
import com.projectaquila.datamodels.PlanCollection;

public abstract class PlanCollectionView extends ViewBase {
    protected ListView mList;
    protected PlanCollectionAdapter mAdapter;
    protected TextView mNullText;
    protected View mNullView;
    protected Callback mLoadCallback;

    protected abstract PlanCollection getPlans();
    protected abstract int getNullTextStringId();

    @Override
    protected int getLayoutId() {
        return R.layout.view_plans;
    }

    protected boolean tryInitVars(){
        mList = (ListView) findViewById(R.id.view_plans_list);
        mNullText = (TextView) findViewById(R.id.view_plans_null_text);
        mNullView = findViewById(R.id.view_plans_null);
        mLoadCallback = getLoadCallback();
        try{
            mAdapter = new PlanCollectionAdapter(getPlans());
            mList.setAdapter(mAdapter);
            return true;
        }catch(UnsupportedOperationException e){
            System.err.println("[PlanCollection.tryInitVars] exception");
            e.printStackTrace();
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
            return false;
        }
    }

    private Callback getLoadCallback(){
        return new Callback(){
            @Override
            public void execute(CallbackParams params) {
                mAdapter.sync();
                if(mAdapter.getCount() == 0) {
                    mList.setVisibility(View.GONE);
                    mNullText.setText(getNullTextStringId());
                    mNullView.setVisibility(View.VISIBLE);
                }else{
                    mList.setVisibility(View.VISIBLE);
                    mNullView.setVisibility(View.GONE);
                }
                AppContext.getCurrent().getActivity().hideLoadingScreen();
            }
        };
    }
}
