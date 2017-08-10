package com.projectaquila.controls;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.common.Callback;
import com.projectaquila.common.CallbackParams;
import com.projectaquila.common.TaskDate;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanEnrollment;
import com.projectaquila.services.HelperService;
import com.projectaquila.views.TaskCollectionView;

public class EnrollmentProgressControl {
    private PlanEnrollment mEnrollment;
    private TextView mStatusText;
    private View mShiftBtn;

    public EnrollmentProgressControl(PlanEnrollment enrollment, TextView statusText, View shiftButton){
        mEnrollment = enrollment;
        mStatusText = statusText;
        mShiftBtn = shiftButton;
        mShiftBtn.setOnClickListener(getShiftButtonClickHandler());
    }

    public void updateView(){
        if(mEnrollment == null){
            mStatusText.setVisibility(View.GONE);
            mShiftBtn.setVisibility(View.GONE);
            return;
        }
        Context ctx = AppContext.getCurrent().getActivity();
        int missedDays = getMissedDays();
        if(missedDays > 0){
            String statusFormat = ctx.getString(R.string.plantaskdetail_enrollstatus_missed);
            mStatusText.setText(statusFormat.replace("{numdays}", HelperService.toString(missedDays)));
            mShiftBtn.setVisibility(View.VISIBLE);
        }else{
            mStatusText.setText(ctx.getString(R.string.plantaskdetail_enrollstatus_ontrack));
            mShiftBtn.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener getShiftButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int missedDays = getMissedDays();
                if(missedDays == 0) return;
                mEnrollment.setStartDate(mEnrollment.getStartDate().getModified(missedDays));
                AppContext.getCurrent().getActivity().showLoadingScreen();
                mEnrollment.submitUpdate(new Callback() {
                    @Override
                    public void execute(CallbackParams params) {
                        AppContext.getCurrent().getNavigationService().goToMainActivity(TaskCollectionView.class, HelperService.getSinglePairMap("date", new TaskDate().toDateKey()));
                    }
                });
            }
        };
    }

    private int getMissedDays(){
        TaskDate today = new TaskDate();
        TaskDate incompleteStart = mEnrollment.getStartDate().getModified(mEnrollment.getCompletedDays());
        return (int)((today.getTime() - incompleteStart.getTime()) / 86400000);
    }
}
