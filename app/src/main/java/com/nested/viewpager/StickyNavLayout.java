package com.nested.viewpager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * Created by lizhifeng on 2018/6/22.
 */

public class StickyNavLayout extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "StickyNavLayout";
    private int minDuration = 600;

    private View mTop;
    private View mNav;
    private ViewPager mViewPager;

    private int mTopViewHeight;

    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private ValueAnimator mOffsetAnimator;
    private int mTouchSlop;
    private int mMaximumVelocity, mMinimumVelocity;

    private float mLastY;
    private boolean mDragging;

    private int TOP_CHILD_FLING_THRESHOLD = 3;

    public StickyNavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);

        mScroller = new OverScroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
//        mMaximumVelocity=2000;
//        mMinimumVelocity=800;

    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //如果动画或者 scroller 正在执行  不接受事件
        if ((mOffsetAnimator != null && mOffsetAnimator.isRunning()) | (mScroller != null && !mScroller.isFinished())) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                if (!mDragging && Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                }
                if (mDragging) {
                    scrollBy(0, (int) -dy);
                }
                mLastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                recycleVelocityTracker();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                }
                recycleVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onFinishInflate() {
        Log.e(TAG, "onFinishInflate");
        super.onFinishInflate();
        mTop = findViewById(R.id.id_stickynavlayout_topview);
        mNav = findViewById(R.id.id_stickynavlayout_indicator);
        View view = findViewById(R.id.id_stickynavlayout_viewpager);
        if (!(view instanceof ViewPager)) {
            throw new RuntimeException(
                    "id_stickynavlayout_viewpager show used by ViewPager !");
        }
        mViewPager = (ViewPager) view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //不限制顶部的高度
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        getChildAt(0).measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        Log.e(TAG, "top:  "+ScreenUtils.checkDeviceHasNavigationBar(getContext()));
        if (ScreenUtils.checkDeviceHasNavigationBar(getContext())) {
            if (mViewPager.getMeasuredHeight()==0) {
                params.height = getMeasuredHeight()
                        - mNav.getMeasuredHeight()

                        + ScreenUtils.getNavigationBarHeight(getContext())
                        + ScreenUtils.getStatusBarHeight(getContext());
            }

            Log.e("viewpager_",ScreenUtils.checkDeviceHasNavigationBar(getContext())+"  "+mViewPager.getMeasuredHeight());
            setMeasuredDimension(getMeasuredWidth(),
                    mTop.getMeasuredHeight()
                    + mNav.getMeasuredHeight()
                    + mViewPager.getMeasuredHeight());

        } else {
            params.height = getMeasuredHeight()
                    - mNav.getMeasuredHeight();
            Log.e("viewpager_",ScreenUtils.checkDeviceHasNavigationBar(getContext())+"  "+mViewPager.getMeasuredHeight());
            setMeasuredDimension(getMeasuredWidth(),
                    mTop.getMeasuredHeight()
                            + mNav.getMeasuredHeight()
                            + mViewPager.getMeasuredHeight());
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTop.getMeasuredHeight();
        Log.e(TAG, "onSizeChanged");
    }


    public void fling(int velocityY) {
        Log.e(TAG, "fling " + velocityY + "    " + getScrollY());
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();
    }


    /**
     * 根据速度计算滚动动画持续时间
     *
     * @param velocityY
     * @return
     */
    private int computeDuration(float velocityY) {
        Log.e(TAG, "computeDuration");
        final int distance;
        if (velocityY > 0) {
            distance = Math.abs(mTop.getHeight() - getScrollY());
        } else {
            distance = Math.abs(mTop.getHeight() - (mTop.getHeight() - getScrollY()));
        }


        final int duration;
        velocityY = Math.abs(velocityY);
        if (velocityY > 0) {
            duration = 3 * Math.round(1000 * (distance / velocityY));
        } else {
            final float distanceRatio = (float) distance / getHeight();
            duration = (int) ((distanceRatio + 1) * 150);
        }

        return duration;

    }

    private void animateScroll(float velocityY, final int duration, boolean consumed) {
        Log.e(TAG, "animateScroll");
        final int currentOffset = getScrollY();
        final int topHeight = mTop.getHeight();
        if (mOffsetAnimator == null) {
            mOffsetAnimator = new ValueAnimator();
            Interpolator mInterpolator = new LinearInterpolator();
            mOffsetAnimator.setInterpolator(mInterpolator);
            mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getAnimatedValue() instanceof Integer) {
                        Log.e(TAG, "onAnimationUpdate "+animation.getAnimatedValue());
                        scrollTo(0, (Integer) animation.getAnimatedValue());
                    }
                }
            });
        } else {
            mOffsetAnimator.cancel();
        }
        mOffsetAnimator.setDuration(Math.min(duration, minDuration));

        if (!mOffsetAnimator.isRunning()) {
            if (velocityY >= 0) {
                mOffsetAnimator.setIntValues(currentOffset, topHeight);
                mOffsetAnimator.start();
            } else {
                //如果子View没有消耗down事件 那么就让自身滑倒0位置
                if (!consumed) {
//                    mOffsetAnimator.setIntValues(currentOffset, 0);
                    mOffsetAnimator.setIntValues(currentOffset);
                    mOffsetAnimator.start();
                }
            }
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        Log.e(TAG, "scrollTo" + "    x=" + x + "     y=" + y + "     topHeight" + mTopViewHeight);
        if (y < 0) {
            y = 0;
        }
        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }

    @Override
    public void computeScroll() {
        Log.e(TAG, "computeScroll");
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }


    //nested
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        Log.e(TAG, "onStartNestedScroll" + ViewCompat.SCROLL_AXIS_VERTICAL + nestedScrollAxes);
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        Log.e(TAG, "onNestedScrollAccepted");
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        Log.e(TAG, "onStopNestedScroll");
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.e(TAG, "onNestedScroll");
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        Log.e(TAG, "onNestedPreScroll    "+dy);
        boolean hiddenTop = dy > 0 && getScrollY() < mTopViewHeight;
        boolean showTop = dy < 0 && getScrollY() >= 0 && !target.canScrollVertically(-1);

        if (hiddenTop || showTop) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Log.e(TAG, "onNestedFling");
        //如果是recyclerView 根据判断第一个元素是哪个位置可以判断是否消耗
        //这里判断如果第一个元素的位置是大于TOP_CHILD_FLING_THRESHOLD的
        //认为已经被消耗，在animateScroll里不会对velocityY<0时做处理
        if (target instanceof RecyclerView && velocityY < 0) {
            final RecyclerView recyclerView = (RecyclerView) target;
            final View firstChild = recyclerView.getChildAt(0);
            final int childAdapterPosition = recyclerView.getChildAdapterPosition(firstChild);
            consumed = childAdapterPosition > TOP_CHILD_FLING_THRESHOLD;
        }
        if (!consumed) {
            animateScroll(velocityY, computeDuration(0), consumed);
        } else {
            animateScroll(velocityY, computeDuration(velocityY), consumed);
        }
        return true;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        //不做拦截 可以传递给子View
        Log.e(TAG, "onNestedPreFling");
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        Log.e(TAG, "getNestedScrollAxes");
        return 0;
    }
}
