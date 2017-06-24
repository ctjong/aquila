package com.projectaquila.controls;


import android.view.MotionEvent;
import android.view.View;

import com.projectaquila.models.Callback;
import com.projectaquila.models.S;

public class SwipeListener implements View.OnTouchListener{
    private View mDraggable;
    private static final int DragMinX = 200;
    private static final int ClickMaxX = 10;

    private Callback mLeftSwipeHandler;
    private Callback mRightSwipeHandler;
    private Callback mClickHandler;

    private float mDraggableX;
    private float mTouchStartX;

    public SwipeListener(View draggable, Callback leftSwipeHandler, Callback rightSwipeHandler, Callback clickHandler){
        mDraggable = draggable;
        mLeftSwipeHandler = leftSwipeHandler;
        mRightSwipeHandler = rightSwipeHandler;
        mClickHandler = clickHandler;
        mDraggableX = draggable.getX();
    }

    private float getNewDraggableX(float pointerX){
        if(Float.isNaN(mTouchStartX) || Float.isNaN(pointerX)) return mDraggableX;
        return pointerX - mTouchStartX + mDraggableX;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        float pointerX = motionEvent.getX();
        float newDraggableX = getNewDraggableX(pointerX);
        if(action == MotionEvent.ACTION_DOWN){
            mTouchStartX = pointerX;
        }else if(action == MotionEvent.ACTION_MOVE){
            mDraggable.setX(newDraggableX);
        }else if(action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_OUTSIDE ||
                action == MotionEvent.ACTION_CANCEL){
            float delta = Math.abs(pointerX - mTouchStartX);
            if(pointerX - mTouchStartX > DragMinX && mRightSwipeHandler != null){
                mRightSwipeHandler.execute(null, S.OK);
            }else if(mTouchStartX - pointerX > DragMinX && mLeftSwipeHandler != null){
                mLeftSwipeHandler.execute(null, S.OK);
            }else if(delta < ClickMaxX && mClickHandler != null){
                mClickHandler.execute(null, S.OK);
            }
            mTouchStartX = Float.NaN;
            mDraggable.setX(mDraggableX);
        }
        return true;
    }
}