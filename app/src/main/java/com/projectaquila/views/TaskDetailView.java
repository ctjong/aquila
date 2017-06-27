package com.projectaquila.views;

import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.Task;
import com.projectaquila.services.HelperService;

public class TaskDetailView extends ViewBase {
    private Task mTask;

    @Override
    protected int getLayoutId() {
        return R.layout.view_taskdetail;
    }

    @Override
    protected void initializeView(){
        String taskId = getNavArg("id");
        if(taskId == null || !AppContext.getCurrent().getTasks().containsKey(taskId)) {
            System.err.println("[TaskDetailView.initializeView] invalid task id found in nav params");
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
            return;
        }
        mTask = AppContext.getCurrent().getTasks().get(taskId);
        ((TextView)findViewById(R.id.taskdetail_taskname)).setText(mTask.getName());
        ((TextView)findViewById(R.id.taskdetail_taskdate)).setText(HelperService.getDateString("MM/dd/yyyy", mTask.getDate()));

        AppContext.getCurrent().getActivity().setToolbarText(R.string.taskdetail_title);
        AppContext.getCurrent().getActivity().showContentScreen();
    }
}
