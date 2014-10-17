package com.ing.eatwhat.activity;

import java.util.ArrayList;
import java.util.List;
import com.ing.eatwhat.R;
import com.ing.eatwhat.adapter.ViewPagerAdapter;
import android.app.Activity;
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
    
    //����ͼƬ��Դ
    private static final int[] pictures = {R.drawable.guide_pic1, R.drawable.guide_pic2, R.drawable.guide_pic3, R.drawable.guide_pic4}; 
    // �ײ�С��ͼƬ  
    private ImageView[] dots;  
    // ��¼��ǰѡ��λ��  
    private int currentIndex; 
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState); 
        //��ȥ��������Ӧ�ó�������֣�  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //��ȥ״̬������(��ص�ͼ���һ�����β���)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        setContentView(R.layout.activity_guide);  
        
        //��ʼ��ҳ��
        initViews();
        
        // ��ʼ���ײ�С��  
        initDots();  
   }  
  
    //��ʼ��ҳ��
    private void initViews() {  
    	views = new ArrayList<View>();         
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);            
        //��ʼ������ͼƬ�б�  
        for(int i=0; i<pictures.length; i++) {  
        	if(i != (pictures.length-1)) {
	    		LinearLayout ll = new LinearLayout(this);  
	            ll.setLayoutParams(mParams);  
	            ll.setBackgroundResource(pictures[i]);  
	            views.add(ll);
        	} else {
        		views.add(this.getLayoutInflater().inflate(R.layout.ll, null));
        	}
             
        }        
        vp = (ViewPager)findViewById(R.id.viewpager);  
        //��ʼ��Adapter  
        vpAdapter = new ViewPagerAdapter(this, views);  
        vp.setAdapter(vpAdapter);  
        //�󶨻ص�  
        vp.setOnPageChangeListener(this); 
    }
  
    //��ʼ���ײ�С��
    private void initDots() {  
    	 LinearLayout ll = (LinearLayout) findViewById(R.id.ll);      	  
         dots = new ImageView[pictures.length];     
         //ѭ��ȡ��С��ͼƬ  
         for (int i = 0; i < pictures.length; i++) {  
             dots[i] = (ImageView) ll.getChildAt(i);  
             dots[i].setEnabled(true);				//����Ϊ��ɫ  
             dots[i].setOnClickListener(this);  
             dots[i].setTag(i);						//����λ��tag������ȡ���뵱ǰλ�ö�Ӧ  
         }  
         currentIndex = 0;  						//��ǰ����
         dots[currentIndex].setEnabled(false);		//����Ϊ��ɫ����ѡ��״̬   
    }  
  
    //���õ�ǰ������ҳ
    private void setCurrentView(int position)  
    {  
        if (position < 0 || position >= pictures.length) {  
            return;  
        }  
        
        vp.setCurrentItem(position);  
    }  
    
    //���õ�ǰ������С���ѡ��
    private void setCurrentDot(int position) {  
        if (position < 0 || position > views.size() - 1  
                || currentIndex == position) {  
            return;  
        }   
        dots[position].setEnabled(false);  			//��ǰ�������ҳ���Ӧ��dot����Ϊѡ��״̬�����ɵ��
        dots[currentIndex].setEnabled(true);    	//֮ǰ�ı�ѡ��ҳ���Ӧ��dot����Ϊδѡ��״̬���ɵ��
        currentIndex = position;  					//��ǰ����
    }  
  
    // ������״̬�ı�ʱ����  
    @Override  
    public void onPageScrollStateChanged(int arg0) {  
    }  
  
    // ����ǰҳ�汻����ʱ����  
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
    }  
  
    // ���µ�ҳ�汻ѡ��ʱ����  
    @Override  
    public void onPageSelected(int arg0) {  
        // ���õײ�С��ѡ��״̬  
        setCurrentDot(arg0);  
    }

    //�����¼��Ļص�����
	@Override
	public void onClick(View view) {
		int position = (Integer)view.getTag();  
        setCurrentView(position);  
        setCurrentDot(position); 
	}  
}
