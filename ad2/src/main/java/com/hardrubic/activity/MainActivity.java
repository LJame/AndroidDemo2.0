package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends TitleActivity implements View.OnClickListener {

    @Bind(R.id.tv_three_scroll)
    TextView tv_three_scroll;
    @Bind(R.id.tv_horizontal_scroll)
    TextView tv_horizontal_scroll;
    @Bind(R.id.tv_aidl)
    TextView tv_aidl;
    @Bind(R.id.tv_recycle_view)
    TextView tv_recycle_view;
    @Bind(R.id.tv_custom_view)
    TextView tv_custom_view;
    @Bind(R.id.tv_custom_view_group)
    TextView tv_custom_view_group;
    @Bind(R.id.tv_listview_swipe)
    TextView tv_listview_swipe;
    @Bind(R.id.tv_ExpandableListView_swipe)
    TextView tv_ExpandableListView_swipe;
    @Bind(R.id.tv_js_bridge)
    TextView tv_js_bridge;
    @Bind(R.id.tv_ndk)
    TextView tv_ndk;
    @Bind(R.id.tv_hide_dialog)
    TextView tv_hide_dialog;
    @Bind(R.id.tv_list_view_edit)
    TextView tv_list_view_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideTitleBack();
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        tv_three_scroll.setOnClickListener(this);
        tv_horizontal_scroll.setOnClickListener(this);
        tv_aidl.setOnClickListener(this);
        tv_recycle_view.setOnClickListener(this);
        tv_custom_view.setOnClickListener(this);
        tv_custom_view_group.setOnClickListener(this);
        tv_listview_swipe.setOnClickListener(this);
        tv_ExpandableListView_swipe.setOnClickListener(this);
        tv_js_bridge.setOnClickListener(this);
        tv_ndk.setOnClickListener(this);
        tv_hide_dialog.setOnClickListener(this);
        tv_list_view_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_three_scroll:
                intent = new Intent(this, ScrollActivity.class);
                break;
            case R.id.tv_horizontal_scroll:
                intent = new Intent(this, HorizontalScrollActivity.class);
                break;
            case R.id.tv_aidl:
                intent = new Intent(this, AidlActivity.class);
                break;
            case R.id.tv_recycle_view:
                intent = new Intent(this, RecyclerViewActivity.class);
                break;
            case R.id.tv_custom_view:
                intent = new Intent(this, CustomViewActivity.class);
                break;
            case R.id.tv_custom_view_group:
                intent = new Intent(this, CustomViewGroupActivity.class);
                break;
            case R.id.tv_listview_swipe:
                intent = new Intent(this, ListViewSwipeActivity.class);
                break;
            case R.id.tv_ExpandableListView_swipe:
                intent = new Intent(this, ExpandableListViewSwipeActivity.class);
                break;
            case R.id.tv_js_bridge:
                intent = new Intent(this, JsTestActivity.class);
                break;
            case R.id.tv_ndk:
                intent = new Intent(this, NdkActivity.class);
                break;
            case R.id.tv_hide_dialog:
                intent = new Intent(this, HideDialogActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
    }
}
