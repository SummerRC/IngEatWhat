package com.ing.eatwhat.entity;

import android.app.Application;

public class ExceptionApplication extends Application{

	@Override  
    public void onCreate() {  
        super.onCreate();  
        MyUncaughtExceptionHandler exceptionHandler = MyUncaughtExceptionHandler.getInstance();  
        exceptionHandler.init(getApplicationContext());  
    }  

}
