package com.ing.eatwhat.activity;

import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.User;
import com.ing.eatwhat.thread.NetThread;
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

@SuppressLint("HandlerLeak")
public class RegisterActivity extends Activity{
	private static Handler mHandler;        		 //注意：handler如果不申明为静态的话,eclipse警告可能会导致内存泄露，详情:http://www.cppblog.com/tx7do/archive/2013/11/14/204251.aspx
	private EditText et_logon_username;       //注册用户名的文本框
	private EditText et_logon_password1;     //输入密码的文本框
	private EditText et_logon_password2;     //确认密码的文本框
	private static long preTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//全屏显示  无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_register);	
		
		et_logon_username = (EditText)findViewById(R.id.et_logon_username);
		et_logon_password1 = (EditText)findViewById(R.id.et_logon_password1);
		et_logon_password2 = (EditText)findViewById(R.id.et_logon_password2);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case 0:
					//result储存的是注册线程返回的信息：ok代表注册成功,failed代表注册失败
					String result = msg.obj.toString();
					if(result.compareToIgnoreCase("ok") == 0) {
						AllUse.info(getApplication(), "注册成功！");
						
						User.userName = et_logon_username.getText().toString().trim();
						User.userPassword = et_logon_password1.getText().toString().trim();
						
						//存储新的账号登录信息
						AllUse.saveLoginStatus(RegisterActivity.this, User.userName, User.userPassword, true);				
						//跳转到主界面
						Intent intent = new Intent();
						intent.setClass(RegisterActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
					} else
						if(result.compareToIgnoreCase("failed") == 0) {
							AllUse.info(getApplication(), "用户名已存在！");
							et_logon_username.setText("");
							et_logon_username.requestFocus();
							return;
						}
					break;
				case 222:
					AllUse.info(getApplication(), "用户名不能包含特殊字符：? | & 空格等.");
					et_logon_username.setText("");
					et_logon_username.requestFocus();
					break;
				default:
					//do  something
					break;
				}	
			}
		};
	}
	
	//注册按钮的监听(该按钮的监听器的注册是在xml文件中进行的)
	public void MakeSureClick(View view) {
		//防止同一时间内多次点击
		AllUse.click_limit(preTime);
		if(limit() == -1) {
			return;		
		}
		NetThread thread = new NetThread(mHandler, et_logon_username.getText().toString().trim(), et_logon_password1.getText().toString().trim(), null, null, 2);
		thread.start();
	}
	
	//返回到登陆界面的方法(该按钮的监听器的注册是在xml文件中进行的)
	public void BackClick(View view) {
		Intent intent = new Intent();
		intent.setClass(RegisterActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	//注册账号时的相关限制
	private int limit() {	
		String name = et_logon_username.getText().toString().trim();
		String password1 = et_logon_password1.getText().toString().trim();
		String password2 = et_logon_password2.getText().toString().trim();		
		//限制三个文本框不能为空
		if(TextUtils.isEmpty(name)) {
			AllUse.info(getApplication(), "请输入用户名！");
			et_logon_username.requestFocus();
			return -1;
		}
		if(TextUtils.isEmpty(password1)) {
			AllUse.info(getApplication(), "请输入密码！");
			et_logon_password1.requestFocus();
			return -1;
		}
		if(TextUtils.isEmpty(password2)) {
			AllUse.info(getApplication(), "请确认密码！");
			et_logon_password2.requestFocus();
			return -1;
		}
		//限制用户名不能少于四个字符
		if(et_logon_username.getText().length() < 2) {
			AllUse.info(getApplication(), "警告：用户名长度少于2个字符！");
			et_logon_username.requestFocus();
			return -1;
		}
		//限制用户名不能多于18个字符
		if(et_logon_username.getText().length() > 18) {
			AllUse.info(getApplication(), "警告：用户名长度不能超出18个字符！");
			et_logon_username.requestFocus();
			return -1;
		}		
		if(et_logon_password1.getText().length() > 18 || et_logon_password2.getText().length() > 18) {
			AllUse.info(getApplication(), "警告：密码长度不能超出18个字符！");
			et_logon_password1.setText("");
			et_logon_password2.setText("");
			et_logon_password1.requestFocus();
			return -1;
		}
		//限制密码长度不能少于六个字符
		if(et_logon_password1.getText().length() < 6 || et_logon_password2.getText().length() < 6 ) {
			AllUse.info(getApplication(), "警告：密码长度不能少于6个字符！");
			et_logon_password1.setText("");
			et_logon_password2.setText("");
			et_logon_password1.requestFocus();
			return -1;
		}
		//判断两次密码是否一致
		if(!(password1.compareTo(password2) == 0)){
			AllUse.info(getApplication(), "两次密码不一致！");
			et_logon_password1.setText("");
			et_logon_password2.setText("");
			et_logon_password1.requestFocus();
			return -1;
		}
		return 0;
	}
	
	//监听返回按键
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(RegisterActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}
