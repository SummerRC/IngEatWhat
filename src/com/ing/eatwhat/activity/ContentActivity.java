package com.ing.eatwhat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.ing.eatwhat.R;

public class ContentActivity extends Activity {
	TextView tv_userName;			//用户名
	TextView tv_date;				//时间
	TextView tv_content;			//内容
	ListView pinglist;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);
		
		init();
	}
	
	private void init() {
		Intent intent = getIntent();
		String userName = intent.getStringExtra("userName");
		String date = intent.getStringExtra("date");
		String content = intent.getStringExtra("content");
		
		tv_userName = (TextView) findViewById(R.id.tv_userName);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_content = (TextView) findViewById(R.id.tv_content);
		
		tv_userName.setText(userName);
		tv_date.setText(date);		
		tv_content.setText(content);  
		
	}
}
