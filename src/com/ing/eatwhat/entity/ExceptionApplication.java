package com.ing.eatwhat.entity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;

public class ExceptionApplication extends Application {

	private static ArrayList<Activity> list = new ArrayList<Activity>();  
	
	@Override  
    public void onCreate() {  
        super.onCreate();  
        MyUncaughtExceptionHandler exceptionHandler = MyUncaughtExceptionHandler.getInstance();  
        exceptionHandler.init(getApplicationContext());  
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
    
}
