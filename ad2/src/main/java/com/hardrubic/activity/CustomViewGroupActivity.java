package com.hardrubic.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hardrubic.myview.CustomHorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

import ad2.hardrubic.com.androiddemo20.R;

public class CustomViewGroupActivity extends TitleActivity {

    private CustomHorizontalScrollView tv_custom_view_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_view_group);

        tv_custom_view_group = (CustomHorizontalScrollView) findViewById(R.id.tv_custom_view_group);

        List<String> data = new ArrayList<>();
        data.add("1");
        data.add("2");
        data.add("3");
        ListView listView = new ListView(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(arrayAdapter);
        tv_custom_view_group.addView(listView);

        data = new ArrayList<>();
        data.add("a");
        data.add("b");
        data.add("c");
        listView = new ListView(this);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(arrayAdapter);
        tv_custom_view_group.addView(listView);
    }
}
