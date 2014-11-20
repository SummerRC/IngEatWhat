package com.ing.eatwhat.entity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;


public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{
	
	private final String sdPATH = Environment .getExternalStorageDirectory().getAbsolutePath();
	private Thread.UncaughtExceptionHandler mDefaultHandler;  					//系统默认的UncaughtException处理类  
    private static MyUncaughtExceptionHandler INSTANCE = new MyUncaughtExceptionHandler();        //CrashHandler实例  
    private Context mContext;  
    private Map<String, String> infos = new HashMap<String, String>();  		 //用来存储设备信息和异常信息  
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  							//用于格式化日期,作为日志文件名的一部分 
  
    /** 
     * 保证只有一个CrashHandler实例 
     * */  
    private MyUncaughtExceptionHandler() {  
    }  
  
    /**
     *  获取MyUncaughtExceptionHandler实例 ,单例模式 
     *  */  
    public static MyUncaughtExceptionHandler getInstance() {  
        return INSTANCE;  
    }  
  
    /** 
     * 初始化 
     */  
    public void init(Context mContext) {  
        this.mContext = mContext;    
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  	//获取系统默认的UncaughtException处理器   
        Thread.setDefaultUncaughtExceptionHandler(this);  								//设置该CrashHandler为程序的默认处理器  
    }  
  
    /** 
     * 当UncaughtException发生时会转入该函数来处理 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        if (!handleException(ex) && mDefaultHandler != null) {  							//如果用户没有处理则让系统默认的异常处理器来处理  
        	mDefaultHandler.uncaughtException(thread, ex);  		
        } else {  
            try {  
                Thread.sleep(3000);  
            } catch (InterruptedException e) {  
            }  
            //退出程序  
           ExceptionApplication.finishActivity();
        }  
    }  
  
    /** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 
     * return true:如果处理了该异常信息;否则返回false. 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  
        //使用Toast来显示异常信息  
        new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Toast.makeText(mContext, "亲，你对哎呀做了神马？哎呀出现异常，即将退出~~.", Toast.LENGTH_LONG).show();  
                Looper.loop();  
            }  
        }.start();  
        //收集设备参数信息   
        collectDeviceInfo(mContext);  
        //保存日志文件   
        saveCrashInfo2File(ex);  
        return true;  
    }  
      
    /** 
     * 收集设备参数信息 
     * @param ctx 
     */  
    public void collectDeviceInfo(Context ctx) {  
        try {  
            PackageManager pm = ctx.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);  
            if (pi != null) {  
                String versionName = pi.versionName == null ? "null" : pi.versionName;  
                String versionCode = pi.versionCode + "";  
                infos.put("versionName", versionName);  
                infos.put("versionCode", versionCode);  
            }  
        } catch (NameNotFoundException e) {  

        }  
        Field[] fields = Build.class.getDeclaredFields();  
        for (Field field : fields) {  
            try {  
                field.setAccessible(true);  
                infos.put(field.getName(), field.get(null).toString());  
            } catch (Exception e) {  
            	e.printStackTrace(); 
            }  
        }  
    }  
  
    /** 
     * 保存错误信息到文件中 
     *  返回文件名称,用于以后将文件传送到服务器 
     */  
    private String saveCrashInfo2File(Throwable ex) {  
          
        StringBuffer sb = new StringBuffer();  
        for (Map.Entry<String, String> entry : infos.entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key + "=" + value + "\n");  
        }  
          
        Writer writer = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(writer);  
        ex.printStackTrace(printWriter);  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
        printWriter.close();  
        String result = writer.toString();  
        sb.append(result);  
        try {  
            long timestamp = System.currentTimeMillis();  
            String time = formatter.format(new Date());  
            String fileName = "crash-" + time + "-" + timestamp + ".log";  
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
                String path = sdPATH + "/ingZone/eatWhat/";
                File dir = new File(path);  
                if (!dir.exists()) {  
                    dir.mkdirs();  
                }  
                FileOutputStream fos = new FileOutputStream(path + fileName);  
                fos.write(sb.toString().getBytes());  
                fos.close();  
            }  
            return fileName;  
        } catch (Exception e) {  
            e.printStackTrace();
        }  
        return null;  
    }  
}  

