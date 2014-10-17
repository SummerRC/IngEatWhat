/**
 * ����ָ���û��û��˵�����Ϣ����̨�ӿڷ��ص���һ��json
 */

package com.ing.eatwhat.thread;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.ing.eatwhat.entity.User;
import android.os.Handler;
import android.os.Message;

public class GetFoodMenuNetThread extends Thread{
	private Handler mHandler;
	
	public GetFoodMenuNetThread(Handler mHandler) {
		this.mHandler = mHandler;
	}
	
	public void run() {
		String url = "http://cqcreer.jd-app.com/food.php?username=" + User.userName;
		String result = "";
		
		HttpClient client = new DefaultHttpClient();	
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			if(response.getStatusLine().getStatusCode() == 200) {
				//ע�⣺�˴���ת��Ļ��������ַ������루�����˲���ת�뷽��������򵥿��У�
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
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
		
		Message msg = new Message();
		msg.what = 0;
		msg.obj = result;
		mHandler.sendMessage(msg);			
	}
	
	
}
