package com.ing.eatwhat.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.ing.eatwhat.R;
import com.ing.eatwhat.adapter.MapListAdapter;

public class BusActivity extends ListActivity {

	public MapListAdapter adapter =null;
	public int routeSize=0;
	public int routeSizex=0;
	public String currentString []= new String[10];
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.busactivity);
	
	routeSize = getIntent().getIntExtra("size", 0);
	routeSizex=routeSize;
	for (int i = 0; i < routeSizex; i++) {
		currentString[i] = getIntent().getStringExtra

("route"+i);
	}
	for (int j = 0; j < routeSizex; j++) {
		for (int m = j+1; m < routeSizex; m++) {
			if(currentString[j].equals

(currentString[m])){
				routeSize--;
			}
		}
	}
	//通过构造函数来将公交换乘方案的大小传给MapListAdapter.class。此乃传递信息的方法之一
	adapter = new MapListAdapter(this, routeSize);
	setListAdapter(adapter);
	System.out.println("88888888888888"+routeSize);
	//根据换乘方案的大小初始化data数组
	for (int y = 0; y < routeSize; y++) {
		adapter.setData(y, currentString[y]);
	}
}
@Override
protected void onListItemClick(ListView l, View v, int 

position, long id) {
	
	
	Intent i = new Intent

(BusActivity.this,GetLocationActivity.class);
	//将所选的公交线路返回MainActivity
	i.putExtra("choosedRoute", position);
    startActivity(i);
	
	super.onListItemClick(l, v, position, id);
}
}