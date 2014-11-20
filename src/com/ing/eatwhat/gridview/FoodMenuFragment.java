package com.ing.eatwhat.gridview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import com.ing.eatwhat.R;
import com.ing.eatwhat.activity.AddFoodActivity;
import com.ing.eatwhat.database.DBManager;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.gridview.ImageScanner.ScanCompleteCallBack;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
	private String deleted_foodName;		//被删除的食物名
	private DBManager dbManager;
	StickyGridAdapter adapter;
	
	private ProgressDialog mProgressDialog;
	private ImageScanner mScanner;
	private GridView mGridView;
	private List<GridItem> mGridList;
	private static int section = 1;
	private Map<String, Integer> sectionMap = new HashMap<String, Integer>();

	//构造函数
	public FoodMenuFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {				
		view = inflater.inflate(R.layout.test_gridview, container, false);
		return view;		
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		
		refresh();
	}

	//刷新
	public void refresh(){
		mGridList = new ArrayList<GridItem>();
		mGridView = (GridView)view.findViewById(R.id.grid);
		mScanner = new ImageScanner(this.getActivity());
		mScanner.scanImages(new ScanCompleteCallBack() {
			{
				mProgressDialog = ProgressDialog.show(FoodMenuFragment.this.getActivity(), null, "正在加载...(我是为你们这群使劲加图片的丧病写的~~~");
			}
			
			@Override
			public void scanComplete(Cursor cursor) {
				// 关闭进度条
				mProgressDialog.dismiss();
				dbManager = new DBManager(getActivity());
				HashMap<String, ArrayList<String>> map = dbManager.getAllFood();
				ArrayList<String> arr_name = map.get("name");
				ArrayList<String> arr_picPath = map.get("picPath");
				for (int i=0; i<arr_name.size(); i++) {
					// 获取图片的路径
					String name = arr_name.get(i);
					String path = arr_picPath.get(i);
					GridItem mGridItem = new GridItem(name, path);
					mGridList.add(mGridItem);
				}
			
				for(ListIterator<GridItem> it = mGridList.listIterator(); it.hasNext();){
					GridItem mGridItem = it.next();
					String path = mGridItem.getpath();
					if(!sectionMap.containsKey(path)){
						mGridItem.setSection(section);
						sectionMap.put(path, section);
						section ++;
					}else{
						mGridItem.setSection(sectionMap.get(path));
					}
				}
				
				if(!cursor.isClosed()) {
					cursor.close();
				}
				adapter = new StickyGridAdapter(FoodMenuFragment.this.getActivity(), mGridList, mGridView);
				mGridView.setAdapter(adapter);
				mGridView.setOnItemClickListener(FoodMenuFragment.this);
				mGridView.setOnItemLongClickListener(FoodMenuFragment.this);		
			}
		});
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
}
