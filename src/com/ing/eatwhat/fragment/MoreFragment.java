package com.ing.eatwhat.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ing.eatwhat.R;
import com.ing.eatwhat.activity.AboutUsActivity;
import com.ing.eatwhat.activity.LoginActivity;
import com.ing.eatwhat.activity.SettingActivity;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.ExceptionApplication;
import com.ing.eatwhat.entity.UpdateManager;
import com.ing.eatwhat.entity.User;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class MoreFragment extends Fragment implements View.OnClickListener{
	Button bt_frag_more_username;	    	//显示用户名的按钮，可监听	
	Button bt_frag_more_setting;			 	//灵敏度
	Button bt_frag_more_personal;				//个人信息
	Button bt_frag_more_update;			   //更新升级
	Button bt_frag_more_feedback;		   //小贴士
	Button bt_frag_more_about;			      //关于我们	
	Button bt_frag_more_exit;		             //退出当前账号
	private ExceptionApplication myApplication; 
	private UpdateManager mUpdateManager;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_more, container, false);
	}
	
	//当Fragment对象可见时调用
	@Override
	public void onStart() {
		super.onStart();
		
		bt_frag_more_username = (Button) getView().findViewById(R.id.bt_frag_more_username);				//用户名
		bt_frag_more_setting = (Button) getView().findViewById(R.id.bt_frag_more_setting);							//灵敏度
		bt_frag_more_personal = (Button) getView().findViewById(R.id.bt_frag_more_personal);				   //个人信息
		bt_frag_more_update = (Button) getView().findViewById(R.id.bt_frag_more_update);					       //更新
		bt_frag_more_feedback = (Button) getView().findViewById(R.id.bt_frag_more_feedback);			       //反馈
		bt_frag_more_about = (Button) getView().findViewById(R.id.bt_frag_more_about);						       //关于我们
		bt_frag_more_exit =(Button) getView().findViewById(R.id.bt_frag_more_exit);							          //退出
		
		bt_frag_more_username.setText(User.userName);
	}
	
	//当Fragment可与用户交互时调用
	@Override
	public void onResume() {
		super.onResume();

		//注册监听
		bt_frag_more_username.setOnClickListener(this);		
	    bt_frag_more_setting.setOnClickListener(this); 
	    bt_frag_more_personal.setOnClickListener(this);		
		bt_frag_more_update.setOnClickListener(this);			
		bt_frag_more_feedback.setOnClickListener(this);			
		bt_frag_more_about.setOnClickListener(this);		
		bt_frag_more_exit.setOnClickListener(this);						
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.bt_frag_more_username:		//点击用户名的按钮进入自己的个人资料
			Intent intent0 = new Intent();
			intent0.setClass(getActivity(), LoginActivity.class);
			getActivity().startActivity(intent0);
			getActivity().finish();
			break;
		case R.id.bt_frag_more_setting:				//设置
			Intent intent = new Intent();
			intent.setClass(getActivity(), SettingActivity.class);
			getActivity().startActivity(intent);
			break;
		case R.id.bt_frag_more_personal:			//个人信息
			Intent intent2 = new Intent();
			intent2.setClass(getActivity(), LoginActivity.class);
			getActivity().startActivity(intent2);
			getActivity().finish();
			break;
		case R.id.bt_frag_more_update:				//更新
			//AllUse.info(getActivity().getApplication(), "没有更新");
			checkVersion();
			break;
		case R.id.bt_frag_more_feedback:			//反馈
			FeedbackAgent agent = new FeedbackAgent(getActivity());
			agent.startFeedbackActivity();
			break;
		case R.id.bt_frag_more_about:				//关于我们
			Intent about_us_intent = new Intent();
			about_us_intent.setClass(getActivity(), AboutUsActivity.class);
			startActivity(about_us_intent);
			break;
		case R.id.bt_frag_more_exit:					//退出
			exit();
			break;
		}	
	}
	
	//调用该方法更新应用程序
	private void  update() {
		UmengUpdateAgent.forceUpdate(getActivity());
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int arg0, UpdateResponse arg1) {
				switch (arg0) {
		        case UpdateStatus.Yes: 						// has update
		            UmengUpdateAgent.showUpdateDialog(getActivity(),arg1);
		        	//Toast.makeText(getActivity(), "changgong", Toast.LENGTH_SHORT).show();
		            break;
		        case UpdateStatus.No: 						// has no update
		            Toast.makeText(getActivity(), "没有更新", Toast.LENGTH_SHORT).show();
		            break;
		        case UpdateStatus.NoneWifi: 			// none wifi
		            Toast.makeText(getActivity(), "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
		            break;
		        case UpdateStatus.Timeout: 				// time out
		            Toast.makeText(getActivity(), "连接超时", Toast.LENGTH_SHORT).show();
		            break;
		        }
			}
		});
	}
	
	//退出当前账号时调用
	private void exit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());				
		builder.setTitle("提示")
			   .setMessage("退出将不再为您保存登陆状态")
			   .setCancelable(false)         				   
			   .setNegativeButton("确定", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   AllUse.editLoginStatus(getActivity());
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
	 /***
		 * 检查是否更新版本
		 */
		public void checkVersion() {
			myApplication = (ExceptionApplication) this.getActivity().getApplication();
			if (myApplication.localVersion < myApplication.serverVersion) {
				// 发现新版本，提示用户更新
				mUpdateManager = new UpdateManager(this.getActivity());
			    mUpdateManager.checkUpdateInfo();
			}else{
				AllUse.info(getActivity().getApplication(), "没有更新");
			}
		}
}
	
