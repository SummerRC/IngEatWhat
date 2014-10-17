/**
 * �����û��˵���ʳ��ͼƬ���̣߳������û��Ĳ˵���Ϣ���뵽�������ݿ�
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
	private String foodUri;			//��ţ�ƴ洢�ĵ�ַ��һ���֣��û���+ʱ��
	private String foodName;		//�����û���ʳ����
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
           
			//��url��Ӧ��Դ��������
			InputStream is = url.openStream();
           
			//���������н�����ͼƬ
			bitmap = BitmapFactory.decodeStream(is);
			
			//�ر����ͷ���Դ
			is.close();
			
			//�������ͼƬ���ļ�Ŀ¼
			mkdir();
			
			//�洢ͼƬ
	        String picPath = SDpath + "/ingZone/eatWhat/" + foodUri + ".jpg"; 
	        SavePicture(bitmap, picPath);
	        
	        //�������ݿ�
	        insertDB(picPath);
   
	        //�߳�����������UI�̷߳�����Ϣ��֪ͨUI�̸߳���UI���
	        Message msg = new Message();
	        msg.what = 1;
			mHandler.sendMessage(msg);
		} catch(Exception e) {
			e.getMessage();
		}
	}
	
	//�����ļ���
	public static void mkdir() {
       File file = new File(SDpath +"/ingZone/eatWhat/");
        
       //�ж��ļ�Ŀ¼�Ƿ���ڣ������ھʹ���
        if(!file.exists()){  
            file.mkdir();  
        }       
	}
	
	//����ͼƬ��ָ��λ��
	public static void SavePicture(Bitmap bitmap, String picPath) {
		FileOutputStream fos = null;
		try {
			//��ͼƬд���Ӧ�ļ�
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

	//��food��Ϣ�������ݿ�
	private void insertDB(String picPath) {
		DBManager dbManager = new DBManager(context);
		Food food = new Food();
		food.setId(AllUse.getFoodNum(context));
		food.setName(foodName);
		food.setPicPath(picPath);
		dbManager.insertFood(food);
	}
}
