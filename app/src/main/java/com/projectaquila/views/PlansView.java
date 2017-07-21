package com.projectaquila.views;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.projectaquila.AppContext;
import com.projectaquila.R;
import com.projectaquila.controls.PlansAdapter;
import com.projectaquila.models.PlansViewMode;

public class PlansView extends ViewBase {
    @Override
    protected int getLayoutId() {
        return R.layout.view_plans;
    }

    @Override
    protected void initializeView(){
        String modeStr = getNavArg("mode");
        System.out.println("[PlansView.initializeView] mode=" + modeStr);
        PlansViewMode mode = PlansViewMode.parse(modeStr);
        PlansAdapter adapter = new PlansAdapter(mode);
        ListView list = (ListView)findViewById(R.id.view_plans_list);
        list.setAdapter(adapter);
        if(mode == PlansViewMode.BROWSE){
            adapter.loadPart(0, 20);
        }else if(mode == PlansViewMode.CREATED){
            Button addBtn = (Button)findViewById(R.id.view_plans_add);
            addBtn.setVisibility(View.VISIBLE);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppContext.getCurrent().getNavigationService().navigateChild(PlanUpdateView.class, null);
                }
            });
            adapter.load();
        }else{
            adapter.load();
        }
        if(adapter.getCount() == 0) {
            list.setVisibility(View.GONE);
            TextView nullText = (TextView) findViewById(R.id.view_plans_null_text);
            if(mode == PlansViewMode.ENROLLED) {
                nullText.setText(R.string.plans_enrolled_null);
            }else if(mode == PlansViewMode.CREATED) {
                nullText.setText(R.string.plans_created_null);
            }else{
                nullText.setText(R.string.plans_browse_null);
            }
            findViewById(R.id.view_plans_null).setVisibility(View.VISIBLE);
        }
    }
}
