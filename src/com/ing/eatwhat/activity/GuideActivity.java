package com.ing.eatwhat.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.ing.eatwhat.R;
import com.ing.eatwhat.adapter.ViewPagerAdapter;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity implements OnPageChangeListener, OnClickListener{
	
	private ViewPager vp;  
    private ViewPagerAdapter vpAdapter;  
    private List<View> views;
    
    //引导图片资源
    private static final int[] pictures = {R.drawable.guide_pic1, R.drawable.guide_pic2, R.drawable.guide_pic3}; 
    // 底部小点图片  
    private ImageView[] dots;  
    // 记录当前选中位置  
    private int currentIndex; 
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState); 
        //隐去标题栏（应用程序的名字）  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐去状态栏部分(电池等图标和一切修饰部分)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        setContentView(R.layout.activity_guide);  
        
        //初始化页面
        initViews();
        
        // 初始化底部小点  
        initDots();  
   }  
  
    //初始化页面
    private void initViews() {  
    	views = new ArrayList<View>();         
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);            
        //初始化引导图片列表  
        for(int i=0; i<pictures.length; i++) {  
        	if(i != (pictures.length-1)) {
	    		LinearLayout ll = new LinearLayout(this);  
	            ll.setLayoutParams(mParams);  
	            InputStream in = this.getResources().openRawResource(pictures[i]);
	            Bitmap bitmap = BitmapFactory.decodeStream(in);								//decodeStream解析更省内存
	            BitmapDrawable drawable = new BitmapDrawable(bitmap);
	            ll.setBackgroundDrawable(drawable);
	            views.add(ll);
        	} else {
        		views.add(this.getLayoutInflater().inflate(R.layout.ll, null));
        	}
             
        }        
        vp = (ViewPager)findViewById(R.id.viewpager);  
        //初始化Adapter  
        vpAdapter = new ViewPagerAdapter(this, views);  
        vp.setAdapter(vpAdapter);  
        //绑定回调  
        vp.setOnPageChangeListener(this); 
    }
  
    //初始化底部小点
    private void initDots() {  
    	 LinearLayout ll = (LinearLayout) findViewById(R.id.ll);      	  
         dots = new ImageView[pictures.length];     
         //循环取得小点图片  
         for (int i = 0; i < pictures.length; i++) {  
             dots[i] = (ImageView) ll.getChildAt(i);  
             dots[i].setEnabled(true);				//都设为灰色  
             dots[i].setOnClickListener(this);  
             dots[i].setTag(i);						//设置位置tag，方便取出与当前位置对应  
         }  
         currentIndex = 0;  						//当前索引
         dots[currentIndex].setEnabled(false);		//设置为白色，即选中状态   
    }  
  
    //设置当前的引导页
    private void setCurrentView(int position)  
    {  
        if (position < 0 || position >= pictures.length) {  
            return;  
        }  
        
        vp.setCurrentItem(position);  
    }  
    
    //设置当前的引导小点的选中
    private void setCurrentDot(int position) {  
        if (position < 0 || position > views.size() - 1  
                || currentIndex == position) {  
            return;  
        }   
        dots[position].setEnabled(false);  			//当前被点击的页面对应的dot设置为选中状态，不可点击
        dots[currentIndex].setEnabled(true);    	//之前的被选中页面对应的dot设置为未选中状态，可点击
        currentIndex = position;  					//当前索引
    }  
  
    // 当滑动状态改变时调用  
    @Override  
    public void onPageScrollStateChanged(int arg0) {  
    }  
  
    // 当当前页面被滑动时调用  
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
    }  
  
    // 当新的页面被选中时调用  
    @Override  
    public void onPageSelected(int arg0) {  
        // 设置底部小点选中状态  
        setCurrentDot(arg0);  
    }

    //监听事件的回调函数
	@Override
	public void onClick(View view) {
		int position = (Integer)view.getTag();  
        setCurrentView(position);  
        setCurrentDot(position); 
	}  
}
