package com.nested.viewpager;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by lizhifeng on 2018/6/28.
 */

public class MyRecyclerView extends RecyclerView {
    float lastY = 0;
    float lastX = 0;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("recycler   touch_event", "dispatchTouchEvent  " + super.dispatchTouchEvent(ev));
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        Log.e("recycler   touch_event", "onInterceptTouchEvent  " + super.onInterceptTouchEvent(e));
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean r = false;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getY();
                lastX = ev.getX();
                r = super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = ev.getY() - lastY;
                float dx = ev.getX() - lastX;
                if (Math.abs(dy) > Math.abs(dx)) {
                    Log.e("StickTopLayout_1", false + "  ");
//                        getParent().requestDisallowInterceptTouchEvent(false);
                    r = false;
                } else {
                    r = super.onTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastX = ev.getX();
                lastY = ev.getY();
                r = super.onTouchEvent(ev);
                break;
        }
        Log.e("recycler   touch_event", "onTouchEvent  " + r);
        return r;
    }
}
