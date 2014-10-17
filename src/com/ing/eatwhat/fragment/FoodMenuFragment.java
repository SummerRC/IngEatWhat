package com.ing.eatwhat.fragment;

import com.ing.eatwhat.R;
import com.ing.eatwhat.activity.AddFoodActivity;
import com.ing.eatwhat.adapter.FoodMenuFragmentAdapter;
import com.ing.eatwhat.database.DBManager;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.User;
import com.ing.eatwhat.thread.NetThread;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private Handler mHandler;
	private String deleted_foodName;		//��ɾ����ʳ����
	private DBManager dbManager;
	
	//���캯��
	public FoodMenuFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {				
		view = inflater.inflate(R.layout.fragment_food_menu, container, false);
		return view;		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 0) {
					int result = Integer.valueOf(msg.obj.toString());
					switch(result) {
					case 2:		//ɾ������ʳ����ʧ��
						AllUse.info(getActivity().getApplicationContext(), "���棺ɾ������ʧ�ܣ������ڸò�����");
						break;
					case 3:		//ɾ���ɹ���ɾ���������ݿ�ļ�¼
						dbManager = new DBManager(getActivity());
						dbManager.delete(deleted_foodName);
						AllUse.editFoodNum(getActivity(), -1);
						refresh();		//����UI���
						break;
					default:							
					}
				}
			}
		};
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		refresh();
	}
	
	//����ļ�����
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

	//�����ļ�����
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());				
		builder.setTitle("��ʾ")
			   .setMessage("�Ƿ�ɾ��")
			   .setCancelable(false)         				   
			   .setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   	TextView tv_frag_home_name = (TextView) view.findViewById(R.id.tv_frag_food_menu_item_name);
						deleted_foodName = (String) tv_frag_home_name.getText();
						
						//�����߳�ɾ�����ϵĲ˵�
						NetThread mThread = new NetThread(mHandler, User.userName, deleted_foodName, null, null, 5);
						mThread.start();
				   }
			   })					   
			   .setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   dialog.dismiss();
				   }
			   })
			   .show();			
		return false;
	}
	
	//ˢ��
	public void refresh(){
		//GridView�����ݽ�������
		gridView = (GridView)view.findViewById(R.id.grid);
		//��������
		adapter = new FoodMenuFragmentAdapter(this.getActivity());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnItemLongClickListener(this);
	}

}
