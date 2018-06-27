package com.nested.viewpager;

/**
 * Created by lizhifeng on 2018/6/22.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.recyclerview.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabFragment extends Fragment {
    public static final String TITLE = "title";
    private String mTitle = "Defaut Value";
    private RecyclerView mRecyclerView;
    // private TextView mTextView;
    private List<String> mDatas = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TITLE);
        }
        for (int i = 0; i < 50; i++) {
            mDatas.add(mTitle + " -> " + i);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        mRecyclerView = (RecyclerView) view
                .findViewById(R.id.id_stickynavlayout_innerscrollview);
//        TwinklingRefreshLayout refreshLayout = view.findViewById(R.id.refresh);
//        refreshLayout.setEnableRefresh(false);
//        refreshLayout.setEnableLoadmore(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // mTextView = (TextView) view.findViewById(R.id.id_info);
        // mTextView.setText(mTitle);

        mRecyclerView.setAdapter(new CommonAdapter<String>(getActivity(), R.layout.item, mDatas) {
            @Override
            public void convert(ViewHolder holder, String o) {
                holder.setText(R.id.id_info, o);
            }
        });
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("touch","recycler    "+event.getY());
                return false;
            }
        });
        return view;

    }

    public static TabFragment newInstance(String title) {
        TabFragment tabFragment = new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

}
