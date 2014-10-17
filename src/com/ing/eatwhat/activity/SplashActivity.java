package com.ing.eatwhat.activity;

import java.util.Timer;
import java.util.TimerTask;
import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.AllUse;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//隐去标题栏（应用程序的名字）  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐去状态栏部分(电池等图标和一切修饰部分)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        setContentView(R.layout.activity_splash);
		
		Timer timer = new Timer();
		//TimerTask是一个抽象类，需要实现其方法
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				SharedPreferences sp = getSharedPreferences("first", Context.MODE_PRIVATE);
				// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
				boolean isFirstIn = sp.getBoolean("isFirstIn", true);
				Log.i("isFirstIn", String.valueOf(isFirstIn));
				//如果第一次登录，则跳到引导界面
				if(isFirstIn) {
					Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
					startActivity(intent);
					finish();
					return;
				}
				//判断SharedPreferenced存储的登录信息haveLogined是否为true
				if(AllUse.islogined(SplashActivity.this)) {
					Intent intent = new Intent();
					intent.setClass(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					Intent intent =new Intent();
					intent.setClass(SplashActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
			}
		};
		
		//两秒之后执行一次
		timer.schedule(task, 1000*2); 
	}
	
}
