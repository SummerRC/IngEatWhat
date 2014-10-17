package com.ing.eatwhat.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.ing.eatwhat.R;
import com.ing.eatwhat.activity.ContentActivity;
import com.ing.eatwhat.activity.ForumPostActivity;
import com.ing.eatwhat.adapter.ForumFragmentAdapter;
import com.ing.eatwhat.database.DBManager;
import com.ing.eatwhat.thread.GetMessagesNetThread;
import com.ing.eatwhat.widget.MyListView;

public class ForumFragment extends Fragment {
	private static Button bt_frag_post;				//�����İ�ť
	private static MyListView myListView;			//�Զ����ListView 
	private static ForumFragmentAdapter adapter;	//������
	HashMap<String, ArrayList<String>> map;
	private Context context;
	private DBManager dbManager;
	private final static int REFRESH_DATA_FINISH = 2;	//����ˢ�¼����������
	private final static int LOAD_DATA_FINISH = 3;		//����������� 
		
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 1:
				//��������Ϣ���뵽���ݿ��У�ע���и��ӣ������߳��в������ݿ�������������
				map = (HashMap<String, ArrayList<String>>) msg.obj;
				dbManager.insertMessages(map);
				break;
			case REFRESH_DATA_FINISH:	//����ˢ�����
				if(adapter != null){
					//ע�⣺����д��Ϊ�˽��notifyDataSetChanged()��ˢ�µ����⣬��ˢ�²�����ԭ���ʶ���
					adapter.date.clear();
					adapter.date.addAll(map.get("date"));
					adapter.userName.clear();
					adapter.userName.addAll(map.get("userName"));
					adapter.content.clear();
					adapter.content.addAll(map.get("content"));
					adapter.notifyDataSetChanged();
				}
				myListView.onRefreshComplete();	
				break;
			case LOAD_DATA_FINISH:      //���ظ������
				if(adapter !=null){
					adapter.map = (HashMap<String, ArrayList<String>>)msg.obj;
					adapter.notifyDataSetChanged();
				}
				myListView.onLoadMoreComplete();	
				break;
			}
			
			if(msg.what == 1) {
							
			}
		}
	};
	
	//���캯��
	public ForumFragment(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = this.getActivity();
		dbManager = new DBManager(context);
		//�����̷߳���������Ϣ
		GetMessagesNetThread thread = new GetMessagesNetThread(mHandler, "http://cqcreer.jd-app.com/get_note.php?index=0", 1);
		thread.start();
		return inflater.inflate(R.layout.fragment_forum, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		bt_frag_post = (Button) getView().findViewById(R.id.bt_frag_post);
		bt_frag_post.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent=new Intent();
				intent.setClass(getActivity(), ForumPostActivity.class);
				getActivity().startActivity(intent);
			}
		});
		
		buildData();
		initView();
	}
	
	//��ʼ��������ݣ�����������������Ϣ���뵽���ݿ⣬�ٴ����ݿ��������������
	private void buildData() {		
		map = dbManager.getMessages();	
		
	}
	
	//��ʼ��view
	private void initView() {
		//����������
		myListView = (MyListView)getView().findViewById(R.id.mylistview);
		adapter = new ForumFragmentAdapter(context, map);
		myListView.setAdapter(adapter);

		//����ˢ�µļ�����
		myListView.setOnRefreshListener(new com.ing.eatwhat.widget.MyListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadData(0);
			}
		});

		//���ظ���ļ�����
		myListView.setOnLoadListener(new com.ing.eatwhat.widget.MyListView.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				loadData(1);
			}
		});

		//ѡ��ÿһ����¼�ļ�����
		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				//��ȡ�����item����Ϣ
				TextView tv_userName =(TextView) v.findViewById(R.id.tv_userName);
				TextView tv_date =(TextView) v.findViewById(R.id.tv_date);
				TextView tv_content =(TextView) v.findViewById(R.id.tv_content);
				String userName = tv_userName.getText().toString().trim();	
				String date = tv_date.getText().toString().trim();
				String content = tv_content.getText().toString().trim();
	
				Intent intent = new Intent();			
				intent.putExtra("userName", userName);
				intent.putExtra("date", date);
				intent.putExtra("content", content);
				intent.setClass(getActivity(), ContentActivity.class);
				getActivity().startActivity(intent);
			}
		});	
	}
	
	//����ˢ�ºͼ��ظ���  �����ݵĴ���
	private void loadData(final int op) {
		//�����̷߳���������Ϣ
		GetMessagesNetThread thread = new GetMessagesNetThread(mHandler, "http://cqcreer.jd-app.com/get_note.php?index=0", 1);
		thread.start();
		new Thread(){
			@Override
			public void run() {
				switch (op) {
				case 0:
					//����ˢ��2
					//�����ݿ��map���в���
					break;
				case 1:					
					//���ظ���
					//�����ݿ��map���в���
					break;
				}
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(op == 0){	//����ˢ��
					 //Collections.reverse(mList);	/������
					Message _Msg = mHandler.obtainMessage(REFRESH_DATA_FINISH, map);
					mHandler.sendMessage(_Msg);
				}else if(op == 1){
					Message _Msg = mHandler.obtainMessage(LOAD_DATA_FINISH, map);
					mHandler.sendMessage(_Msg);
				}
			}
		}.start();
	}
}
