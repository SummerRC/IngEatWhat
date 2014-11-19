package com.ing.eatwhat.activity;

import com.ing.eatwhat.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class AboutUsActivity extends SlidingBackActivity{
	private Button bt_back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//全屏显示  无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		//添加滑动返回监听
		LinearLayout ll = (LinearLayout) findViewById(R.id.about_aiya_layout);
		ll.setOnTouchListener(this);
		
		bt_back = (Button) this.findViewById(R.id.bt_back);
		bt_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AboutUsActivity.this.finish();
			}
		});
	}

}
