package com.ing.eatwhat.adapter;

import com.ing.eatwhat.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MapListAdapter extends BaseAdapter {

	public int size = 0;
	public MapListData [] data =null;
	public Context context=null;
	public int position=0;
	
	public MapListAdapter(Context context,int size){
		this.context = context;
		data = new MapListData[size];
		System.out.println("55555555555555555555");
	}
	
	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public MapListData getItem(int position) {
		this.position = position;
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		this.position = position;
		return position;
	}
	
	public Context getContext(){
		return context;
	}
	public void setData(int currentPosition,String busRoute){
			data[currentPosition] = new MapListData(busRoute);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout ll =null;
		if (convertView !=null) {
			ll = (LinearLayout) convertView;
		}else{
			ll = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.maplistcell, null);
		}

		TextView busRoute = (TextView) ll.findViewById(R.id.busRoute);
		MapListData currentData = getItem(position);
		busRoute.setText(currentData.getBusRoute());
		
		return ll;
	}
}
