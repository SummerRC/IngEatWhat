package com.ing.eatwhat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.User;
import com.ing.eatwhat.thread.NetThread;

public class LoginActivity extends Activity {
 
	private Handler mHandler;
	private EditText et_login_name;               //���� �û������ı���
	private EditText et_login_password;           //����������ı���
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//ȫ����ʾ  �ޱ�����
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_login);
		
		et_login_name = (EditText)findViewById(R.id.et_login_name);
		et_login_password = (EditText)findViewById(R.id.et_login_password);
		
		//�û������ز�Ϊnullʱ��˵���洢���û��ĵ�¼��Ϣ����ô�Զ����
		if(!AllUse.getSharedPreferencesContent(LoginActivity.this, "userName").equalsIgnoreCase("null")) {
			//�Զ�������һ�ε�¼����Ϣ���û���������
			et_login_name.setText(AllUse.getSharedPreferencesContent(LoginActivity.this, "userName"));
			et_login_password.setText(AllUse.getSharedPreferencesContent(LoginActivity.this, "userPassword"));		
		}
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {	
				switch(msg.what) {
				case 0:                          //��¼��֤�̷߳��͵���Ϣ
					//��ȡ�̷߳��صĽ��
					String result = msg.obj.toString().trim();	
					if(result.compareToIgnoreCase("ok") == 0) {						
						AllUse.info(LoginActivity.this, "��½�ɹ�!");						
						//����ֵΪ-1˵��food_num�����ڣ����û���һ�ε�¼,��ô��Ҫ��ʼ����¼��Ϣ(���ж�һ�µĻ�����¼��Ϣ�ᱻ���ǵ�)
						if(AllUse.getFoodNum(LoginActivity.this) == -1) {	
							//����ǵ�һ�ε�¼�ͳ�ʼ���û���¼��Ϣ
							AllUse.saveLoginStatus(LoginActivity.this, User.userName, User.userPassword, true);
						} else 
							if(AllUse.getSharedPreferencesContent(LoginActivity.this, "userName").equalsIgnoreCase(User.userName)) {
								//˵����¼����ԭ�˺�,��ô�޸�haveLoginedΪtrue
								AllUse.editLoginStatus(LoginActivity.this, true);
							} else {				
								//˵����¼��һ���˺�
								AllUse.saveLoginStatus(LoginActivity.this, User.userName, User.userPassword, true);
							}						
						//�����������Ҫ�������ϵ�ʳ��ͼƬ����1��app��ж�أ� ��2����������ֻ��ϵ�¼
						if(AllUse.getFoodNum(LoginActivity.this) == 0 && AllUse.real_foodnum > 0) {
						 	//��ת�����ؽ���
							Intent intent = new Intent(LoginActivity.this, LoadActivity.class);
						 	startActivity(intent);
							finish();
						} else {
							//��ת��������
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this, MainActivity.class);
							startActivity(intent);
							finish();
						}	
					}					
					if(result.compareToIgnoreCase("wrong") == 0) {
						AllUse.info(LoginActivity.this, "�������");
						clean();
						return;
					} 					
					if(result.compareToIgnoreCase("notexist") == 0) {
						AllUse.info(LoginActivity.this, "�û���������!");
						clean();
						return;
					} 					
					break;					
				case 1:                     //��ȡ�û�food��Ŀ�̷߳��͵���Ϣ
					String str = msg.obj.toString().trim();
					AllUse.real_foodnum = Integer.valueOf(str).intValue();
					break;
				default:
					AllUse.info(getApplicationContext(), "δ֪����!");
					clean();
					return;
				}				
			}
		};
	}

	//��¼��ť�ļ�����
	public void LoginClick(View view) {	
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, LoadActivity.class);
		startActivity(intent);
		finish();
		
		//�û��������벻��Ϊ��
		if(TextUtils.isEmpty(et_login_name.getText().toString().trim()) || TextUtils.isEmpty(et_login_password.getText().toString().trim())) {
			AllUse.info(LoginActivity.this, "�û��������벻��Ϊ�գ�");
			clean();
			return;
		}
		//�û��������볤�Ȳ��ܳ���18���ַ�
		if(et_login_name.getText().length() > 18 || et_login_password.getText().length() > 18 ) {
			AllUse.info(LoginActivity.this, "���棺�û��������볤�Ⱦ����ܳ���18���ַ���");
			clean();
			return;
		}		
		//�ж��Ƿ�����
		if(!AllUse.isHaveInternet(LoginActivity.this)){
			AllUse.info(LoginActivity.this, "�������Ӵ���,����ʧ��");
			clean();
			return;
		}	
		//��ø��û���ʳ�� ����
		NetThread getNumber_thread = new NetThread(mHandler, et_login_name.getText().toString().trim(), null, null, null, 7);
		getNumber_thread.start();		
		//��ȡʳ����Ŀ�����������߳�ִ��,�����ֻ�ȴ�һ��
		try {
			getNumber_thread.join(1000);
		} catch (InterruptedException e) {
			AllUse.info(getApplicationContext(), "������������µ�¼!");
			clean();
			return;
		}		
		//����������Ҫ��Ϊ���ӳٵ�¼��֤�߳�ִ�е�ʱ�䣬Ϊ��ȡ�û�ʳ��������߳���ȡʱ��
		User.userName = et_login_name.getText().toString().trim();
		User.userPassword = et_login_password.getText().toString().trim();		
		//��¼��֤���߳�
		NetThread login_thread = new NetThread(mHandler, User.userName, User.userPassword, null, null, 1);
		login_thread.start();
	}
	
	//ע�ᰴť�ļ�������������ע�����
	public void LogonClick(View view) {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
		finish();
	}
	
	//��EditText��գ�����ʼ��User.userName�� User.userPassword
	private void clean() {
		et_login_name.setText("");
		et_login_password.setText("");
		User.userName = "";
		User.userPassword = "";
	}
}
