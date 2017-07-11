package com.projectaquila.views;

import android.provider.Settings;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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
