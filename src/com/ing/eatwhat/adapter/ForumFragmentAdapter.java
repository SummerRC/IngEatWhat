package com.ing.eatwhat.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.ing.eatwhat.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ForumFragmentAdapter extends BaseAdapter{
	private Context context;
	public HashMap<String, ArrayList<String>> map;
	public ArrayList<String> date;
	public ArrayList<String> userName;
	public ArrayList<String> content;
	private int count;
	
	//构造函数
	public ForumFragmentAdapter(Context context, HashMap<String, ArrayList<String>> map) {
		this.context = context;	
		this.map = map;
		date = map.get("date");
		userName = map.get("userName");
		content = map.get("content");	
		count = date.size();
	}

	//根据返回的count绘制条目数
	@Override
	public int getCount() {
		return count;
	}
	
	//一条一条地绘制
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(count == 0) {
			return null;
		}
		ViewHolder holder = null;
		if(convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.fragment_forum_item, null);
			holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			holder.tv_userName = (TextView) convertView.findViewById(R.id.tv_userName); 		
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content); 		
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tv_date.setText(date.get(position));				
		holder.tv_userName.setText(userName.get(position));		
		holder.tv_content.setText(content.get(position));		
			
		return convertView;
	}
	
	private static class ViewHolder {
		private TextView tv_date;
		private TextView tv_userName;
		private TextView tv_content;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
