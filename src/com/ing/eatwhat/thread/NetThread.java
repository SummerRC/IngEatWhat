/*
 * NetThread�߳�����Ҫ���������ܣ�����һ�����ʾ����Ƶ����ݿ��ò����ķ���ֵ����������ֵͨ����Ϣ���ݸ�UI�߳�    msg.what = 0;
 *                         ���ܶ�������ţ�ƴ洢����ͼƬ��������ͨ����Ϣ���ݸ�UI�̴߳���   msg.what = 1;
 *                         ���������Ӿ����Ʒ���������ϴ�ƾ֤   msg.what = 2.
 *                         op:1 ��¼�� 2 ע�ᣬ 3 �Ӳˣ� 4 �޸Ĳˣ� 5 ����ϴ�ƾ֤�� 6 ��ͼƬ
 */
package com.ing.eatwhat.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import android.os.Handler;
import android.os.Message;

public class NetThread extends Thread {
	private Handler mHandler;
	private String str1;
	private String str2;
	private String str3;
	private String foodurl;
	private int op;             //�������ͣ�jingdong  qiqiu
	DefaultHttpClient client;
	String url;
	
	//��һ������Ϊ�û������ڶ�������Ϊ�������ʳ����������������Ϊʳ�����������ĸ�����Ϊuri(��ţ�ƴ洢�ϵ�����),�����Ϊ��������
	public NetThread(Handler mHandler, String str1, String str2, String str3, String str4, int op) {
		this.mHandler = mHandler;
		this.str1 = str1;
		this.str2 = str2;
		this.str3 = str3;
		foodurl = str4;
		this.op = op;
	}
	
	public void run() {
		
		client = new DefaultHttpClient();
	    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000); // ��������ʱʱ��
	    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 50000); // ��ȡ��ʱ

	    //������������
		DoNetWork();
	}
	
	private void DoNetWork() {

		String result;
		
		switch(op) {
		case 1:    //��¼
			url = "http://cqcreer.jd-app.com/login.php?username=" + str1 + "&password=" + str2;
		    break;
		case 2:    //ע��
			url = "http://cqcreer.jd-app.com/register.php?username=" + str1 + "&password=" + str2;
		    break;
		case 3:    //�Ӳ�
		    url = "http://cqcreer.jd-app.com/add_food.php?username=" + str1 + "&foodname=" + str2 + "&foodurl=" + foodurl;
			break;
		case 4:    //�޸Ĳ�
			url = "http://cqcreer.jd-app.com/change_food.php?username=" + str1 + "&foodname1=" + str2 + "&foodname2=" + str3 + "&foodurl=" + foodurl;
			break;
		case 5:    //ɾ����
			url = "http://cqcreer.jd-app.com/delete_food.php?username=" + str1 + "&foodname=" + str2;
			break;
		case 6:    //����ϴ�ƾ֤
			url = "http://cqcreer.jd-app.com/test_Uptoken.php";
			break;
		case 7:    //�����û���ʳ�����
			url = "http://cqcreer.jd-app.com/get_food_num.php?username=" + str1;
			break;
		}
		try {
			HttpGet get = new HttpGet(url);
			get.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			HttpResponse response;
			Message msg = new Message();
				
			response = client.execute(get);
			
			if(response.getStatusLine().getStatusCode() == 200) {
			
				if(op == 1 || op == 2 || op == 3 || op == 4 || op == 5) {
					BufferedReader bin = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				    result = bin.readLine();			//result�����״̬��Ϣ
				    
				    msg.what = 0;
				    msg.obj = result;
					mHandler.sendMessage(msg);		
				} else
					if(op == 7) {
						//result����Ƿ��ص�ʳ�����
					    result = EntityUtils.toString(response.getEntity(), "GBK");
					    
						msg.what = 1;
						msg.obj = result;
						mHandler.sendMessage(msg);
					} else
						if(op == 6) {
							BufferedReader bin = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
						    result = bin.readLine();   //result������ϴ�ƾ֤
						    
						    msg.what = 2;
						    msg.obj = result;
							mHandler.sendMessage(msg);	
						}
			}
			
		} catch (ClientProtocolException e) {			
			e.printStackTrace();			
		} catch (IOException e) {			
			e.printStackTrace();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Message msg = new Message();
			msg.what = 222;
		    mHandler.sendMessage(msg);				
		}
	}
	
}