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
	private static Handler mHandler;         //注意：handler如果不申明为静态的话,eclipse警告可能会导致内存泄露，详情:http://www.cppblog.com/tx7do/archive/2013/11/14/204251.aspx
	private EditText et_logon_name;          //注册用户名的文本框
	private EditText et_logon_password1;     //输入密码的文本框
	private EditText et_logon_password2;     //确认密码的文本框
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//全屏显示  无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_register);	
		
		et_logon_name = (EditText)findViewById(R.id.emailorPN);
		et_logon_password1 = (EditText)findViewById(R.id.password);
		et_logon_password2 = (EditText)findViewById(R.id.passwordagain);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case 0:
					//result储存的是注册线程返回的信息：ok代表注册成功,failed代表注册失败
					String result = msg.obj.toString();
					if(result.compareToIgnoreCase("ok") == 0) {
						AllUse.info(RegisterActivity.this, "注册成功！将跳转到引导界面！");	
						//存储新的账号登录信息
						AllUse.saveLoginStatus(RegisterActivity.this, User.userName, User.userPassword, true);				
						//跳转到主界面
						Intent intent = new Intent();
						intent.setClass(RegisterActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
					} else
						if(result.compareToIgnoreCase("failed") == 0) {
							AllUse.info(RegisterActivity.this, "用户名已存在！");
							clean();
							return;
						}
					break;
				case 222:
					AllUse.info(RegisterActivity.this, "用户名不能包含特殊字符：? | & 空格等.");
					clean();
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
		limit();		
		//开启注册用户的线程
		NetThread netThread = new NetThread(mHandler, User.userName, User.userPassword, null, null, 2);
		netThread.start();
	}
	
	//返回到登陆界面的方法(该按钮的监听器的注册是在xml文件中进行的)
	public void BackClick(View view) {
		Intent intent = new Intent();
		intent.setClass(RegisterActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	//注册账号时的相关限制
	private void limit() {	
		User.userName = et_logon_name.getText().toString().trim();
		User.userPassword = et_logon_password1.getText().toString().trim();
		String password = et_logon_password2.getText().toString().trim();		
		//限制三个文本框不能为空
		if(TextUtils.isEmpty(User.userName) || TextUtils.isEmpty(User.userPassword) || TextUtils.isEmpty(password)) {
			AllUse.info(RegisterActivity.this, "用户名或者密码不能为空！");
			clean();
			return;
		}
		//限制用户名不能少于四个字符
		if(et_logon_name.getText().length() < 4) {
			AllUse.info(RegisterActivity.this, "警告：用户名长度少于4个字符！");
			clean();
			return;
		}
		//限制用户名不能多于18个字符
		if(et_logon_name.getText().length() > 18 || et_logon_password1.getText().length() > 18 || et_logon_password2.getText().length() > 18 ) {
			AllUse.info(RegisterActivity.this, "警告：用户名和密码长度均不能超出18个字符！");
			clean();
			return;
		}				
		//限制密码长度不能少于六个字符
		if(et_logon_password1.getText().length() < 6 || et_logon_password2.getText().length() < 6 ) {
			AllUse.info(RegisterActivity.this, "警告：密码长度少于6个字符！");
			clean();
			return;
		}
		//限制密码长度不能多于18个字符
		if(et_logon_password1.getText().length() > 18 || et_logon_password2.getText().length() > 18) {
			AllUse.info(RegisterActivity.this, "警告：密码长度大于18个字符！");
			clean();
			return;
		}	
		//判断两次密码是否一致
		if(!(User.userPassword.compareTo(password) == 0)){
			AllUse.info(RegisterActivity.this, "两次密码不一致！");
			clean();
			return;
		}
	}
	
	//清空文本框的内容
	private void clean() {
		et_logon_name.setText("");
		et_logon_password1.setText("");
		et_logon_password2.setText("");
	}
}
