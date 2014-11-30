package com.ing.eatwhat.entity;

import java.util.ArrayList;
import com.ing.eatwhat.thread.NetThread;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class ExceptionApplication extends Application {

	private static ArrayList<Activity> list = new ArrayList<Activity>();  
	private final static String sdPATH = Environment .getExternalStorageDirectory().getAbsolutePath();
	
	public static int localVersion = 0;// 本地安装版本

	public int serverVersion = 0;// 服务器版本
	public static String downloadUrl = null;
	public static String downloadDir = sdPATH + "/ingZone/eatWhat/update/";// 安装目录
	@Override  
    public void onCreate() {  
        super.onCreate();  
        MyUncaughtExceptionHandler exceptionHandler = MyUncaughtExceptionHandler.getInstance();  
        exceptionHandler.init(getApplicationContext());
        getVersions();
    }  

     // 向Activity列表中添加Activity对象
    public static void addActivity(Activity activity){  
        list.add(activity);  
    }  
      
     // Activity关闭时，删除Activity列表中的Activity对象
    public static void removeActivity(Activity activity){  
        list.remove(activity);  
    }  
    
     // 关闭Activity列表中的所有Activity
    public static void finishActivity(){  
        for (Activity activity : list) {    
            if (null != activity) {    
                activity.finish();    
            }    
        }  
        //杀死该应用进程(这货一般只能结束当前Activity和前一个或两个Activity)
       android.os.Process.killProcess(android.os.Process.myPid());    
    }  
    public void getVersions(){
    	try {
			PackageInfo packageInfo = getApplicationContext()
					.getPackageManager().getPackageInfo(getPackageName(), 0);
			localVersion = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					// 获取线程返回的结果
					String result = msg.obj.toString().trim();
					JSONUtility ju = new JSONUtility(result);
					serverVersion = Integer.parseInt(ju.version);
					downloadUrl = ju.url;
				}
			}
		};
		NetThread thread = new NetThread(mHandler, null, null, null, null, 9);
		thread.start();
    }
}
