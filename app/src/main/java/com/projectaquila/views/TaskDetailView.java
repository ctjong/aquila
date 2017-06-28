package com.projectaquila.views;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.Task;
import com.projectaquila.services.HelperService;

import java.util.HashMap;

public class TaskDetailView extends ViewBase {
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

        final Task task = AppContext.getCurrent().getTasks().get(taskId);
        ((TextView)findViewById(R.id.taskdetail_taskname)).setText(task.getName());
        ((TextView)findViewById(R.id.taskdetail_taskdate)).setText(HelperService.getDateString("MM/dd/yyyy", task.getDate()));
        findViewById(R.id.taskdetail_edit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> navParams = new HashMap<>();
                navParams.put("id", task.getId());
                AppContext.getCurrent().getNavigationService().navigate(TaskUpdateView.class, navParams);
            }
        });

        AppContext.getCurrent().getActivity().setToolbarText(R.string.taskdetail_title);
        AppContext.getCurrent().getActivity().showContentScreen();
    }
}
