package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.hardrubic.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;

public class SwipeMenuActivity extends TitleActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_menu);
        mContext = this;

        initView();
    }

    private void initView() {
        List<String> dataList = new ArrayList<>();
        dataList.add("1");
        dataList.add("2");
        dataList.add("3");
        dataList.add("4");
        dataList.add("5");
        dataList.add("6");
        dataList.add("7");
        dataList.add("8");
        dataList.add("9");
        dataList.add("10");
        dataList.add("11");
        dataList.add("12");
        dataList.add("13");
        dataList.add("14");
        dataList.add("15");

        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.lv_list);
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
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
    }
}
