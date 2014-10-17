package com.ing.eatwhat.activity;

import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.User;
import com.ing.eatwhat.thread.NetThread;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class RegisterActivity extends Activity{
	private static Handler mHandler;         //ע�⣺handler���������Ϊ��̬�Ļ�,eclipse������ܻᵼ���ڴ�й¶������:http://www.cppblog.com/tx7do/archive/2013/11/14/204251.aspx
	private EditText et_logon_name;          //ע���û������ı���
	private EditText et_logon_password1;     //����������ı���
	private EditText et_logon_password2;     //ȷ��������ı���
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//ȫ����ʾ  �ޱ�����
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
					//result�������ע���̷߳��ص���Ϣ��ok����ע��ɹ�,failed����ע��ʧ��
					String result = msg.obj.toString();
					if(result.compareToIgnoreCase("ok") == 0) {
						AllUse.info(RegisterActivity.this, "ע��ɹ�������ת���������棡");	
						//�洢�µ��˺ŵ�¼��Ϣ
						AllUse.saveLoginStatus(RegisterActivity.this, User.userName, User.userPassword, true);				
						//��ת��������
						Intent intent = new Intent();
						intent.setClass(RegisterActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
					} else
						if(result.compareToIgnoreCase("failed") == 0) {
							AllUse.info(RegisterActivity.this, "�û����Ѵ��ڣ�");
							clean();
							return;
						}
				case 222:
					AllUse.info(RegisterActivity.this, "�û������ܰ��������ַ���? | & �ո��.");
					clean();
					break;
				default:
					//do  something
					break;
				}	
			}
		};
	}
	
	//ע�ᰴť�ļ���(�ð�ť�ļ�������ע������xml�ļ��н��е�)
	public void MakeSureClick(View view) {
		limit();		
		//����ע���û����߳�
		NetThread netThread = new NetThread(mHandler, User.userName, User.userPassword, null, null, 2);
		netThread.start();
	}
	
	//���ص���½����ķ���(�ð�ť�ļ�������ע������xml�ļ��н��е�)
	public void BackClick(View view) {
		Intent intent = new Intent();
		intent.setClass(RegisterActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	//ע���˺�ʱ���������
	private void limit() {	
		User.userName = et_logon_name.getText().toString().trim();
		User.userPassword = et_logon_password1.getText().toString().trim();
		String password = et_logon_password2.getText().toString().trim();		
		//���������ı�����Ϊ��
		if(TextUtils.isEmpty(User.userName) || TextUtils.isEmpty(User.userPassword) || TextUtils.isEmpty(password)) {
			AllUse.info(RegisterActivity.this, "�û����������벻��Ϊ�գ�");
			clean();
			return;
		}
		//�����û������������ĸ��ַ�
		if(et_logon_name.getText().length() < 4) {
			AllUse.info(RegisterActivity.this, "���棺�û�����������4���ַ���");
			clean();
			return;
		}
		//�����û������ܶ���18���ַ�
		if(et_logon_name.getText().length() > 18 || et_logon_password1.getText().length() > 18 || et_logon_password2.getText().length() > 18 ) {
			AllUse.info(RegisterActivity.this, "���棺�û��������볤�Ⱦ����ܳ���18���ַ���");
			clean();
			return;
		}				
		//�������볤�Ȳ������������ַ�
		if(et_logon_password1.getText().length() < 6 || et_logon_password2.getText().length() < 6 ) {
			AllUse.info(RegisterActivity.this, "���棺���볤������6���ַ���");
			clean();
			return;
		}
		//�������볤�Ȳ��ܶ���18���ַ�
		if(et_logon_password1.getText().length() > 18 || et_logon_password2.getText().length() > 18) {
			AllUse.info(RegisterActivity.this, "���棺���볤�ȴ���18���ַ���");
			clean();
			return;
		}	
		//�ж����������Ƿ�һ��
		if(!(User.userPassword.compareTo(password) == 0)){
			AllUse.info(RegisterActivity.this, "�������벻һ�£�");
			clean();
			return;
		}
	}
	
	//����ı��������
	private void clean() {
		et_logon_name.setText("");
		et_logon_password1.setText("");
		et_logon_password2.setText("");
	}
}
