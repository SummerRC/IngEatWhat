/**
 * 下载用户菜单中食物图片的线程，并将用户的菜单信息插入到本地数据库
 */

package com.ing.eatwhat.thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import com.ing.eatwhat.database.DBManager;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.Food;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class DownlaodPicNetThread extends Thread{
	private Handler mHandler;
	private String foodUri;			//七牛云存储的地址的一部分：用户名+时间
	private String foodName;		//网上用户的食物名
	private Bitmap bitmap;
	private final static String SDpath = Environment .getExternalStorageDirectory().getAbsolutePath();
	Context context;
	
	public DownlaodPicNetThread(Context context, Handler mHandler, String foodName, String foodUri) {
		this.context = context;
		this.mHandler= mHandler;		
		this.foodName = foodName;
		this.foodUri = foodUri;
	}
	
	public void run() {
		try	{
			URL url = new URL("http://eatwhat.qiniudn.com/" + foodUri);
           
			//打开url对应资源的输入流
			InputStream is = url.openStream();
           
			//从输入流中解析出图片
			bitmap = BitmapFactory.decodeStream(is);
			
			//关闭流释放资源
			is.close();
			
			//创建存放图片的文件目录
			mkdir();
			
			//存储图片
	        String picPath = SDpath + "/ingZone/eatWhat/" + foodUri + ".jpg"; 
	        SavePicture(bitmap, picPath);
	        
	        //插入数据库
	        insertDB(picPath);
   
	        //线程完成任务后，想UI线程发送消息，通知UI线程更新UI组件
	        Message msg = new Message();
	        msg.what = 1;
			mHandler.sendMessage(msg);
		} catch(Exception e) {
			e.getMessage();
		}
	}
	
	//创建文件夹
	public static void mkdir() {
       File file = new File(SDpath +"/ingZone/eatWhat/");
        
       //判断文件目录是否存在，不存在就创建
        if(!file.exists()){  
            file.mkdir();  
        }       
	}
	
	//保存图片到指定位置
	public static void SavePicture(Bitmap bitmap, String picPath) {
		FileOutputStream fos = null;
		try {
			//将图片写入对应文件
			fos = new FileOutputStream(picPath);
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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

	//将food信息插入数据库
	private void insertDB(String picPath) {
		DBManager dbManager = new DBManager(context);
		Food food = new Food();
		food.setId(AllUse.getFoodNum(context));
		food.setName(foodName);
		food.setPicPath(picPath);
		dbManager.insertFood(food);
	}
}
