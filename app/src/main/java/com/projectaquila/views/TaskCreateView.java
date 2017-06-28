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
import com.projectaquila.services.HelperService;

import java.util.Date;
import java.util.HashMap;

public class TaskCreateView extends ViewBase {
    private Date mTaskDate;
    private EditText mTaskNameText;
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

        mTaskNameText = ((EditText)findViewById(R.id.taskupdate_taskname));
        mTaskNameText.setText(taskName);
        mTaskDateText = ((TextView)findViewById(R.id.taskupdate_taskdate));
        mTaskDateText.setPaintFlags(mTaskDateText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mTaskDateText.setKeyListener(null);
        mTaskDateText.setOnClickListener(getDateTextClickHandler());
        findViewById(R.id.taskupdate_save_btn).setOnClickListener(getSaveButtonClickHandler());
        findViewById(R.id.taskupdate_cancel_btn).setOnClickListener(getCancelButtonClickHandler());

        updateView();
        AppContext.getCurrent().getActivity().setToolbarText(R.string.taskcreate_title);
        AppContext.getCurrent().getActivity().showContentScreen();
    }

    private View.OnClickListener getDateTextClickHandler(){
        return HelperService.getDatePickerClickHandler(mTaskDate, new Callback() {
            @Override
            public void execute(HashMap<String, Object> params, S s) {
                mTaskDate = (Date)params.get("retval");
                updateView();
            }
        });
    }

    private View.OnClickListener getSaveButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[TaskCreateView.getSaveButtonClickHandler] saving");
                AppContext.getCurrent().getActivity().onBackPressed();
                AppContext.getCurrent().getActivity().showLoadingScreen();
                String updatedName = mTaskNameText.getText().toString();
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
        };
    }

    private View.OnClickListener getCancelButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[TaskCreateView.getCancelButtonClickHandler] cancelling");
                AppContext.getCurrent().getActivity().onBackPressed();
            }
        };
    }

    private void updateView(){
        mTaskDateText.setText(HelperService.getDateString("MM/dd/yyyy", mTaskDate));
    }
}
