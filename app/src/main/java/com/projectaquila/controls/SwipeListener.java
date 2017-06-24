package com.projectaquila.controls;


import android.view.MotionEvent;
import android.view.View;

import com.projectaquila.models.Callback;
import com.projectaquila.models.S;

public class SwipeListener implements View.OnTouchListener {
    private static final int DragMinX = 200;
    private static final int ClickMaxX = 10;

    private Callback mLeftSwipeHandler;
    private Callback mRightSwipeHandler;
    private Callback mClickHandler;

    private View mDraggable;
    private Integer mTouchStartX;


    public static void listen(View view, View draggable, Callback leftSwipeHandler, Callback rightSwipeHandler, Callback clickHandler){
        SwipeListener listener = new SwipeListener();
        listener.mLeftSwipeHandler = leftSwipeHandler;
        listener.mRightSwipeHandler = rightSwipeHandler;
        listener.mClickHandler = clickHandler;
        listener.mDraggable = draggable;

        view.setOnTouchListener(listener);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int pointerX = (int)motionEvent.getX();
        if(action == MotionEvent.ACTION_DOWN) {
            mTouchStartX = pointerX;
        }else if(mTouchStartX == null){
            return true;
        }else if(action == MotionEvent.ACTION_MOVE) {
            setDraggableX(pointerX - mTouchStartX);
        }else if(action == MotionEvent.ACTION_OUTSIDE ||action == MotionEvent.ACTION_CANCEL) {
            mTouchStartX = null;
            setDraggableX(0);
        }else if(action == MotionEvent.ACTION_UP){
            float delta = Math.abs(pointerX - mTouchStartX);
            if(pointerX - mTouchStartX > DragMinX && mRightSwipeHandler != null){
                mRightSwipeHandler.execute(null, S.OK);
            }else if(mTouchStartX - pointerX > DragMinX && mLeftSwipeHandler != null){
                mLeftSwipeHandler.execute(null, S.OK);
            }else if(delta < ClickMaxX && mClickHandler != null){
                mClickHandler.execute(null, S.OK);
            }
            mTouchStartX = null;
            setDraggableX(0);
        }
        return true;
    }

    private void setDraggableX(int x){
        mDraggable.setTranslationX(x);
        /*int leftMargin = 0;
        int rightMargin = 0;
        if(x > 0){
            leftMargin = x;
        }else{
            rightMargin = x * -1;
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mDraggable.getLayoutParams();
        layoutParams.leftMargin = leftMargin;
        layoutParams.rightMargin = rightMargin;
        mDraggable.setLayoutParams(layoutParams);*/
    }
}