 package com.ing.eatwhat.entity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class AllUse {

	public static int real_foodnum = 0;

	//Toast
	public static void info(Application ctx, String str) {
		Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
	}
			
	//判断是否联网
	public static boolean isHaveInternet(Context ctx) {  
        try {      	
            ConnectivityManager manger = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);  
            NetworkInfo info = manger.getActiveNetworkInfo();  
            return (info != null && info.isConnected());  
        } catch(Exception e) {  	
            return false;  
        }
     }  
	
	//用SharedPreference储存用户登录信息和登录状态,私有数据，其他app不能访问
	public static void saveLoginStatus(Context ctx, String userName, String userPassword, int food_num) {
		
		SharedPreferences sp = ctx.getSharedPreferences(User.userName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		
		editor.putString("userName", userName);    
		editor.putString("userPassword", userPassword); 
		editor.putInt("food_num", food_num);     
		
		editor.commit();
	}
	
	//用SharedPreference储存用户手机的IMEI，其他app不能访问
	public static void saveIMEI(Context ctx, String IMEI) {		
		SharedPreferences sp = ctx.getSharedPreferences(User.userName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		
		editor.putString("IMEI", IMEI);    
		editor.commit();
	}
	
	//获得储存的用户登录信息,如不存在返回null
    public static String getSharedPreferencesContent(Context ctx, String content) {
	    SharedPreferences sp = ctx.getSharedPreferences(User.userName, Context.MODE_PRIVATE);
		
	    return sp.getString(content, "null");
    }
    
	//编辑用户登录信息和登录状态
    public static void editLoginStatus(Context ctx) {
    	SharedPreferences sp = ctx.getSharedPreferences(User.userName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		
		editor.putString("userName", "null");    
		editor.putString("userPassword", ""); 
		
		editor.commit();
    }
    
    //获得储存的用户food数目信息,如果不存在返回-1
    public static int getFoodNum(Context ctx) {
	    SharedPreferences sp = ctx.getSharedPreferences(User.userName, Context.MODE_PRIVATE);
		return sp.getInt("food_num", 0);
    }
    
    //设置用户的food_num
    public static void setFood_num(Context ctx, int num) {
    	    SharedPreferences sp = ctx.getSharedPreferences(User.userName, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			
			editor.putInt("food_num", num);      		
			editor.commit();
    }
       
    //编辑用户food数目信息(删除菜单中一个菜时-1，增加时+1）
    public static void editFoodNum(Context ctx, int op) {
    	    SharedPreferences sp = ctx.getSharedPreferences(User.userName, Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			
			int num = getFoodNum(ctx) + op;
			editor.putInt("food_num", num);
			
			editor.commit();
    }
    
    //判断是否登陆过,注意需要给他传递上下文
    public static boolean islogined(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(User.userName, Context.MODE_PRIVATE);
		
		//取得登录状态，若"haveLogined"不存在就返回false
		boolean haveLogined = sp.getBoolean("haveLogined", false);
		if(haveLogined == true) {
			User.userName = sp.getString("userName", "");
			User.userPassword = sp.getString("userPassword", "");
			
			return true;
		} 
		return false;
	}
	
    
    //储存图片
	public static void savePicture(Bitmap bitmap, String picPath) {
		 FileOutputStream fos = null;
		 try {
		       fos = new FileOutputStream(picPath);
	           bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 把数据写入文件
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                fos.flush();
	                fos.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }   
	}
	
	//防止按钮短时间内被多次点击
	public static int click_limit(long preTime) {
		long currentTime = new Date().getTime(); 
		  
        // 如果时间间隔小于于2秒, 退出程序  
        if ((currentTime - preTime) < 1*1000) {  
        	 preTime = currentTime;
        	 return -1;    
        } else {
        	 preTime = currentTime;
        	 return 0;
        }
	}
	
	public static int strLimit(String str) {
		String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; 
	    Pattern p = Pattern.compile(regEx); 
	    Matcher m = p.matcher(str);                 
	    if( m.find()){
	        return -1;
	    } else {
	    	return 0;
	    }
	}
	
}
