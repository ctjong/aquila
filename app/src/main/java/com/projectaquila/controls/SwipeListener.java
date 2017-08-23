package com.projectaquila.controls;


import android.view.MotionEvent;
import android.view.View;

import com.projectaquila.common.Callback;

public class SwipeListener implements View.OnTouchListener {
    private static final int ClickTolerance = 50;

    private Callback mLeftSwipeHandler;
    private Callback mRightSwipeHandler;
    private Callback mClickHandler;
    private int mDragMinX;

    private View mDraggable;
    private Integer mTouchStartX;
    private boolean mIsPossibleClickAction;

    public static void listen(View view, View draggable, Callback leftSwipeHandler, Callback rightSwipeHandler, Callback clickHandler, int dragMinX){
        SwipeListener listener = new SwipeListener(leftSwipeHandler, rightSwipeHandler, clickHandler, draggable, dragMinX);
        view.setOnTouchListener(listener);
    }

    private SwipeListener(Callback leftSwipeHandler, Callback rightSwipeHandler, Callback clickHandler, View draggable, int dragMinX){
        mLeftSwipeHandler = leftSwipeHandler;
        mRightSwipeHandler = rightSwipeHandler;
        mClickHandler = clickHandler;
        mDraggable = draggable;
        mDragMinX = dragMinX;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int pointerX = (int)motionEvent.getRawX();
        if(action == MotionEvent.ACTION_DOWN) {
            mTouchStartX = pointerX;
            mIsPossibleClickAction = true;
        }else if(mTouchStartX == null){
            return true;
        }else if(action == MotionEvent.ACTION_MOVE) {
            if(evaluateSwipe(pointerX)) return true;
            mDraggable.setTranslationX(pointerX - mTouchStartX);
            if(Math.abs(pointerX - mTouchStartX) > ClickTolerance) mIsPossibleClickAction = false;
        }else if(action == MotionEvent.ACTION_CANCEL) {
            resetSwipe();
        }else if(action == MotionEvent.ACTION_UP){
            if(evaluateSwipe(pointerX)) return true;
            if(mIsPossibleClickAction){
                System.out.println("[SwipeListener.onTouch] click triggered");
                if(mClickHandler != null) mClickHandler.execute(null);
            }
            resetSwipe();
        }
        return true;
    }

    private boolean evaluateSwipe(int pointerX){
        if(pointerX - mTouchStartX > mDragMinX){
            System.out.println("[SwipeListener.onTouch] right swipe triggered");
            if(mRightSwipeHandler != null) {
                mRightSwipeHandler.execute(null);
                resetSwipe();
                return true;
            }
        }else if(mTouchStartX - pointerX > mDragMinX){
            System.out.println("[SwipeListener.onTouch] left swipe triggered");
            if(mLeftSwipeHandler != null) {
                mLeftSwipeHandler.execute(null);
                resetSwipe();
                return true;
            }
        }
        return false;
    }

    private void resetSwipe(){
        mTouchStartX = null;
        mDraggable.setTranslationX(0);
    }
}