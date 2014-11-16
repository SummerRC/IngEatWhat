package com.ing.eatwhat.activity;

import com.ing.eatwhat.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class AboutUsActivity extends Activity{
	private Button bt_back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//全屏显示  无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);	
		
		bt_back = (Button) this.findViewById(R.id.bt_back);
		bt_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AboutUsActivity.this.finish();
			}
		});
	}

}
