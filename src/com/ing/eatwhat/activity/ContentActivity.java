package com.ing.eatwhat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.ing.eatwhat.R;

public class ContentActivity extends Activity {
	TextView tv_userName;			//�û���
	TextView tv_date;				//ʱ��
	TextView tv_content;			//����
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
		
		Log.i("����", userName);
		Log.i("����", date);
		Log.i("����", content);
		tv_userName = (TextView) findViewById(R.id.tv_userName);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_content = (TextView) findViewById(R.id.tv_content);
		
		Log.i("����", "1");
		tv_userName.setText(userName);
		Log.i("����", "2");
		tv_date.setText(date);
		Log.i("����", "3");
		tv_content.setText("3");  
		Log.i("����", "4");
		
		/*ArrayList<HashMap<String,String>> list =new ArrayList<HashMap<String,String>>();
		HashMap<String,String> map1 = new HashMap<String,String>();
		HashMap<String,String> map2 = new HashMap<String,String>();
		HashMap<String,String> map3 = new HashMap<String,String>();
		map1.put("user_name", "��ð���");
		map2.put("user_name", "�ø�ƨ������");
		map3.put("user_name", "������������������");
		list.add(map1);
		list.add(map2);
		list.add(map3);
		SimpleAdapter listAdapter = new SimpleAdapter(this,list,R.layout.ping,new String[]{"user_name"},new int[]{R.id.pingtext});
		pinglist = (ListView) findViewById(R.id.pinglist);
		pinglist.setAdapter(listAdapter);*/
	}
}
