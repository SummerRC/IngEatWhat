package com.ing.eatwhat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.ing.eatwhat.R;

public class ContentActivity extends Activity {
	TextView tv_userName;			//ÓÃ»§Ãû
	TextView tv_date;				//Ê±¼ä
	TextView tv_content;			//ÄÚÈÝ
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
		
		Log.i("¹þ¹þ", userName);
		Log.i("¹þ¹þ", date);
		Log.i("¹þ¹þ", content);
		tv_userName = (TextView) findViewById(R.id.tv_userName);
		tv_date = (TextView) findViewById(R.id.tv_date);
		tv_content = (TextView) findViewById(R.id.tv_content);
		
		Log.i("¹þ¹þ", "1");
		tv_userName.setText(userName);
		Log.i("¹þ¹þ", "2");
		tv_date.setText(date);
		Log.i("¹þ¹þ", "3");
		tv_content.setText("3");  
		Log.i("¹þ¹þ", "4");
		
		/*ArrayList<HashMap<String,String>> list =new ArrayList<HashMap<String,String>>();
		HashMap<String,String> map1 = new HashMap<String,String>();
		HashMap<String,String> map2 = new HashMap<String,String>();
		HashMap<String,String> map3 = new HashMap<String,String>();
		map1.put("user_name", "ÄãºÃ°¡£¡");
		map2.put("user_name", "ºÃ¸öÆ¨¡£¡£¡£");
		map3.put("user_name", "¹ö¡£¡£¡£¡£¡£¡£¡£¡£");
		list.add(map1);
		list.add(map2);
		list.add(map3);
		SimpleAdapter listAdapter = new SimpleAdapter(this,list,R.layout.ping,new String[]{"user_name"},new int[]{R.id.pingtext});
		pinglist = (ListView) findViewById(R.id.pinglist);
		pinglist.setAdapter(listAdapter);*/
	}
}
