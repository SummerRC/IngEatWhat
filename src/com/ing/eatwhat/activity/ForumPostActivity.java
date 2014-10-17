package com.ing.eatwhat.activity;

import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.User;
import com.ing.eatwhat.thread.GetMessagesNetThread;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForumPostActivity extends Activity implements View.OnClickListener{
	private static EditText et_content;
	//private static ImageView iv_addPicture;
	private static Button bt_post;
	private static Button bt_back;
	private Handler mHandler;    

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_fragment_post);	

		initialize();

		mHandler = new Handler() {
			public void handleMessage(Message msg) {	
				if(msg.what == 0) {						
					String result = msg.obj.toString().trim();
					Toast.makeText(ForumPostActivity.this, "帖子发送成功"+result, Toast.LENGTH_LONG).show();
					finish();	
				}
			}			
		};			
	}

	//初始化
	private void initialize() {
		//iv_addPicture = (ImageView ) findViewById(R.id.iv_addPicture);
		et_content = (EditText) findViewById(R.id.et_content);
		bt_post = (Button) findViewById(R.id.bt_post);
		bt_post.setOnClickListener(this);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_back.setOnClickListener(this);
	}		

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.bt_post:
			post();
			break;
		case R.id.bt_back:
			finish();
			break;
		}	
	}
	
	//发送帖子
	private void post() {
		String content = et_content.getText().toString().trim();
		//判断是否联网
		if(!AllUse.isHaveInternet(this)){
			Toast.makeText(ForumPostActivity.this, "未连接网络,操作失败", Toast.LENGTH_SHORT).show();
			return;
		}
		if(content.equals("")) {
			AllUse.info(ForumPostActivity.this, "警告：输入文字不能为空！");
			return;
		}	
		if(content.length() > 288) {
			AllUse.info(ForumPostActivity.this, "警告：输入文字过长！");
			return;
		}
		
		content = content.replace(' ','$');

		String author = User.userName;
		
		String url ="http://cqcreer.jd-app.com/add_note.php?content=" + content + "&author="+author;
		GetMessagesNetThread thread = new GetMessagesNetThread(mHandler, url, 0);
		thread.start();					
	}
}