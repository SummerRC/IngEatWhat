 package com.ing.eatwhat.entity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	public static void info(Context ctx, String str) {
		Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
	}
			
	//�ж��Ƿ�����
	public static boolean isHaveInternet(Context ctx) {  
        try {      	
            ConnectivityManager manger = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);  
            NetworkInfo info = manger.getActiveNetworkInfo();  
            return (info != null && info.isConnected());  
        } catch(Exception e) {  	
            return false;  
        }
     }  
	
	//��SharedPreference�����û���¼��Ϣ�͵�¼״̬,˽�����ݣ�����app���ܷ���
	public static void saveLoginStatus(Context ctx, String userName, String userPassword, boolean haveLogined) {
		
		SharedPreferences sp = ctx.getSharedPreferences("ingEatwhat", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		
		editor.putString("userName", userName);           
		editor.putBoolean("haveLogined", haveLogined);
		editor.putInt("food_num", 0);     
		
		editor.commit();
	}
	
	//��ô�����û���¼��Ϣ,�粻���ڷ���null
    public static String getSharedPreferencesContent(Context ctx, String content) {
	    SharedPreferences sp = ctx.getSharedPreferences("ingEatwhat", Context.MODE_PRIVATE);
		
	    return sp.getString(content, "null");
    }
    
	//�༭�û���¼��Ϣ�͵�¼״̬
    public static void editLoginStatus(Context ctx, boolean haveLogined) {
    	    SharedPreferences sp = ctx.getSharedPreferences("ingEatwhat", Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			
			editor.putBoolean("haveLogined", haveLogined);
			
			editor.commit();
    }
    
    //��ô�����û�food��Ŀ��Ϣ,��������ڷ���-1
    public static int getFoodNum(Context ctx) {
	    SharedPreferences sp = ctx.getSharedPreferences("ingEatwhat", Context.MODE_PRIVATE);
		return sp.getInt("food_num", -1);
    }
    
    //�����û���food_num
    public static void setFood_num(Context ctx, int num) {
    	    SharedPreferences sp = ctx.getSharedPreferences("ingEatwhat", Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			
			editor.putInt("food_num", num);      
			
			editor.commit();
    }
       
    //�༭�û�food��Ŀ��Ϣ(ɾ���˵���һ����ʱ-1������ʱ+1��
    public static void editFoodNum(Context ctx, int op) {
    	    SharedPreferences sp = ctx.getSharedPreferences("ingEatwhat", Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			
			int num = getFoodNum(ctx) + op;
			editor.putInt("food_num", num);
			
			editor.commit();
    }
    
    //�ж��Ƿ��½��,ע����Ҫ��������������
    public static boolean islogined(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("ingEatwhat", Context.MODE_PRIVATE);
		
		//ȡ�õ�¼״̬����"haveLogined"�����ھͷ���false
		boolean haveLogined = sp.getBoolean("haveLogined", false);
		if(haveLogined == true) {
			User.userName = sp.getString("userName", "");
			User.userPassword = sp.getString("userPassword", "");
			
			return true;
		} 
			
		return false;
		
	}
	
    
    //����ͼƬ
	public static void savePicture(Bitmap bitmap, String picPath) {
		 FileOutputStream fos = null;
		 try {
		       fos = new FileOutputStream(picPath);
	           bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);// ������д���ļ�
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
	
}
