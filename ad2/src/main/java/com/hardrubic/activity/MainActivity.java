package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
        }
    }
}
