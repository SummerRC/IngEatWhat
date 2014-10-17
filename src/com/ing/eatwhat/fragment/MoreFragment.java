package com.ing.eatwhat.fragment;

import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.User;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MoreFragment extends Fragment implements View.OnClickListener{
	Button bt_frag_more_about;			//关于我们
	Button bt_frag_more_update;			//升级
	Button bt_frag_more_username;		//显示用户名的按钮，可监听
	Button bt_frag_more_exit;			//退出当前账号

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_more, container, false);
	}
	
	//当Fragment对象可见时调用
	@Override
	public void onStart() {
		super.onStart();
		
		bt_frag_more_about = (Button) getView().findViewById(R.id.bt_frag_more_about);
		bt_frag_more_update = (Button) getView().findViewById(R.id.bt_frag_more_update);
		bt_frag_more_exit =(Button) getView().findViewById(R.id.bt_frag_more_exit);
		bt_frag_more_username = (Button) getView().findViewById(R.id.bt_frag_more_username);
	
		bt_frag_more_username.setText(User.userName);
	}
	
	//当Fragment可与用户交互时调用
	@Override
	public void onResume() {
		super.onResume();

		//注册监听
		bt_frag_more_about.setOnClickListener(this);
		bt_frag_more_update.setOnClickListener(this);
		bt_frag_more_exit.setOnClickListener(this);
		bt_frag_more_username.setOnClickListener(this);				
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.bt_frag_more_about:
			break;
		case R.id.bt_frag_more_update:
			update();
			break;
		case R.id.bt_frag_more_exit:
			exit();
			break;
		case R.id.bt_frag_more_username:
			//点击用户名的按钮进入自己的个人资料
			break;
		}
		
	}
	
	//调用该方法更新应用程序
	private void  update() {
		
	}
	
	//退出当前账号时调用
	private void exit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());				
		builder.setTitle("提示")
			   .setMessage("退出将不再为您保存登陆状态")
			   .setCancelable(false)         				   
			   .setNegativeButton("确定", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					    SharedPreferences sp = getActivity().getSharedPreferences("ingEatwhat", Context.MODE_PRIVATE);
						Editor editor=sp.edit();
						editor.putBoolean("haveLogined", false);
						editor.commit();
						getActivity().finish();								
				   }
			   })					   
			   .setPositiveButton("取消", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   dialog.dismiss();
				   }
			   })
			   .show();
	}
	
}
	
