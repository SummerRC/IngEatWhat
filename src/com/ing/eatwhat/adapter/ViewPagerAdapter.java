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

	//�����б�  
    private List<View> views;
    private Activity activity;
      
    public ViewPagerAdapter (Activity activity, List<View> views){  
    	this.activity = activity;
        this.views = views;  
    }  
 
    //��õ�ǰ������  
    @Override  
    public int getCount() {  
        if (views != null)  
        {  
            return views.size();  
        }            
        return 0;  
    }  
        
    //��ʼ��arg1λ�õĽ��� 
    @Override  
    public Object instantiateItem(View arg0, int arg1) {  
    	((ViewPager) arg0).addView(views.get(arg1), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);  
        if (arg1 == views.size() - 1) { 
        	 //ע�����һҳ��ť�ļ����¼�
            views.get(arg1).findViewWithTag("1").setOnClickListener(new OnClickListener() {	
    			@Override
    			public void onClick(View v) {
    				// �����Ѿ�����  
                    setGuided();  
                    goHome(); 
    			}
    		});
            //ע�����һҳ�ļ����¼�
        	views.get(arg1).setOnClickListener(new OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                    // �����Ѿ�����  
                    setGuided();  
                    goHome();   
                }    
            });  
        }   	          
        return views.get(arg1);  
    }  
  
    //����arg1λ�õĽ���  
    @Override  
    public void destroyItem(View arg0, int arg1, Object arg2) {  
        ((ViewPager) arg0).removeView(views.get(arg1));       
    } 
    
    //�ж��Ƿ��ɶ������ɽ���  
    @Override  
    public boolean isViewFromObject(View arg0, Object arg1) {  
        return (arg0 == arg1);  
    }  
  
    private void goHome() {  
        // ��ת  
        Intent intent = new Intent(activity, LoginActivity.class);  
        activity.startActivity(intent);  
        activity.finish();  
    }  
    
    //�����Ѿ��������ˣ��´����������ٴ�����  
    private void setGuided() {  
        SharedPreferences preferences = activity.getSharedPreferences("first", Context.MODE_PRIVATE);  
        Editor editor = preferences.edit();  
        // ��������  
        editor.putBoolean("isFirstIn", false);  
        // �ύ�޸�  
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
