package com.ing.eatwhat.activity;

import java.util.Date;

import com.baidu.mapapi.SDKInitializer;
import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.fragment.FoodMenuFragment;
import com.ing.eatwhat.fragment.ForumFragment;
import com.ing.eatwhat.fragment.MoreFragment;
import com.ing.eatwhat.fragment.RecommendFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.view.View;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
	
	private LinearLayout ll_home;				//主界面底部四个icon所在的LinearLayout
	private LinearLayout ll_recommend;
	private LinearLayout ll_forum;
	private LinearLayout ll_more;
	
	private ImageButton ib_home;				//主界面底部四个icon所在的ImageButton
	private ImageButton ib_recommend;
	private ImageButton ib_forum;
	private ImageButton ib_more;

	private static Drawable dw_home, dw_home_, dw_recommend, dw_recommend_,
							dw_forum, dw_forum_, dw_more, dw_more_;
							
	private FragmentManager fm;
	
	long preTime;  
	public static final long TWO_SECOND = 2 * 1000;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//全屏显示  无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_main);
		
		init();
		initFragment();
		
	}

	
	@Override
	public void onClick(View v) {
		FragmentTransaction ft = fm.beginTransaction();
		int tag = (Integer)v.getTag();
		
		switch(tag) {
		case 1:		
			if(fm.findFragmentByTag("FoodMenuFragment") != null && fm.findFragmentByTag("FoodMenuFragment").isVisible()) {
				return;
			}	
			setPicture(dw_home_, dw_recommend, dw_forum, dw_more);
			ft.replace(R.id.frag_Container, fm.findFragmentByTag("FoodMenuFragment"));
			ft.commit();
			break;
		case 2:
			if(fm.findFragmentByTag("RecommendFragment") != null) {
				return;
			}
			setPicture(dw_home, dw_recommend_, dw_forum, dw_more);
			RecommendFragment recommendFragment = new RecommendFragment();
			ft.replace(R.id.frag_Container, recommendFragment, "RecommendFragment");
			ft.commit();
			break;
		case 3:		
			if(fm.findFragmentByTag("ForumFragment") != null) {
				return;
			}
			setPicture(dw_home, dw_recommend, dw_forum_, dw_more);			
			ForumFragment forumFragment = new ForumFragment();
			ft.replace(R.id.frag_Container, forumFragment, "ForumFragment");	
			ft.commit();
			break;
		case 4:
			if(fm.findFragmentByTag("MoreFragment") != null) {
				return;
			}
			setPicture(dw_home, dw_recommend, dw_forum, dw_more_);
			MoreFragment moreFragment = new MoreFragment();
			ft.replace(R.id.frag_Container, moreFragment, "MoreFragment");
			ft.commit();
			break;
		
		}	
	}

	//更换icon的背景图片
	@SuppressWarnings("deprecation")
	private void setPicture(Drawable home, Drawable information,  Drawable forum, Drawable more) {
		ib_home.setBackgroundDrawable(home);		
		ib_recommend.setBackgroundDrawable(information);
		ib_forum.setBackgroundDrawable(forum);
		ib_more.setBackgroundDrawable(more);	
	}
	
	//初始化默认的Fragment
	private void initFragment() {
		fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		FoodMenuFragment foodMenuFragment = new FoodMenuFragment();
		ft.add(R.id.frag_Container, foodMenuFragment, "FoodMenuFragment");
		ft.addToBackStack(null);
		ft.commit();
	}
	
	//初始化
	private void init() {
		ll_home = (LinearLayout)this.findViewById(R.id.ll_home);
		ll_recommend = (LinearLayout)this.findViewById(R.id.ll_recommend);
		ll_forum = (LinearLayout)this.findViewById(R.id.ll_forum);
		ll_more = (LinearLayout)this.findViewById(R.id.ll_more);
		ib_home = (ImageButton)this.findViewById(R.id.ib_home);
		ib_recommend = (ImageButton)this.findViewById(R.id.ib_recommend);
		ib_forum = (ImageButton)this.findViewById(R.id.ib_forum);
		ib_more =  (ImageButton)this.findViewById(R.id.ib_more);
		
		ll_home.setOnClickListener(this);
		ll_recommend.setOnClickListener(this);
		ll_forum.setOnClickListener(this);
		ll_more.setOnClickListener(this);
		ib_home.setOnClickListener(this);
		ib_recommend.setOnClickListener(this);
		ib_forum.setOnClickListener(this);
		ib_more.setOnClickListener(this);
		
		ll_home.setTag(1);
		ib_home.setTag(1);
		ll_recommend.setTag(2);
		ib_recommend.setTag(2);
		ll_forum.setTag(3);
		ib_forum.setTag(3);
		ll_more.setTag(4);
		ib_more.setTag(4);		
		
		dw_home = getBaseContext().getResources().getDrawable(R.drawable.home);
		dw_home_ = getBaseContext().getResources().getDrawable(R.drawable.home_);
		dw_recommend = getBaseContext().getResources().getDrawable(R.drawable.recommend);
		dw_recommend_ = getBaseContext().getResources().getDrawable(R.drawable.recommend_);
		dw_forum = getBaseContext().getResources().getDrawable(R.drawable.forum);
		dw_forum_ = getBaseContext().getResources().getDrawable(R.drawable.forum_);
		dw_more = getBaseContext().getResources().getDrawable(R.drawable.more);
		dw_more_ = getBaseContext().getResources().getDrawable(R.drawable.more_);
	}
	
	//两秒之内点击两次后退键退出程序
	@Override
	public void onBackPressed() {
		long currentTime = new Date().getTime();  
		  
        // 如果时间间隔小于于2秒, 退出程序  
        if ((currentTime - preTime) > TWO_SECOND) {  
            AllUse.info(MainActivity.this, "再按一次后退键退出哎呀呀");
            // 更新时间  
            preTime = currentTime;
        } else {
        	MainActivity.this.finish();
        }	         
	}
	
}

