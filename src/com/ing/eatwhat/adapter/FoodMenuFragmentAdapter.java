package com.ing.eatwhat.adapter;

import android.app.Activity;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.ing.eatwhat.R;
import com.ing.eatwhat.database.DBManager;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FoodMenuFragmentAdapter extends BaseAdapter { 
	private Context context;
	private DBManager dbManager;
	private ArrayList<String> arr_name;
	private ArrayList<String> arr_picPath;
	private int count;
	
	public FoodMenuFragmentAdapter(Context context) {
		this.context = context;
		//从数据库中取出数据集name和picPath存到相应ArraList
		dbManager = new DBManager(this.context);
		HashMap<String, ArrayList<String>> map = dbManager.getAllFood();
		arr_name = map.get("name");
		arr_picPath = map.get("picPath");
		count = arr_name.size();
	}
	
	//绘制之前先得到条目总数
	@Override
	public int getCount() {
		if(count == 0){
			return 1;
		} else{
			return count + 1;
		}
	}
	
	//一条一条的绘制
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.fragment_food_menu_item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv_frag_food_menu_item_pic);
			holder.tv = (TextView) convertView.findViewById(R.id.tv_frag_food_menu_item_name); 		
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		if(position == getCount()-1){
			holder.iv.setImageResource(R.drawable.add);
			holder.tv.setText("添加");
		} else {			
			holder.tv.setText(arr_name.get(position));		//数据库获得
			holder.iv.setImageURI(Uri.fromFile(new File(arr_picPath.get(position))));
		}
		
		return convertView;
	}
	
	public static class ViewHolder {
		private ImageView iv;
		private TextView tv;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}
	
}
