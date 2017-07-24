package com.projectaquila.views;

import android.view.View;
import android.widget.EditText;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;
import com.projectaquila.datamodels.PlanItem;
import com.projectaquila.services.HelperService;

public class PlanItemUpdateView extends ViewBase {
    private PlanItem mPlanItem;
    private EditText mNameText;
    private EditText mDescText;

    /**
     * Get layout id
     * @return layout id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.view_planitemupdate;
    }

    /**
     * Get title bar string id
     * @return title bar string id
     */
    @Override
    protected int getTitleBarStringId(){
        if(mPlanItem.getParent().getId() != null){
            return R.string.planupdate_title;
        }
        return R.string.plancreate_title;
    }

    /**
     * Initialize the view
     */
    @Override
    protected void initializeView(){
        mPlanItem = (PlanItem)getNavArgObj("planitem");
        System.out.println("[PlanItemUpdateView.initializeView] starting. planItemId=" + HelperService.safePrint(mPlanItem.getId()));
        mNameText = (EditText)findViewById(R.id.planitemupdate_name);
        mDescText = (EditText)findViewById(R.id.planitemupdate_desc);
        mNameText.setText(mPlanItem.getName());
        mDescText.setText(mPlanItem.getDescription());
        findViewById(R.id.planitemupdate_save_btn).setOnClickListener(getSaveButtonClickHandler());
        findViewById(R.id.planitemupdate_cancel_btn).setOnClickListener(getCancelButtonClickHandler());
        AppContext.getCurrent().getActivity().showContentScreen();
    }

    /**
     * Get click handler for the save button
     * @return click handler
     */
    private View.OnClickListener getSaveButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanItemUpdateView.getSaveButtonClickHandler] saving");
                mPlanItem.setName(mNameText.getText().toString());
                mPlanItem.setDescription(mDescText.getText().toString());
                AppContext.getCurrent().getNavigationService().goBack();
                mPlanItem.notifyListeners();
            }
        };
    }

    /**
     * Get click handler for the cancel button
     * @return click handler
     */
    private View.OnClickListener getCancelButtonClickHandler(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("[PlanItemUpdateView.getCancelButtonClickHandler] cancelling");
                AppContext.getCurrent().getNavigationService().goBack();
            }
        };
    }
}
