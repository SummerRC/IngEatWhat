package com.ing.eatwhat.activity;

import java.util.Date;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.ExceptionApplication;
import com.ing.eatwhat.entity.User;
import com.ing.eatwhat.fragment.MoreFragment;
import com.ing.eatwhat.fragment.RecommendFragment;
import com.ing.eatwhat.gridview.FoodMenuFragment;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
	
	private LinearLayout ll_home;				//主界面底部四个icon所在的LinearLayout
	private LinearLayout ll_yaoyiyao;
	private LinearLayout ll_more;
	
	private ImageButton ib_home;				//主界面底部四个icon所在的ImageButton
	private ImageButton ib_yaoyiyao;
	private ImageButton ib_more;

	private static Drawable dw_home, dw_home_, dw_yaoyiyao, dw_yaoyiyao_, dw_more, dw_more_;
							
	private FragmentManager fm;
	
	private static final int IMEI1 = Menu.FIRST ;
	private static final int IMEI2 = Menu.FIRST + 1;
	private static final int IMEI3 = Menu.FIRST + 2;
	
	long preTime = 0;  
	public static final long TWO_SECOND = 2 * 1000;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//全屏显示  无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_main);
		
		ExceptionApplication.addActivity(this);
		
		//友盟自动反馈提示
		//开发者回复用户反馈后，自动提醒用户
		FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
		agent.sync();
		
		//友盟自动更新
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		
		//注册上下文菜单
		registerForContextMenu(this.findViewById(R.id.container));
		
		init();
		initFragment();		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(AllUse.getSharedPreferencesContent(this, "userName").equalsIgnoreCase("null")) {
			User.userName = "游客";
		} else {
			User.userName = AllUse.getSharedPreferencesContent(this, "userName");
		}
	}

	//初始化
	private void init() {
		ll_home = (LinearLayout)this.findViewById(R.id.ll_home);
		ll_yaoyiyao = (LinearLayout)this.findViewById(R.id.ll_yaoyiyao);
		ll_more = (LinearLayout)this.findViewById(R.id.ll_more);
		ib_home = (ImageButton)this.findViewById(R.id.ib_home);
		ib_yaoyiyao = (ImageButton)this.findViewById(R.id.ib_yaoyiyao);
		ib_more =  (ImageButton)this.findViewById(R.id.ib_more);
		
		ll_home.setOnClickListener(this);
		ll_yaoyiyao.setOnClickListener(this);
		ll_more.setOnClickListener(this);
		ib_home.setOnClickListener(this);
		ib_yaoyiyao.setOnClickListener(this);
		ib_more.setOnClickListener(this);
		
		ll_home.setTag(1);
		ib_home.setTag(1);
		ll_yaoyiyao.setTag(2);
		ib_yaoyiyao.setTag(2);
		ll_more.setTag(3);
		ib_more.setTag(3);		
		
		dw_home = getBaseContext().getResources().getDrawable(R.drawable.home);
		dw_home_ = getBaseContext().getResources().getDrawable(R.drawable.home_);
		dw_yaoyiyao = getBaseContext().getResources().getDrawable(R.drawable.recommend);
		dw_yaoyiyao_ = getBaseContext().getResources().getDrawable(R.drawable.recommend_);
		dw_more = getBaseContext().getResources().getDrawable(R.drawable.more);
		dw_more_ = getBaseContext().getResources().getDrawable(R.drawable.more_);
		
	}
		
	//初始化默认的Fragment
	private void initFragment() {
		fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		RecommendFragment foodMenuFragment = new RecommendFragment();
		ft.add(R.id.frag_Container, foodMenuFragment, "RecommendFragment");
		//ft.addToBackStack(null);
		ft.commit();
	}
	
	@Override
	public void onClick(View v) {
		FragmentTransaction ft = fm.beginTransaction();
		int tag = (Integer)v.getTag();
		
		switch(tag) {
		case 1:		
			if(fm.findFragmentByTag("FoodMenuFragment") != null) {
				if(!fm.findFragmentByTag("FoodMenuFragment").isVisible()) {
					ft.replace(R.id.frag_Container,  (FoodMenuFragment)fm.findFragmentByTag("FoodMenuFragment"));			
					//ft.addToBackStack(null);
					ft.commit();	
					setPicture(dw_home_, dw_yaoyiyao, dw_more);
				}
				return;
			}			
			FoodMenuFragment foodMenuFragment = new FoodMenuFragment();
			ft.replace(R.id.frag_Container, foodMenuFragment, "FoodMenuFragment");
			//ft.addToBackStack(null);
			ft.commit();	
			setPicture(dw_home_, dw_yaoyiyao, dw_more);	
			break;
		case 2:
			if(fm.findFragmentByTag("RecommendFragment") != null) {
				if(!fm.findFragmentByTag("RecommendFragment").isVisible()) {
					ft.replace(R.id.frag_Container,  (RecommendFragment)fm.findFragmentByTag("RecommendFragment"), "RecommendFragment");
					//ft.addToBackStack(null);
					ft.commit();	
					setPicture(dw_home, dw_yaoyiyao_, dw_more);
				}
				return;
			}
			
			RecommendFragment recommendFragment = new RecommendFragment();
			ft.replace(R.id.frag_Container,  recommendFragment,"RecommendFragment");
			//ft.addToBackStack(null);
			ft.commit();	
			setPicture(dw_home, dw_yaoyiyao_, dw_more);
			break;
		case 3:
			if(fm.findFragmentByTag("MoreFragment") != null) {
				if(!fm.findFragmentByTag("MoreFragment").isVisible()) {
					ft.replace(R.id.frag_Container,  (MoreFragment)fm.findFragmentByTag("MoreFragment"));
					//ft.addToBackStack(null);
					ft.commit();	
					setPicture(dw_home, dw_yaoyiyao, dw_more_);
				}
				
				return;
			}
			
			MoreFragment moreFragment = new MoreFragment();
			ft.replace(R.id.frag_Container,  moreFragment, "MoreFragment");
			//ft.addToBackStack(null);
			ft.commit();	
			setPicture(dw_home, dw_yaoyiyao, dw_more_);
			break;	
		}	
	}

	//更换icon的背景图片
	private void setPicture(Drawable home, Drawable information, Drawable more) {
		ib_home.setBackgroundDrawable(home);
		ib_yaoyiyao.setBackgroundDrawable(information);
		ib_more.setBackgroundDrawable(more);	
	}

	//两秒之内点击两次后退键退出程序
	@Override
	public void onBackPressed() {
		long currentTime = new Date().getTime();  
		  
        // 如果时间间隔小于于2秒, 退出程序  
        if ((currentTime - preTime) > TWO_SECOND) {  
            AllUse.info(getApplication(), "快速双击两次退出哎呀呀");
            // 更新时间  
            preTime = currentTime;
        } else {
        	ExceptionApplication.addActivity(this);
        	android.os.Process.killProcess(android.os.Process.myPid()); 		//结束整个应用程序
        	System.exit(0);
        }	         
	}
	
	 //上下文菜单，长按2秒钟激活上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,  ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("操作：");
        //添加菜单项
        menu.add(0, IMEI1, 0, "退出");
        menu.add(0, IMEI2, 0, "注册");
        menu.add(0, IMEI3, 0, "重新");
    }
    
    //菜单单击响应
    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch(item.getItemId()){
        case IMEI1:			//退出
        	android.os.Process.killProcess(android.os.Process.myPid()); 		//结束整个应用程序
            break;
        case IMEI2:			//注册
        	Intent gegister_intent = new Intent(this, RegisterActivity.class);
        	startActivity(gegister_intent);
        	this.finish();
            break;    			
        case IMEI3:			//重新登录
        	Intent login_intent = new Intent(this, LoginActivity.class);
        	startActivity(login_intent);
        	this.finish();
            break;
        }
        return true;
    }
}

