package com.nested.viewpager;

import android.animation.ValueAnimator;
import android.content.Context;
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
    private int minDuration = 100;

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
    private boolean isDy=false;

    private int TOP_CHILD_FLING_THRESHOLD = 3;

    public StickyNavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);

        mScroller = new OverScroller(context, new LinearInterpolator());
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();
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
//        如果动画或者 scroller 正在执行  不接受事件 避免接受多个事件 造成滑动混乱
//        if ((mOffsetAnimator != null && mOffsetAnimator.isRunning()) | (mScroller != null && !mScroller.isFinished())) {
//            return false;
//        }
//        如果动画或者 scroller 正在执行  则 取消当前执行的动画或者 scroller 继续接受新的事件
        if ((mOffsetAnimator != null && mOffsetAnimator.isRunning()) | (mScroller != null && !mScroller.isFinished())) {
            if (mOffsetAnimator != null && mOffsetAnimator.isRunning()) {
                mOffsetAnimator.cancel();
            }
            if (mScroller != null && !mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
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
                Log.e("event", "ACTION_DOWN");
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastY = y;
                Log.e("touch", event.getY()+"   down");
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.e("touch", event.getY()+"    move");
                Log.e("event", "ACTION_MOVE");
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
                Log.e("touch", event.getY()+"   cancel");
                Log.e("event", "ACTION_CANCEL");
                mDragging = false;
                recycleVelocityTracker();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e("touch", event.getY()+"    up");
                Log.e("event", "ACTION_UP");
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                Log.e("velocity", "Y velocity: " + mVelocityTracker.getYVelocity()+"   max:"+mMaximumVelocity+"   min:"+mMinimumVelocity);
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    // todo 尝试寻找一个比较合适的速度压缩
                    fling(-velocityY/3);
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
        Log.e(TAG, "top:  " + ScreenUtils.checkDeviceHasNavigationBar(getContext()));
        if (ScreenUtils.checkDeviceHasNavigationBar(getContext())) {
            if (mViewPager.getMeasuredHeight() == 0) {
                params.height = getMeasuredHeight()
                        - mNav.getMeasuredHeight()

                        + ScreenUtils.getNavigationBarHeight(getContext())
                        + ScreenUtils.getStatusBarHeight(getContext());
            }

            Log.e("viewpager_", ScreenUtils.checkDeviceHasNavigationBar(getContext()) + "  " + mViewPager.getMeasuredHeight());
            setMeasuredDimension(getMeasuredWidth(),
                    mTop.getMeasuredHeight()
                            + mNav.getMeasuredHeight()
                            + mViewPager.getMeasuredHeight());

        } else {
            params.height = getMeasuredHeight()
                    - mNav.getMeasuredHeight();
            Log.e("viewpager_", ScreenUtils.checkDeviceHasNavigationBar(getContext()) + "  " + mViewPager.getMeasuredHeight());
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
        Log.e(TAG, "fling " + velocityY + "    " + getScrollY()+"      canScrollVertically "+canScrollVertically(-1));
        // todo getScrollY()到顶部啦  开始刷新
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
        Log.e(TAG, " 惯性嵌套滑动 animateScroll   " + "duration：" + duration + "    consumed：" + consumed+"    velocityY"+velocityY);
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
                        Log.e(TAG, "duration " + mOffsetAnimator.getDuration());
                        scrollTo(0, (Integer) animation.getAnimatedValue());
                    }
                }
            });
        } else {
            mOffsetAnimator.cancel();
        }
//        mOffsetAnimator.setDuration(Math.min(duration, minDuration));
        mOffsetAnimator.setDuration(100);
        if (!mOffsetAnimator.isRunning()) {
            if (velocityY >= 0) {
//                mOffsetAnimator.setIntValues(currentOffset, topHeight);
                Log.e("lzf_currentOffset", currentOffset +"      "+velocityY);
                // 部分情况 回弹
                mOffsetAnimator.setIntValues(currentOffset, topHeight);
                mOffsetAnimator.start();
            } else {
                //如果子View没有消耗down事件 那么就让自身滑倒0位置
                if (!consumed) {
                    //todo 尝试一个合适的速度压缩
                    int targetY= (int) velocityY/10;
                    Log.e("lzf_currentOffset", currentOffset + "  targetY "+targetY+"      "+(targetY+currentOffset));
                    mOffsetAnimator.setIntValues(currentOffset, targetY+currentOffset);
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
    // 开启 停止嵌套滚动时被调用 start
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        Log.e(TAG, "嵌套滚动 开始 onStartNestedScroll");
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        Log.e(TAG, "嵌套滚动 接受 onNestedScrollAccepted");
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        Log.e(TAG, "嵌套滚动 停止 onStopNestedScroll");
    }
    // 开启 停止嵌套滚动时被调用 start


    // 触摸嵌套滑动 时被调用 start
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.e("press", "触摸嵌套滑动 子view 先滚动  onNestedScroll     已消费的Y:" + dyConsumed + "   未消费的Y:" + dyUnconsumed);
//        if (isDy&&dyConsumed==0){
////            如果父容器需要消费的 dy>0 &&子view中 onNestedScroll 已经消费的dy=0 说明 到达啦底部 开始加载
//            scrollBy(0,dyUnconsumed/3);
//        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
//       consumed  The horizontal and vertical scroll distance consumed by this parent
        // todo 如果父容器需要消费的 dy>0 &&子view中 onNestedScroll 已经消费的dy=0 说明 到达啦底部 开始加载
        Log.e("press", "触摸嵌套滑动  父容器 先滚动  onNestedPreScroll    " + dy);
        boolean hiddenTop = dy > 0 && getScrollY() < mTopViewHeight;
        boolean showTop = dy < 0 && getScrollY() >= 0 && !target.canScrollVertically(-1);
        isDy=dy>0;
        if (hiddenTop || showTop) {
            Log.e(TAG, "onNestedPreScroll     hiddenTop  " + hiddenTop + "     showTop " + showTop);
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }
    // 触摸嵌套滑动 时被调用 end

    // 惯性嵌套滑动 时被调用 start
    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        //不做拦截 可以传递给子View
        int[] location = new int[2];
        target.getLocationInWindow(location);
        int targetY = location[1];
        Log.e("onNestedPreFling", "惯性嵌套滑动  父容器 惯性 onNestedPreFling   " + targetY);
        return false;
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Log.e("onNestedFling", "惯性嵌套滑动  子view 惯性 onNestedFling");
        //如果是recyclerView 根据判断第一个元素是哪个位置可以判断是否消耗
        //这里判断如果第一个元素的位置是大于TOP_CHILD_FLING_THRESHOLD的
        //认为已经被消耗，在animateScroll里不会对velocityY<0时做处理
        if (target instanceof RecyclerView && velocityY < 0) {
            RecyclerView recyclerView = (RecyclerView) target;
            View firstChild = recyclerView.getChildAt(0);
            int childAdapterPosition = recyclerView.getChildAdapterPosition(firstChild);
            Log.e("lzf_recycler",childAdapterPosition+"");
            consumed = childAdapterPosition > TOP_CHILD_FLING_THRESHOLD;
        }
        Log.e(TAG, "consumed    " + consumed + "   velocityY:   " + velocityY);
        if (!consumed) {
//            // todo 尝试一个比较合适的速度压缩
            animateScroll(velocityY, computeDuration(0), consumed);
        } else {
            animateScroll(velocityY, computeDuration(velocityY), consumed);
        }
        return false;
    }
    // 惯性嵌套滑动 时被调用 end

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }
}
