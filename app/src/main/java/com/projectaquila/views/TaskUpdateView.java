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
    private Date mUpdatedDate;
    private TextView mTaskDateText;

    @Override
    protected int getLayoutId() {
        return R.layout.view_taskupdate;
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
        final EditText taskNameText = ((EditText)findViewById(R.id.taskupdate_taskname));
        taskNameText.setText(task.getName());

        mUpdatedDate = task.getDate();
        mTaskDateText = ((TextView)findViewById(R.id.taskupdate_taskdate));
        mTaskDateText.setPaintFlags(mTaskDateText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mTaskDateText.setKeyListener(null);
        mTaskDateText.setOnClickListener(HelperService.getDatePickerClickHandler(mUpdatedDate, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                mUpdatedDate = (Date)params.get("retval");
                updateView();
            }
        }));
        findViewById(R.id.taskupdate_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = taskNameText.getText().toString();
                HashMap<String, String> data = new HashMap<>();
                data.put("taskname", updatedName);
                data.put("taskdate", HelperService.getDateKey(mUpdatedDate));
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.PUT, "/data/task/private/" + task.getId(), data, null);
                task.setDate(mUpdatedDate);
                task.setName(updatedName);
                task.notifyListeners();
                AppContext.getCurrent().getActivity().onBackPressed();
            }
        });
        findViewById(R.id.taskupdate_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getCurrent().getActivity().onBackPressed();
            }
        });

        updateView();
        AppContext.getCurrent().getActivity().setToolbarText(R.string.taskupdate_title);
        AppContext.getCurrent().getActivity().showContentScreen();
    }

    private void updateView(){
        mTaskDateText.setText(HelperService.getDateString("MM/dd/yyyy", mUpdatedDate));
    }
}
