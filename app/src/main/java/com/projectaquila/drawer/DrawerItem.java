package com.projectaquila.drawer;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projectaquila.R;
import com.projectaquila.contexts.AppContext;

public abstract class DrawerItem {
    private int mContentHeight;
    private int mBgColorResId;
    private int mFgColorResId;

    protected DrawerItem(){
        mContentHeight = 40;
        mBgColorResId = R.color.white;
        mFgColorResId = R.color.gray;
    }

    protected DrawerItem(int height, int bgColorResId, int fgColorResId){
        mContentHeight = height;
        mBgColorResId = bgColorResId;
        mFgColorResId = fgColorResId;
    }

    public abstract String getLine1String();
    public abstract String getLine2String();
    public abstract String getImageUrl();
    public abstract void invoke();

    public void updateLayout(View layout) {
        View contentView = layout.findViewById(R.id.draweritem_content);
        ViewGroup.LayoutParams params = contentView.getLayoutParams();
        params.height = mContentHeight;
        contentView.setLayoutParams(params);

        Context ctx = AppContext.getCurrent().getActivity();
        int bcColor = ContextCompat.getColor(ctx, mBgColorResId);
        int fgColor = ContextCompat.getColor(ctx, mFgColorResId);

        layout.findViewById(R.id.draweritem_control).setBackgroundColor(bcColor);
        ((TextView)layout.findViewById(R.id.draweritem_line1)).setTextColor(fgColor);
        ((TextView)layout.findViewById(R.id.draweritem_line2)).setTextColor(fgColor);
    }
}
