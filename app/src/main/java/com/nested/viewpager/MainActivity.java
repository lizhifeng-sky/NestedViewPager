package com.nested.viewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.recyclerview.CommonAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {
    private String[] mTitles = new String[]{"简介", "评价", "相关"};
    private SimpleViewPagerIndicator mIndicator;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private TabFragment[] mFragments = new TabFragment[mTitles.length];
    private RecyclerView recyclerView;
    private TwinklingRefreshLayout refreshLayout;
    private StickyNavLayout stickyNavLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                mIndicator.scroll(position, positionOffset);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initDatas() {
        mIndicator.setTitles(mTitles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        List<String> mDatas = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            mDatas.add("横向测试 -> " + i);
        }
        recyclerView.setAdapter(new CommonAdapter<String>(this, R.layout.item_recycler, mDatas) {
            @Override
            public void convert(ViewHolder holder, String o) {
                holder.setText(R.id.id_info, o);
            }
        });
        for (int i = 0; i < mTitles.length; i++) {
            mFragments[i] = (TabFragment) TabFragment.newInstance(mTitles[i]);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);

    }

    private void initViews() {
        mIndicator = findViewById(R.id.id_stickynavlayout_indicator);
        mViewPager = findViewById(R.id.id_stickynavlayout_viewpager);
        recyclerView = findViewById(R.id.recycler);
        stickyNavLayout = findViewById(R.id.stick);
    }
}
