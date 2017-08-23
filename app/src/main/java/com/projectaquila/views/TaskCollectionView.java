package com.projectaquila.views;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.projectaquila.R;
import com.projectaquila.activities.ShellActivity;
import com.projectaquila.common.Callback;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.TaskDate;
import com.projectaquila.controls.DailyTasksControl;

import java.util.ArrayList;
import java.util.List;

public class TaskCollectionView extends ViewBase implements ViewPager.OnPageChangeListener {
    private ShellActivity mShell;
    private ViewPager mPager;
    private TasksPagerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.view_tasks;
    }

    @Override
    protected int getTitleBarStringId() {
        return R.string.menu_tasks;
    }

    @Override
    protected void initializeView(){
        mShell = AppContext.getCurrent().getActivity();
        Object dateObj = getNavArgObj("date");
        final TaskDate startDate = dateObj != null ? (TaskDate)dateObj : new TaskDate();
        mAdapter = new TasksPagerAdapter();
        mPager = (ViewPager)findViewById(R.id.view_tasks_pager);
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(this);

        mShell.showLoadingScreen();
        AppContext.getCurrent().getTasks().loadItems(new Callback() {
            @Override
            public void execute(CallbackParams params) {
                mAdapter.push(new DailyTasksControl(mShell, null, startDate.getModified(-1)));
                mAdapter.push(new DailyTasksControl(mShell, null, startDate));
                mAdapter.push(new DailyTasksControl(mShell, null, startDate.getModified(1)));
                mPager.setCurrentItem(1);
                mShell.hideLoadingScreen();
            }
        });
    }

    @Override
    public void onNavigatedFrom(){
        AppContext.getCurrent().getTasks().removeChangedHandlers();
    }

    @Override
    public void onPageSelected(int position) {
        TaskDate currentDate = mAdapter.getItem(position).getCurrentDate();
        if(position == 0){
            mAdapter.pushToFront(new DailyTasksControl(mShell, null, currentDate.getModified(-1)));
        }else if(position == mAdapter.getCount() - 1){
            mAdapter.push(new DailyTasksControl(mShell, null, currentDate.getModified(1)));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private class TasksPagerAdapter extends PagerAdapter {
        private List<DailyTasksControl> mViews;

        public TasksPagerAdapter(){
            mViews = new ArrayList<>();
        }

        public DailyTasksControl getItem(int position){
            return mViews.get(position);
        }

        public void push(DailyTasksControl view){
            mViews.add(view);
            notifyDataSetChanged();
        }

        public void pushToFront(DailyTasksControl view){
            mViews.add(0, view);
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object){
            DailyTasksControl view = (DailyTasksControl)object;
            return mViews.indexOf(view);
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            DailyTasksControl view = mViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            DailyTasksControl view = mViews.get(position);
            container.removeView(view);
        }
    }
}
