package com.projectaquila.views;

import android.widget.AbsListView;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanCollection;
import com.projectaquila.datamodels.PublicPlanCollection;

public class PublicPlanCollectionView extends PlanCollectionView implements AbsListView.OnScrollListener {
    private static final int ITEMS_PER_PART = 20;
    private PlanCollection mPlans;
    private int mLoadPartCount;

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
        AppContext.getCurrent().getActivity().showLoadingScreen();
        mLoadPartCount = 0;
        mPlans.loadItemsPart(mLoadPartCount, ITEMS_PER_PART, mLoadCallback);
        mList.setOnScrollListener(this);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastItem = firstVisibleItem + visibleItemCount;
        int threshold = (mLoadPartCount + 1) * ITEMS_PER_PART;
        if(lastItem == totalItemCount && totalItemCount == threshold && !mPlans.isAllLoaded())
        {
            mLoadPartCount++;
            System.out.println("[PublicPlanCollectionView.onScroll] scrolled to last item. loading part " + mLoadPartCount);
            mPlans.loadItemsPart(mLoadPartCount, ITEMS_PER_PART, mLoadCallback);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}
