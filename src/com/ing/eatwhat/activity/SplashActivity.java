package com.ing.eatwhat.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//隐去标题栏（应用程序的名字）  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐去状态栏部分(电池等图标和一切修饰部分)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
  
        InputStream in = this.getResources().openRawResource(R.drawable.bg_splash);
        ImageView iv_splash =  (ImageView) this.findViewById(R.id.iv_splash);
        iv_splash.setImageBitmap(BitmapFactory.decodeStream(in));
        if(in != null){
        	try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        //实例化一个计时器后台线程
		Timer timer = new Timer();
		//TimerTask是一个抽象类，需要实现其方法
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				SharedPreferences sp = getSharedPreferences("first", Context.MODE_PRIVATE);
				// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
				boolean isFirstIn = sp.getBoolean("isFirstIn", true);
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
					intent.setClass(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		};
		
		//两秒之后执行一次
		timer.schedule(task, 1000*2); 	
		init();
	}

	
	private void init() {
		//读取配置信息
		if(AllUse.getSharedPreferencesContent(this, "IMEI").equalsIgnoreCase("null")) {
			//获取设备IMEI码（15位数，唯一标识）并存储
			User.IMEI = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			AllUse.saveIMEI(this, User.IMEI);
		} else {
			User.IMEI = AllUse.getSharedPreferencesContent(this, "IMEI");
		}
		
		if(AllUse.getSharedPreferencesContent(this, "userName").equalsIgnoreCase("null")) {
			User.userName = "游客";
		} else {
			User.userName = AllUse.getSharedPreferencesContent(this, "userName");
		}	
		
		SharedPreferences sp = this.getSharedPreferences(User.userName + "config", Context.MODE_PRIVATE);
		int sensitivity = sp.getInt("sensitive", 0);
		if(sensitivity == 0) {
			Editor editor = sp.edit();
			editor.putInt("sensitive", 50);    
			editor.commit();
			
			User.sensitivity = 50;
		} else {
			User.sensitivity = sensitivity;
		}
	}
	
	
	
}
