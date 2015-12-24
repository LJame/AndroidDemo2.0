package com.hardrubic.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hardrubic.adapter.MyDividerItemDecoration;
import com.hardrubic.adapter.MyRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import ad2.hardrubic.com.androiddemo20.R;

/**
 * 参考http://blog.csdn.net/lmj623565791/article/details/45059587
 */
public class RecyclerViewActivity extends TitleActivity {
    private List<Integer> mDataList = new ArrayList<>();
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        for (int i = 0; i < 1000; i++) {
            mDataList.add(i);
        }

        initView();
    }

    private void initView() {
        findViewById(R.id.tv_try_list).setOnClickListener(this);
        findViewById(R.id.tv_try_grid).setOnClickListener(this);
        findViewById(R.id.tv_try_waterfall).setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        showList();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_try_list:
                showList();
                break;
            case R.id.tv_try_grid:
                showGrid();
                break;
            case R.id.tv_try_waterfall:
                showWaterfall();
                break;
        }
    }

    private void showList() {
        //设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置adapter
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(this, mDataList);
        mRecyclerView.setAdapter(adapter);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, MyDividerItemDecoration.HORIZONTAL_LIST));
    }

    private void showGrid() {

    }

    private void showWaterfall() {

    }
}

