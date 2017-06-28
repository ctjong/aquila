package com.projectaquila.views;

import android.graphics.Paint;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.models.ApiTaskMethod;
import com.projectaquila.models.Callback;
import com.projectaquila.models.S;
import com.projectaquila.models.Task;
import com.projectaquila.services.HelperService;

import java.util.Date;
import java.util.HashMap;

public class TaskUpdateView extends ViewBase {
    private Date mTaskDate;
    private TextView mTaskDateText;

    @Override
    protected int getLayoutId() {
        return R.layout.view_taskupdate;
    }

    @Override
    protected void initializeView(){
        String taskId = getNavArg("id");
        System.out.println("[TaskUpdateView.initializeView] task id = " + taskId);
        if(taskId == null || !AppContext.getCurrent().getTasks().containsKey(taskId)) {
            System.err.println("[TaskUpdateView.initializeView] invalid task id found in nav params");
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
            return;
        }

        final Task task = AppContext.getCurrent().getTasks().get(taskId);
        final EditText taskNameText = ((EditText)findViewById(R.id.taskupdate_taskname));
        taskNameText.setText(task.getName());

        mTaskDate = task.getDate();
        mTaskDateText = ((TextView)findViewById(R.id.taskupdate_taskdate));
        mTaskDateText.setPaintFlags(mTaskDateText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mTaskDateText.setKeyListener(null);
        mTaskDateText.setOnClickListener(HelperService.getDatePickerClickHandler(mTaskDate, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                mTaskDate = (Date)params.get("retval");
                updateView();
            }
        }));
        findViewById(R.id.taskupdate_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[TaskUpdateView.initializeView] saving");
                AppContext.getCurrent().getActivity().onBackPressed();
                AppContext.getCurrent().getActivity().showLoadingScreen();
                String updatedName = taskNameText.getText().toString();
                final String updatedDate = HelperService.getDateKey(mTaskDate);
                HashMap<String, String> data = new HashMap<>();
                data.put("taskname", updatedName);
                data.put("taskdate", updatedDate);
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/private/" + task.getId(), data, new Callback() {
                    @Override
                    public void execute(HashMap<String, Object> params, S s) {
                        HashMap<String, String> navParams = new HashMap<>();
                        navParams.put("date", updatedDate);
                        AppContext.getCurrent().getNavigationService().navigate(TasksView.class, navParams);
                    }
                });
            }
        });
        findViewById(R.id.taskupdate_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[TaskUpdateView.initializeView] cancelling");
                AppContext.getCurrent().getActivity().onBackPressed();
            }
        });

        updateView();
        AppContext.getCurrent().getActivity().setToolbarText(R.string.taskupdate_title);
        AppContext.getCurrent().getActivity().showContentScreen();
    }

    private void updateView(){
        mTaskDateText.setText(HelperService.getDateString("MM/dd/yyyy", mTaskDate));
    }
}
