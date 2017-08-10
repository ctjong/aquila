package com.projectaquila.drawer;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;

public abstract class DrawerItem {
    private int mContentHeightResId;
    private int mBgColorResId;
    private int mFgColorResId;

    protected DrawerItem(int heightResId, int bgColorResId, int fgColorResId){
        mContentHeightResId = heightResId;
        mBgColorResId = bgColorResId;
        mFgColorResId = fgColorResId;
    }

    public abstract String getLine1String();
    public abstract String getLine2String();
    public abstract String getImageUrl();
    public abstract void invoke();

    public void updateLayout(View layout) {
        Context ctx = AppContext.getCurrent().getActivity();
        View contentView = layout.findViewById(R.id.draweritem_content);
        ViewGroup.LayoutParams params = contentView.getLayoutParams();
        params.height = (int)ctx.getResources().getDimension(mContentHeightResId);
        contentView.setLayoutParams(params);

        int bcColor = ContextCompat.getColor(ctx, mBgColorResId);
        int fgColor = ContextCompat.getColor(ctx, mFgColorResId);

        layout.findViewById(R.id.draweritem_control).setBackgroundColor(bcColor);
        ((TextView)layout.findViewById(R.id.draweritem_line1)).setTextColor(fgColor);
        ((TextView)layout.findViewById(R.id.draweritem_line2)).setTextColor(fgColor);
    }
}
