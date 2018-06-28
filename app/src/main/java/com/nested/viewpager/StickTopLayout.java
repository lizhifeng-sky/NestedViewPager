package com.nested.viewpager;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.ScrollView;


/**
 * Created by lizhifeng on 2018/6/28.
 */

public class StickTopLayout extends LinearLayout {
    float lastY = 0;
    float lastX = 0;
    int mTouchSlop;

    public StickTopLayout(Context context) {
        this(context, null);
    }

    public StickTopLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickTopLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastY = ev.getY();
//                lastX = ev.getX();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float dy = ev.getY() - lastY;
//                float dx = ev.getX() - lastX;
//                if (Math.abs(dy) > mTouchSlop | Math.abs(dx) > mTouchSlop) {
//                    if (Math.abs(dy) > Math.abs(dx)) {
//                        Log.e("StickTopLayout_1",  false+"  ");
////                        getParent().requestDisallowInterceptTouchEvent(false);
//                        return false;
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                lastX = ev.getX();
//                lastY = ev.getY();
//                break;
//        }
        Log.e("子 touch_event",  " dispatchTouchEvent  "+super.dispatchTouchEvent(ev)+"  ");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("子 touch_event",  "  onInterceptTouchEvent "+super.onInterceptTouchEvent(ev)+"  ");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastY = ev.getY();
//                lastX = ev.getX();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float dy = ev.getY() - lastY;
//                float dx = ev.getX() - lastX;
//                if (Math.abs(dy) > mTouchSlop | Math.abs(dx) > mTouchSlop) {
//                    if (Math.abs(dy) > Math.abs(dx)) {
//                        Log.e("StickTopLayout_1",  false+"  ");
////                        getParent().requestDisallowInterceptTouchEvent(false);
//                        return false;
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                lastX = ev.getX();
//                lastY = ev.getY();
//                break;
//        }
        Log.e("子 touch_event",  " onTouchEvent  "+super.onTouchEvent(ev)+"  ");
        return super.onTouchEvent(ev);
    }
}
