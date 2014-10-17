package com.ing.eatwhat.thread;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import android.os.Handler;
import android.os.Message;

public class GetMessagesNetThread extends Thread {	
	private Handler mHandler;
	private String url;
	DefaultHttpClient client;
	HttpResponse response;
	Message msg;
	int i = -1;					//����ѡ��

	//���캯��
	public GetMessagesNetThread(Handler mHandler, String url, int i) {
		this.mHandler = mHandler;
		this.url = url;
		this.i = i;
		msg = new Message();
	}
	
	public void run() {		
		client = new DefaultHttpClient();
	    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);  //��������ʱʱ��
	    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 50000);         // ��ȡ��ʱ
	    HttpGet get = new HttpGet(url);	    
		try {
			response = client.execute(get);
			if(response.getStatusLine().getStatusCode() == 200) {
				switch(i){
				case 0:
					addNote();		//����
					break;
				case 1:
					getNote();		//��������
					break;
				default:
					break;
				}
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����
	 */
	private void addNote() throws Exception {	
		BufferedReader bin = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String result = bin.readLine();
	    
	    msg.what = 0;
	    msg.obj = result;
		mHandler.sendMessage(msg);
	}
	
	/**
	 * �������ʮ������
	 */
	private void getNote() throws Exception {	
		HttpEntity entity = response.getEntity();			
		StringBuilder sBuilder = new StringBuilder();
		
		InputStream inputStream = entity.getContent();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(inputStreamReader);
		String s;
		while (((s = reader.readLine()) != null)) {
			sBuilder.append(s);
		}
	
		//�ر������ͷ���Դ
		inputStream.close();								
		inputStreamReader.close();
		reader.close();		

		String abc = sBuilder.toString();
		abc = abc.replace("'", "\"");

		//���ַ���ת����һ��Json����
		JSONArray jsonArray = new JSONArray(abc); 	

		//�洢����ʱ�䡢�û��������ݵ�����	
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> date = new ArrayList<String>();		
		ArrayList<String> userName = new ArrayList<String>();
		ArrayList<String> content = new ArrayList<String>();
		
		//ȡ��Json������������Ϣ
		for(int i=0; i<10; i++){							
			date.add(jsonArray.getJSONObject(i).get("date").toString());
			userName.add(jsonArray.getJSONObject(i).get("username").toString());
			content.add(jsonArray.getJSONObject(i).get("content").toString().replace('$',' '));
		}      		 
		
		//��������Ϣ��װ��HashMap�У�ͨ��Message���ͳ�ȥ
		map.put("date", date);
		map.put("userName", userName);
		map.put("content", content);
		
		msg.obj = map;
		msg.what = 1;
		mHandler.sendMessage(msg);
	}
}
