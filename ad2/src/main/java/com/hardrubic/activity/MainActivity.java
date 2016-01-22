package com.hardrubic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ad2.hardrubic.com.androiddemo20.R;

public class MainActivity extends TitleActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideTitleBack();

        initView();
    }

    private void initView() {
        findViewById(R.id.tv_three_scroll).setOnClickListener(this);
        findViewById(R.id.tv_horizontal_scroll).setOnClickListener(this);
        findViewById(R.id.tv_aidl).setOnClickListener(this);
        findViewById(R.id.tv_recycle_view).setOnClickListener(this);
        findViewById(R.id.tv_custom_view).setOnClickListener(this);
        findViewById(R.id.tv_custom_view_group).setOnClickListener(this);
        findViewById(R.id.tv_listview_swipe).setOnClickListener(this);
        findViewById(R.id.tv_ExpandableListView_swipe).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_three_scroll:
                Intent intent1 = new Intent(this, ScrollActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_horizontal_scroll:
                Intent intent2 = new Intent(this, HorizontalScrollActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_aidl:
                Intent intent3 = new Intent(this, AidlActivity.class);
                startActivity(intent3);
                break;
            case R.id.tv_recycle_view:
                Intent intent4 = new Intent(this, RecyclerViewActivity.class);
                startActivity(intent4);
                break;
            case R.id.tv_custom_view:
                Intent intent6 = new Intent(this, CustomViewActivity.class);
                startActivity(intent6);
                break;
            case R.id.tv_custom_view_group:
                Intent intent7 = new Intent(this, CustomViewGroupActivity.class);
                startActivity(intent7);
                break;
            case R.id.tv_listview_swipe:
                Intent intent8 = new Intent(this, ListViewSwipeActivity.class);
                startActivity(intent8);
                break;
            case R.id.tv_ExpandableListView_swipe:
                Intent intent9 = new Intent(this, ExpandableListViewSwipeActivity.class);
                startActivity(intent9);
                break;
        }
    }
}
