package com.ing.eatwhat.fragment;

import com.ing.eatwhat.R;
import com.ing.eatwhat.activity.AboutActivity;
import com.ing.eatwhat.activity.ChangePasswordActivity;
import com.ing.eatwhat.activity.TimeLineActivity;
import com.ing.eatwhat.entity.User;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MoreFragment extends Fragment implements View.OnClickListener{
	Button bt_frag_more_username;		//��ʾ�û����İ�ť���ɼ���	
	Button bt_frag_more_changePassword; //�޸�����
	Button bt_frag_more_timeLine;		//ʱ����
	Button bt_frag_more_update;			//����
	Button bt_frag_more_tips;			//С��ʿ
	Button bt_frag_more_about;			//��������	
	Button bt_frag_more_exit;			//�˳���ǰ�˺�

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_more, container, false);
	}
	
	//��Fragment����ɼ�ʱ����
	@Override
	public void onStart() {
		super.onStart();
		
		bt_frag_more_username = (Button) getView().findViewById(R.id.bt_frag_more_username);				//�û���
		bt_frag_more_changePassword = (Button) getView().findViewById(R.id.bt_frag_more_changePassword);	//��������
		bt_frag_more_timeLine = (Button) getView().findViewById(R.id.bt_frag_more_timeLine);				//ʱ����
		bt_frag_more_update = (Button) getView().findViewById(R.id.bt_frag_more_update);					//����
		bt_frag_more_tips = (Button) getView().findViewById(R.id.bt_frag_more_tips);						//С��ʿ
		bt_frag_more_about = (Button) getView().findViewById(R.id.bt_frag_more_about);						//��������
		bt_frag_more_exit =(Button) getView().findViewById(R.id.bt_frag_more_exit);							//�˳�
		
		bt_frag_more_username.setText(User.userName);
	}
	
	//��Fragment�����û�����ʱ����
	@Override
	public void onResume() {
		super.onResume();

		//ע�����
		bt_frag_more_username.setOnClickListener(this);		
	    bt_frag_more_changePassword.setOnClickListener(this); 
		bt_frag_more_timeLine.setOnClickListener(this);		
		bt_frag_more_update.setOnClickListener(this);			
		bt_frag_more_tips.setOnClickListener(this);			
		bt_frag_more_about.setOnClickListener(this);		
		bt_frag_more_exit.setOnClickListener(this);						
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.bt_frag_more_username:		//����û����İ�ť�����Լ��ĸ�������
			
			break;
		case R.id.bt_frag_more_changePassword:	//�޸�����
			Intent intent_changePassword = new Intent(getActivity(), ChangePasswordActivity.class);
			startActivity(intent_changePassword);
			break;
		case R.id.bt_frag_more_timeLine:		//ʱ����
			Intent intent_timeLine = new Intent(getActivity(), TimeLineActivity.class);
			startActivity(intent_timeLine);
			break;
		case R.id.bt_frag_more_update:			//����
			update();
			break;
		case R.id.bt_frag_more_tips:			//С��ʿ
			break;
		case R.id.bt_frag_more_about:			//��������
			Intent intent_about = new Intent(getActivity(), AboutActivity.class);
			startActivity(intent_about);
			break;
		case R.id.bt_frag_more_exit:			//�˳�
			exit();
			break;
		
		}
		
	}
	
	//���ø÷�������Ӧ�ó���
	private void  update() {
		
	}
	
	//�˳���ǰ�˺�ʱ����
	private void exit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());				
		builder.setTitle("��ʾ")
			   .setMessage("�˳�������Ϊ�������½״̬")
			   .setCancelable(false)         				   
			   .setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					    SharedPreferences sp = getActivity().getSharedPreferences("ingEatwhat", Context.MODE_PRIVATE);
						Editor editor=sp.edit();
						editor.putBoolean("haveLogined", false);
						editor.commit();
						getActivity().finish();								
				   }
			   })					   
			   .setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   dialog.dismiss();
				   }
			   })
			   .show();
	}
	
}
	
