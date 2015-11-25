package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

public class TitleActivity extends AppCompatActivity implements OnClickListener {

    private Toolbar toolbar;
	private FrameLayout mContainerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_title);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mContainerLayout = (FrameLayout) findViewById(R.id.content_container);
	}

	/**
	 * 子类重写时,应该在switch的default中调用super.onOptionsItemSelected();
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	/**
	 * 标题文字
	 */
	public void setTitleText(String title) {
		toolbar.setTitle(title);
	}

	/**
	 * 标题文字
	 */
	public void setTitleText(int resId) {
		toolbar.setTitle(resId);
	}

	/**
	 * 隐藏左侧返回键
	 */
	public void hideTitleBack(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	public void hideTitleBar() {
		getSupportActionBar().hide();
	}

	@Override
	public void setContentView(View v) {
		super.setContentView(v);
		mContainerLayout.addView(v);
	}

	@Override
	public void setContentView(int resId) {
		getLayoutInflater().inflate(resId, mContainerLayout);
	}

}
