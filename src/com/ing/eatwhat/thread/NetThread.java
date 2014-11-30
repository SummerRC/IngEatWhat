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
	private int op;             //操作类型：jingdong  qiqiu
	private final String STR = "http://ingeatwhat.sinaapp.com/";
	DefaultHttpClient client;
	String url;
	
	//第一个参数为用户名，第二个参数为密码或者食物名，第三个参数为食物新名，第四个参数为uri(七牛云存储上的名字),第五个为操作类型
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
	    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000); // 设置请求超时时间
	    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 50000); // 读取超时

	    //处理网络任务
		DoNetWork();
	}
	
	private void DoNetWork() {

		String result;
		
		switch(op) {
		case 1:    //登录
			url =  STR + "login.php?username=" + str1 + "&password=" + str2;
		    break;
		case 2:    //注册
			url =  STR + "register.php?username=" + str1 + "&password=" + str2;
		    break;
		case 3:    //加菜
		    url =  STR + "add_food.php?username=" + str1 + "&foodname=" + str2 + "&foodurl=" + foodurl;
			break;
		case 4:    //修改菜
			url =  STR + "change_food.php?username=" + str1 + "&foodname1=" + str2 + "&foodname2=" + str3 + "&foodurl=" + foodurl;
			break;
		case 5:    //删除菜
			url =  STR + "delete_food.php?username=" + str1 + "&foodname=" + str2;
			break;
		case 6:    //获得上传凭证
			url =  STR + "test_Uptoken.php";
			break;
		case 7:    //返回用户的食物个数
			url =  STR + "get_food_num.php?username=" + str1;
			break;
		case 8:	   //修改密码
			url =  STR + "change_password.php?username=" + str1 + "&password=" + str2 + "&new_pw=" + str3;
			break;
		case 9:	   //获得最新版本信息
			url =  "http://1.ingeatwhat.sinaapp.com/check_version.php";
			break;
		}
		try {
			HttpGet get = new HttpGet(url);
			get.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
			HttpResponse response;
			Message msg = new Message();
				
			response = client.execute(get);
			
			if(response.getStatusLine().getStatusCode() == 200) {
			
				if(op == 1 || op == 2 || op == 3 || op == 4 || op == 5 || op == 8 || op == 9) {
					BufferedReader bin = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				    result = bin.readLine();			//result存的是状态信息
				    
				    msg.what = 0;
				    msg.obj = result;
					mHandler.sendMessage(msg);		
				} else
					if(op == 7) {
						//result存的是返回的食物个数
					    result = EntityUtils.toString(response.getEntity(), "GBK");
					    
						msg.what = 1;
						msg.obj = result;
						mHandler.sendMessage(msg);
					} else
						if(op == 6) {
							BufferedReader bin = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
						    result = bin.readLine();   //result存的是上传凭证
						    
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