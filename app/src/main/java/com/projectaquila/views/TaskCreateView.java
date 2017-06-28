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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TaskCreateView extends ViewBase {
    private Date mTaskDate;
    private TextView mTaskDateText;

    @Override
    protected int getLayoutId() {
        return R.layout.view_taskupdate;
    }

    @Override
    protected void initializeView(){
        String taskName = getNavArg("taskname");
        mTaskDate = HelperService.parseDateKey(getNavArg("taskdate"));
        if(taskName == null || mTaskDate == null) {
            System.err.println("[TaskCreateView.initializeView] missing task name/date");
            AppContext.getCurrent().getActivity().showErrorScreen(R.string.shell_error_unknown);
            return;
        }

        final EditText taskNameText = ((EditText)findViewById(R.id.taskupdate_taskname));
        taskNameText.setText(taskName);

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
                System.out.println("[TaskCreateView.initializeView] saving");
                AppContext.getCurrent().getActivity().onBackPressed();
                AppContext.getCurrent().getActivity().showLoadingScreen();
                String updatedName = taskNameText.getText().toString();
                final String updatedDate = HelperService.getDateKey(mTaskDate);
                HashMap<String, String> data = new HashMap<>();
                data.put("taskname", updatedName);
                data.put("taskdate", updatedDate);
                AppContext.getCurrent().getDataService().request(ApiTaskMethod.POST, "/data/task/public", data, new Callback() {
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
                System.out.println("[TaskCreateView.initializeView] cancelling");
                AppContext.getCurrent().getActivity().onBackPressed();
            }
        });

        updateView();
        AppContext.getCurrent().getActivity().setToolbarText(R.string.taskcreate_title);
        AppContext.getCurrent().getActivity().showContentScreen();
    }

    private void updateView(){
        mTaskDateText.setText(HelperService.getDateString("MM/dd/yyyy", mTaskDate));
    }
}
