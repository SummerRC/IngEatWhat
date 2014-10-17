package com.ing.eatwhat.activity;

import com.ing.eatwhat.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadActivity extends Activity{

	ProgressBar progressBar;
	TextView progress;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				Intent intent = new Intent();
				intent.setClass(LoadActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;	
			case 1:
				progress.setText("%"+msg.obj);
				break;
			}			
			}							
	};
//	private Handler handler1 = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch(msg.what){
//			case 1:
//				progress.setText("%"+msg.arg1);
//				break;
//			}			
//		}		
//	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_load);	
		progress = (TextView) this.findViewById(R.id.progress);
		progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
		
		Thread updateThread = new Thread(new Runnable() {			
			public void run() {
				
//				Message msg1 = new Message();
				for(int i=0;i<100;i++){
					try {
						Thread.sleep(100);
						progressBar.setProgress(i+1);
//						msg.recycle();
						Message msg = Message.obtain();						
						msg.what = 1;
						msg.obj=i+1;
						handler.sendMessage(msg);
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}	
				Message msg = Message.obtain();
				msg.what = 0;
				handler.sendMessage(msg);
				
			}	
		});
		updateThread.start(); /* Æô¶¯Ïß³Ì */
	
		}						
}

