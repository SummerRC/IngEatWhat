package com.ing.eatwhat.fragment;

import com.ing.eatwhat.R;
import com.ing.eatwhat.activity.AddFoodActivity;
import com.ing.eatwhat.adapter.FoodMenuFragmentAdapter;
import com.ing.eatwhat.database.DBManager;
import com.ing.eatwhat.entity.AllUse;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class FoodMenuFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener{

	private View view;
	private static GridView gridView;
	private static FoodMenuFragmentAdapter adapter;
	private String deleted_foodName;		//被删除的食物名
	private DBManager dbManager;
	
	//构造函数
	public FoodMenuFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {				
		view = inflater.inflate(R.layout.fragment_food_menu, container, false);
		return view;		
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		refresh();
	}
	
	//点击的监听器
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TextView tv_frag_food_menu_item_name = (TextView) view.findViewById(R.id.tv_frag_food_menu_item_name);
		String foodName = (String)tv_frag_food_menu_item_name.getText();
		Intent intent = new Intent();
		intent.setClass(getActivity(), AddFoodActivity.class);
		if(position == (adapter.getCount()-1)){			
			intent.putExtra("op", "save");
		} else {
			intent.putExtra("op", "edit"); 
		}
		intent.putExtra("foodname", foodName);	
		startActivity(intent);	
	}

	//长按的监听器
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
		if(position == AllUse.getFoodNum(getActivity())) {	//屏蔽加号图片的长按点击事件
			return true;	//点击事件不再向下传递
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());				
		builder.setTitle("提示")
			   .setMessage("是否删除")
			   .setCancelable(false)         				   
			   .setNegativeButton("确定", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   	TextView tv_frag_home_name = (TextView) view.findViewById(R.id.tv_frag_food_menu_item_name);
						deleted_foodName = (String) tv_frag_home_name.getText();
						dbManager = new DBManager(getActivity());
						dbManager.delete(deleted_foodName);
						AllUse.editFoodNum(getActivity(), -1);
						refresh();		//更新UI组件
				   }
			   })					   
			   .setPositiveButton("取消", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   dialog.dismiss();
				   }
			   })
			   .show();			
		return true;
	}
	
	//刷新
	public void refresh(){
		//GridView与数据进行适配
		gridView = (GridView)view.findViewById(R.id.grid);
		//重新适配
		adapter = new FoodMenuFragmentAdapter(this.getActivity());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemLongClickListener(this);
	}
}
