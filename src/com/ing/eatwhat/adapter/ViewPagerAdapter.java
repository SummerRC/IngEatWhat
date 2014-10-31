package com.ing.eatwhat.adapter;

import java.util.List;
import com.ing.eatwhat.activity.LoginActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.app.Activity;

public class ViewPagerAdapter extends PagerAdapter{

	//界面列表  
    private List<View> views;
    private Activity activity;
      
    public ViewPagerAdapter (Activity activity, List<View> views){  
    	this.activity = activity;
        this.views = views;  
    }  
 
    //获得当前界面数  
    @Override  
    public int getCount() {  
        if (views != null)  
        {  
            return views.size();  
        }            
        return 0;  
    }  
        
    //初始化arg1位置的界面 
    @Override  
    public Object instantiateItem(View arg0, int arg1) {  
    	((ViewPager) arg0).addView(views.get(arg1), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);  
        if (arg1 == views.size() - 1) { 
        	 //注册最后一页按钮的监听事件
            views.get(arg1).findViewWithTag("1").setOnClickListener(new OnClickListener() {	
    			@Override
    			public void onClick(View v) {
    				// 设置已经引导  
                    setGuided();  
                    goHome(); 
    			}
    		});
            //注册最后一页的监听事件
        	views.get(arg1).setOnClickListener(new OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                    // 设置已经引导  
                    setGuided();  
                    goHome();   
                }    
            });  
        }   	          
        return views.get(arg1);  
    }  
  
    //销毁arg1位置的界面  
    @Override  
    public void destroyItem(View arg0, int arg1, Object arg2) {  
        ((ViewPager) arg0).removeView(views.get(arg1));       
    } 
    
    //判断是否由对象生成界面  
    @Override  
    public boolean isViewFromObject(View arg0, Object arg1) {  
        return (arg0 == arg1);  
    }  
  
    private void goHome() {  
        // 跳转  
        Intent intent = new Intent(activity, LoginActivity.class);  
        activity.startActivity(intent);  
        activity.finish();  
    }  
    
    //设置已经引导过了，下次启动不用再次引导  
    private void setGuided() {  
        SharedPreferences preferences = activity.getSharedPreferences("first", Context.MODE_PRIVATE);  
        Editor editor = preferences.edit();  
        // 存入数据  
        editor.putBoolean("isFirstIn", false);  
        // 提交修改  
        editor.commit();  
    }  
    
    @Override  
    public void restoreState(Parcelable arg0, ClassLoader arg1) {  
        
    }  
  
    @Override  
    public Parcelable saveState() {  
        return null;  
    }  
  
    @Override  
    public void startUpdate(View arg0) {  
         
    }  
    
    @Override  
    public void finishUpdate(View arg0) {  
        
    } 
}
