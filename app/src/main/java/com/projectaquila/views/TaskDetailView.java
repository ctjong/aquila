package com.projectaquila.views;

import com.projectaquila.AppContext;
import com.projectaquila.R;

public class TaskDetailView extends ViewBase {

    @Override
    protected int getLayoutId() {
        return R.layout.view_taskdetail;
    }

    @Override
    protected void initializeView(){
        AppContext.getCurrent().getActivity().showContentScreen();
    }
}
