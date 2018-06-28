package com.nested.viewpager;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by lizhifeng on 2018/6/28.
 */

public class LoadRecyclerView extends RecyclerView {
    private LoadMoreListener mLoadingListener;
    private ArrayList<View> mFootViews = new ArrayList<>();

    public LoadRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public LoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_load, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        addFootView(view);
        mFootViews.get(0).setVisibility(GONE);
    }

    public void addFootView(final View view) {
        mFootViews.clear();
        mFootViews.add(view);
    }

    public LoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.mLoadingListener = loadMoreListener;
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (!canScrollVertically(1)) {
            View footView = mFootViews.get(0);
            footView.setVisibility(View.VISIBLE);
            mLoadingListener.onLoadMore();
        }
    }

    public void onLoadMoreComplete(){
        View footView = mFootViews.get(0);
        footView.setVisibility(View.GONE);
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }
}

