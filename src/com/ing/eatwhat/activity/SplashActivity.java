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
		
		//��ȥ��������Ӧ�ó�������֣�  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //��ȥ״̬������(��ص�ͼ���һ�����β���)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        setContentView(R.layout.activity_splash);
		
		Timer timer = new Timer();
		//TimerTask��һ�������࣬��Ҫʵ���䷽��
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				SharedPreferences sp = getSharedPreferences("first", Context.MODE_PRIVATE);
				// ȡ����Ӧ��ֵ�����û�и�ֵ��˵����δд�룬��true��ΪĬ��ֵ
				boolean isFirstIn = sp.getBoolean("isFirstIn", true);
				Log.i("isFirstIn", String.valueOf(isFirstIn));
				//�����һ�ε�¼����������������
				if(isFirstIn) {
					Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
					startActivity(intent);
					finish();
					return;
				}
				//�ж�SharedPreferenced�洢�ĵ�¼��ϢhaveLogined�Ƿ�Ϊtrue
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
		
		//����֮��ִ��һ��
		timer.schedule(task, 1000*2); 
	}
	
}
