package com.nested.viewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.recyclerview.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

public class TextActivity extends AppCompatActivity {
    private CommonAdapter<String> adapter;
    private List<String> mDatas = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        LoadRecyclerView mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        for (int i = 0; i < 50; i++) {
            mDatas.add(" title  " + " -> " + i);
        }
        adapter = new CommonAdapter<String>(this, R.layout.item, mDatas) {
            @Override
            public void convert(ViewHolder holder, String o) {
                holder.setText(R.id.id_info, o);
            }
        };
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLoadMoreListener(new LoadRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < 10; i++) {
                                    mDatas.add("load  +" + i);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();

            }
        });
    }
}
