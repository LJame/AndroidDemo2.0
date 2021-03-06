package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.baoyz.swipemenuexpandablelistview.SwipeMenuExpandableListView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.hardrubic.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;

public class ExpandableListViewSwipeActivity extends TitleActivity {

    private Context mContext;
    private List<String> groupList = new ArrayList<>();
    private List<List<String>> childList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_list_view_swipe);
        mContext = this;

        initData();
        initView();
    }

    private void initView() {
        SwipeMenuExpandableListView expandableListView = (SwipeMenuExpandableListView) findViewById(R.id.lv_expand);
        expandableListView.setGroupIndicator(null);
        expandableListView.setMenuCreator(new SwipeMenuCreator() {
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
        expandableListView.setOnExpandableMenuItemClickListener(new SwipeMenuExpandableListView.OnExpandableMenuItemClickListener() {
            @Override
            public boolean onExpandableMenuItemClick(int groupPosition, int childPosition, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ToastUtil.longShow(mContext, "删除" + childList.get(groupPosition).get(childPosition));
                        break;
                }
                return false;
            }
        });
        CustomExpandableListAdapter adapter = new CustomExpandableListAdapter();
        expandableListView.setAdapter(adapter);
    }

    private void initData() {
        groupList.add("A");
        List<String> tempList = new ArrayList<>();
        tempList.add("1");
        tempList.add("2");
        tempList.add("3");
        childList.add(tempList);

        groupList.add("B");
        tempList = new ArrayList<>();
        tempList.add("4");
        tempList.add("5");
        tempList.add("6");
        childList.add(tempList);

        groupList.add("C");
        tempList = new ArrayList<>();
        tempList.add("7");
        tempList.add("8");
        tempList.add("9");
        tempList.add("10");
        childList.add(tempList);
    }

    private class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return Long.parseLong(childList.get(groupPosition).get(childPosition));
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setTextColor(Color.BLUE);
            textView.setText(groupList.get(groupPosition));
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(childList.get(groupPosition).get(childPosition));
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

}
