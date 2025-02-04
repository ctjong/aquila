package com.planmaster.views;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;

import com.planmaster.R;
import com.planmaster.activities.ShellActivity;
import com.planmaster.contexts.AppContext;
import com.planmaster.dataadapters.PlanCollectionAdapter;
import com.planmaster.datamodels.PublicPlanCollection;

public class PublicPlanCollectionView extends PlanCollectionView implements AbsListView.OnScrollListener {
    private static final int ITEMS_PER_PART = 20;
    private PublicPlanCollection mPlans;
    private int mLoadPartCount;

    @Override
    protected int getTitleBarStringId() {
        return R.string.menu_browse_plans;
    }

    @Override
    protected int getNullTextStringId() {
        return R.string.plans_browse_null;
    }

    @Override
    protected void setupPlanCollectionView() throws UnsupportedOperationException {
        System.out.println("[PublicPlanCollectionView.setupPlanCollectionView] started");
        mPlans = new PublicPlanCollection();
        mAdapter = new PlanCollectionAdapter(mPlans, false);
        mList.setOnScrollListener(this);

        // show filter controls
        ShellActivity shell = AppContext.getCurrent().getActivity();
        findViewById(R.id.view_plans_filter).setVisibility(View.VISIBLE);
        findViewById(R.id.view_plans_topmargin).setVisibility(View.VISIBLE);

        // init filter controls
        SearchView searchView = (SearchView)findViewById(R.id.view_plans_search);
        searchView.setOnQueryTextListener(getSearchHandler());
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(shell, R.layout.control_spinneritem);
        sortAdapter.add(shell.getString(R.string.plans_sort_createdtime));
        sortAdapter.add(shell.getString(R.string.plans_sort_name));
        sortAdapter.add(shell.getString(R.string.plans_sort_ownerid));
        Spinner sortSpinner = (Spinner) findViewById(R.id.view_plans_sort);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setOnItemSelectedListener(getSortSpinnerHandler());
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastItem = firstVisibleItem + visibleItemCount;
        int threshold = (mLoadPartCount + 1) * ITEMS_PER_PART;
        if(lastItem == totalItemCount && totalItemCount == threshold && !mPlans.isAllLoaded()) {
            mLoadPartCount++;
            System.out.println("[PublicPlanCollectionView.onScroll] scrolled to last item. loading part " + mLoadPartCount);
            mPlans.loadItemsPart(mLoadPartCount, ITEMS_PER_PART, mLoadCallback);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    private SearchView.OnQueryTextListener getSearchHandler(){
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("[PublicPlanCollectionView.getSearchHandler] searching: " + query);
                mPlans.setSearchQuery(query);
                loadFirstPart();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    System.out.println("[PublicPlanCollectionView.getSearchHandler] clearing search query");
                    mPlans.setSearchQuery(null);
                    loadFirstPart();
                }
                return true;
            }
        };
    }

    private AdapterView.OnItemSelectedListener getSortSpinnerHandler(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newSortByField;
                if(position == 0){
                    newSortByField = "~createdtime";
                }else if(position == 1){
                    newSortByField = "name";
                }else{
                    newSortByField = "ownerid";
                }
                if(mPlans.getSortByField().equals(newSortByField)) return;
                System.out.println("[PublicPlanCollectionView.getSortSpinnerHandler] sorting by: " + newSortByField);
                mPlans.setSortByField(newSortByField);
                loadFirstPart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    private void loadFirstPart(){
        AppContext.getCurrent().getActivity().showLoadingScreen();
        mLoadPartCount = 0;
        mList.smoothScrollToPosition(0);
        mPlans.getItems().clear();
        mPlans.loadItemsPart(mLoadPartCount, ITEMS_PER_PART, mLoadCallback);
    }
}
