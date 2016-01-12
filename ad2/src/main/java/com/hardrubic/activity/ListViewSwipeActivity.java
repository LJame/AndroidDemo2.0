package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.hardrubic.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;

public class ListViewSwipeActivity extends TitleActivity {

    private Context mContext;
    private List<String> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_swipe);
        mContext = this;

        initData();
        initView();
    }

    private void initData(){
        for (int i = 0; i < 20; i++) {
            mDataList.add(""+i);
        }
    }

    private void initView() {
        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.lv_list);
        listView.setVisibility(View.VISIBLE);
        listView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem swipeMenuItem = new SwipeMenuItem(mContext);
                swipeMenuItem.setWidth(200);
                swipeMenuItem.setTitle("删除");
                swipeMenuItem.setTitleSize(20);
                swipeMenuItem.setTitleColor(getResources().getColor(R.color.white));
                swipeMenuItem.setBackground(R.color.red);
                menu.addMenuItem(swipeMenuItem);
            }
        });
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ToastUtil.longShow(mContext, "删除");
                        break;
                }
                return false;
            }
        });
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mDataList);
        listView.setAdapter(adapter);
    }
}
