package com.ing.eatwhat.activity;

import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.User;
import com.ing.eatwhat.thread.NetThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends Activity implements OnClickListener{
	
	private Handler mHandler;
	private EditText et_oldPassword;
	private EditText et_newPassword;
	private EditText et_newPasswordAgain;
	private Button bt_ok;
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_changepassword);	
	
		mHandler = new Handler() {		
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case 1:
					String result = msg.obj.toString().trim();
					Log.i("����", result);
					if(result.equalsIgnoreCase("seccess")) {
						AllUse.info(ChangePasswordActivity.this, "�޸ĳɹ�");
						finish();
					}
					if(result.equalsIgnoreCase("fail")) {
						AllUse.info(ChangePasswordActivity.this, "�޸�ʧ�ܣ������ԣ�");
						clean();
						return;
					}
					if(result.equalsIgnoreCase("wrong")) {
						AllUse.info(ChangePasswordActivity.this, "�޸�ʧ�ܣ����Ժ����ԣ�");
						finish();
					}
					break;
				
				default:
						//do something
				}
			}
		};
		
		init();
	}

	//��ʼ��
	private void init() {
		et_oldPassword = (EditText) findViewById(R.id.et_oldPassword);
		et_newPassword = (EditText) findViewById(R.id.et_newPassword);
		et_newPasswordAgain = (EditText) findViewById(R.id.et_newPasswordAgain);
		bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.bt_ok:
			changePassworld();
			break;
		}
	}
	
	private void changePassworld() {
		String oldPassword = et_oldPassword.getText().toString().trim();
		String newPassword = et_newPassword.getText().toString().trim();
		String newPasswordAgain = et_newPasswordAgain.getText().toString().trim();
		
		//���������ı�����Ϊ��
		if(TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(newPasswordAgain)) {
			AllUse.info(ChangePasswordActivity.this, "���벻��Ϊ�գ�");
			clean();
			return;
		}
			
		//�������볤�Ȳ������������ַ�
		if(newPassword.length() < 6 || newPasswordAgain.length() < 6) {
			AllUse.info(ChangePasswordActivity.this, "���棺���볤������6���ַ���");
			clean();
			return;
		}
		//�������볤�Ȳ��ܶ���18���ַ�
		if(newPassword.length() > 18 || newPasswordAgain.length() > 18) {
			AllUse.info(ChangePasswordActivity.this, "���棺���볤�ȴ���18���ַ���");
			clean();
			return;
		}	
		
		if(!newPassword.equals(newPasswordAgain)) {
			AllUse.info(ChangePasswordActivity.this, "���棺�������벻һ�£�");
			clean();
			return;
		}
		
		//ȥ���������޸�����
		NetThread thread = new NetThread(mHandler, User.userName, User.userPassword, newPassword, null, 8);
		thread.start();
	}

	private void clean() {
		et_oldPassword.setText("");
		et_newPassword.setText("");
		et_newPasswordAgain.setText("");
	}
}
