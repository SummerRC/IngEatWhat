package com.ing.eatwhat.activity;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.User;
import com.ing.eatwhat.thread.NetThread;

@SuppressLint("HandlerLeak")
public class LoginActivity extends Activity {
 
	private Handler mHandler;
	private EditText et_login_name;               //输入 用户名的文本框
	private EditText et_login_password;           //输入密码的文本框
	private static long preTime = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//全屏显示  无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_login);
		
		et_login_name = (EditText)findViewById(R.id.et_login_name);
		et_login_password = (EditText)findViewById(R.id.et_login_password);
		
		//用户名返回不为null时，说明存储有用户的登录信息，那么自动填充
		if(!AllUse.getSharedPreferencesContent(LoginActivity.this, "userName").equalsIgnoreCase("null")) {
			//自动填充最后一次登录的信息：用户名和密码
			et_login_name.setText(AllUse.getSharedPreferencesContent(LoginActivity.this, "userName"));
			et_login_password.setText(AllUse.getSharedPreferencesContent(LoginActivity.this, "userPassword"));		
		}
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {	
				switch(msg.what) {
				case 0:                          //登录验证线程发送的消息
					//获取线程返回的结果
					String result = msg.obj.toString().trim();	
					if(result.compareToIgnoreCase("ok") == 0) {						
						AllUse.info(getApplication(), "登陆成功!");		
						
						User.userName = et_login_name.getText().toString().trim();
						User.userPassword = et_login_password.getText().toString().trim();	
						User.food_num = AllUse.getFoodNum(LoginActivity.this);
						
						//初始化用户登录信息
						AllUse.saveLoginStatus(LoginActivity.this, User.userName, User.userPassword, User.food_num);
						
						//跳转到主界面
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, MainActivity.class);
						startActivity(intent);
						finish();	
					}					
					if(result.compareToIgnoreCase("wrong") == 0) {
						AllUse.info(getApplication(), "密码错误！");
						et_login_password.setText("");
						et_login_password.requestFocus();
						return;
					} 					
					if(result.compareToIgnoreCase("notexist") == 0) {
						AllUse.info(getApplication(), "用户名不存在!");
						et_login_name.setText("");
						et_login_password.setText("");
						et_login_name.requestFocus();
						return;
					} 					
					break;					
				case 1:                     //获取用户food数目线程发送的消息	
					break;
				default:
					AllUse.info(getApplication(), "未知错误!");
					return;
				}				
			}
		};
	}

	//登录按钮的监听器
	public void LoginClick(View view) {	
		//防止同一时间内多次点击
		if(AllUse.click_limit(preTime) == -1) {
			return;
		}
		//用户名、密码不能为空
		if(TextUtils.isEmpty(et_login_name.getText().toString().trim())) { 
			AllUse.info(getApplication(), "用户名不能为空！");
			et_login_name.setText("");
			et_login_password.setText("");
			et_login_name.requestFocus();
			return;
		}
		if(TextUtils.isEmpty(et_login_password.getText().toString().trim())) {
			AllUse.info(getApplication(), "密码不能为空！");
			et_login_password.setText("");
			et_login_password.requestFocus();
			return;
		}
		//用户名、密码长度不能超过18个字符
		if(et_login_name.getText().length() > 18) {
			AllUse.info(getApplication(), "警告：用户名不能超出18个字符！");
			et_login_name.requestFocus();
			return;
		}		
		if(et_login_password.getText().length() > 18 ) {
			AllUse.info(getApplication(), "警告：密码不能超出18个字符！");
			et_login_password.requestFocus();
			return;
		}
		if(AllUse.strLimit(et_login_name.getText().toString().trim()) == -1) {
			AllUse.info(getApplication(), "警告：用户名不能含有特殊字符！");
			et_login_name.setText("");
			et_login_name.requestFocus();
			return;
		}
		if(AllUse.strLimit(et_login_password.getText().toString().trim()) == -1) {
			AllUse.info(getApplication(), "警告：密码不能含有特殊字符！");
			et_login_password.setText("");
			et_login_password.requestFocus();
			return;
		}
		//判断是否联网
		if(!AllUse.isHaveInternet(LoginActivity.this)){
			AllUse.info(getApplication(), "网络连接错误,操作失败");
			return;
		}	
		
		NetThread thread = new NetThread(mHandler, et_login_name.getText().toString().trim(), et_login_password.getText().toString().trim(), null, null, 1);
		thread.start();
	}
	
	//注册按钮的监听器，将跳往注册界面
	public void LogonClick(View view) {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
		finish();
	}
	
	//监听返回按键
	@Override
	public void onBackPressed() {
		//android.os.Process.killProcess(android.os.Process.myPid()); 		//结束整个应用程序
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		this.finish();
	}
	
}
