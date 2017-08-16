package com.projectaquila.views;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.dataadapters.PlanCollectionAdapter;

public abstract class PlanCollectionView extends ViewBase {
    protected ListView mList;
    protected PlanCollectionAdapter mAdapter;
    protected TextView mNullText;
    protected View mMainView;
    protected View mNullView;
    protected Callback mLoadCallback;

    protected abstract int getNullTextStringId();
    protected abstract void setupPlanCollectionView() throws UnsupportedOperationException;

    @Override
    protected int getLayoutId() {
        return R.layout.view_plans;
    }

    @Override
    protected void initializeView(){
        mList = (ListView) findViewById(R.id.view_plans_list);
        mNullText = (TextView) findViewById(R.id.view_plans_null_text);
        mMainView = findViewById(R.id.view_plans_main);
        mNullView = findViewById(R.id.view_plans_null);
        mLoadCallback = getLoadCallback();

        try{
            setupPlanCollectionView();
            mList.setAdapter(mAdapter);
        }catch(UnsupportedOperationException e){
            System.err.println("[PlanCollection.initializeView] exception");
            e.printStackTrace();
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
        }
    }

    private Callback getLoadCallback(){
        return new Callback(){
            @Override
            public void execute(CallbackParams params) {
                mAdapter.sync();
                if(mAdapter.getCount() == 0) {
                    mMainView.setVisibility(View.GONE);
                    mNullText.setText(getNullTextStringId());
                    mNullView.setVisibility(View.VISIBLE);
                }else{
                    mMainView.setVisibility(View.VISIBLE);
                    mNullView.setVisibility(View.GONE);
                }
                AppContext.getCurrent().getActivity().hideLoadingScreen();
            }
        };
    }
}
