package com.ing.eatwhat.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ing.eatwhat.R;
import com.ing.eatwhat.database.DBManager;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.Food;
import com.ing.eatwhat.entity.User;

@SuppressLint("HandlerLeak")
public class RecommendFragment extends Fragment implements AnimationListener, SensorEventListener{
	private int index = 1; 
	private RelativeLayout rl_food;					//food的相对布局
	private ImageView iv_food;
	private TextView tv_foodname;
	private RelativeLayout rl_yaoyiyao;			//摇一摇的相对布局	
	private ImageView iv_yaoyiyao;
	
	public RotateAnimation rotateAnimation;
	private DBManager dbManager;
	private SoundPool soundPool;				// 音频池
	private int hitOkSfx;
	private Vibrator mVibrator;						// 震动
	private SensorManager mSensorManager;
	private Sensor mSensor;
	boolean isfirstshark=false;
	
	int screenWidth;
	int screenHeigh;
	private static final String TAG = "TestSensorActivity";
	private static final int SENSOR_SHAKE = 10;
	private static final int ISFIRSTSHAKE = 1;
	
	LayoutParams initpara;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recommend, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	
	
	private void init() {
		dbManager = new DBManager(getActivity());
		//创建一个SensorManager来获取系统的传感器服务
		mSensorManager = (SensorManager)  getActivity().getApplication().getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);		
		mVibrator = (Vibrator)  getActivity().getApplication().getSystemService(Context.VIBRATOR_SERVICE);
		
		rl_food = (RelativeLayout) getView().findViewById(R.id.rl_food);
		rl_yaoyiyao = (RelativeLayout) getView().findViewById(R.id.rl_yaoyiyao);
		iv_yaoyiyao = (ImageView) getView().findViewById(R.id.iv_yaoyiyao);
		iv_food = (ImageView) getView().findViewById(R.id.iv_food);	
		tv_foodname = (TextView) getView().findViewById(R.id.tv_foodname);
		iv_yaoyiyao.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {	
				startAnimation();	
				if(limit() == -1) {
					return;
				}	
				isfirstshark=false;
				rl_yaoyiyao.setVisibility(View.GONE);
				rl_food.setVisibility(View.VISIBLE);
				
				Food food = dbManager.randomFood();
				Bitmap bitmap = null;
				try {
					bitmap = BitmapFactory.decodeFile(food.getPicPath());
				} catch (Exception e) {
					e.printStackTrace();
				}					
				iv_food.setImageBitmap(bitmap);
				tv_foodname.setText(food.getName());
			}
		});
		
		DisplayMetrics dm = new DisplayMetrics();	
		//获取屏幕信息
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeigh = dm.heightPixels;
		
		// 这里指定声音池的最大音频流数目为10，
		// 声音品质为5大家可以自 己测试感受下效果
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		// 载入音频流
		hitOkSfx = soundPool.load(getActivity(), R.raw.shake, 0);
	}

	@Override
	public void onResume() {
		super.onResume();
			if (mSensorManager != null) {			// 注册监听器
				// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
				mSensorManager.registerListener(RecommendFragment.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);		
			}
			isfirstshark=true;	
	};
	
	@Override
	public void onPause() {
		if (mSensorManager != null) {				// 取消监听器
			mSensorManager.unregisterListener(RecommendFragment.this);
		}
		super.onPause();
	};
	
	@Override
	public void onStop() {
		super.onStop();
		/*if (mSensorManager != null) {				// 取消监听器
			mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		}*/
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		/*if (mSensorManager != null) {				// 取消监听器
			mSensorManager.unregisterListener(this);
		}*/
	};


	/**
	 * 动作执行
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SENSOR_SHAKE:
				if(isfirstshark) {	
					startAnimation();
					isfirstshark=false;
					if(limit() == -1) {
						return;
					}	
					rl_yaoyiyao.setVisibility(View.GONE);
					rl_food.setVisibility(View.VISIBLE);
					
					Food food = dbManager.randomFood();
					Bitmap bitmap = null;
					try {
						bitmap = BitmapFactory.decodeFile(food.getPicPath());
					} catch (Exception e) {
						e.printStackTrace();
					}				
						
					iv_food.setImageBitmap(bitmap);
					tv_foodname.setText(food.getName());
				} else {
					if (mSensorManager != null) {			// 取消监听器
						mSensorManager.unregisterListener(RecommendFragment.this);
					}
				}
				break;
			case ISFIRSTSHAKE:
				if (mSensorManager != null) {	 			// 注册监听器
					//mSensorManager.registerListener(RecommendFragment.this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
					// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
				}
				AllUse.info(getActivity(), "一次只能摇一次哟~");
				isfirstshark=true;
				break;
			}
		}
	};
	
	
	
	private int limit() {
		int count = AllUse.getFoodNum(getActivity());
		if (count == 1) {
			AllUse.info(getActivity(), "只有一个菜肴木有摇一摇的乐趣哦~");
		} 
		else if(count == 0){
			AllUse.info(getActivity(), "先添加一个菜肴吧~");
			return -1;
		}
		return 0;
	}
	
	public void startAnimation(){	
		index = 1;
		rotateAnimation =new RotateAnimation(0, -10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(200);
		rotateAnimation.setAnimationListener(this);
		iv_yaoyiyao.startAnimation(rotateAnimation);
		// 速率最低0.5最高为2，1代表 正常速度
		soundPool.play(hitOkSfx, 1, 1, 0, 0, 1);
		mVibrator.vibrate(300);
		// 第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
		mVibrator.vibrate(new long[] { 100, 200, 100, 300 }, -1);
		Thread updateThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		});
		updateThread.start(); /* 启动线程 */		
	}

	 @Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			switch (index){
	        case 1://第一个动画
	        {
	         index++;
	         rotateAnimation =new RotateAnimation(-10, 10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	 		 rotateAnimation.setDuration(200);
	 		 rotateAnimation.setAnimationListener(this);
	 		 iv_yaoyiyao.startAnimation(rotateAnimation);
	        }break;

	        case 2://第二个动画
	        {
	         index++;
	         rotateAnimation =new RotateAnimation(10, -10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	 		 rotateAnimation.setDuration(200);
	 		 rotateAnimation.setAnimationListener(this);
	 		 iv_yaoyiyao.startAnimation(rotateAnimation);  
	        }break;

	        case 3://第三个动画
	        {
	         index++;
	         rotateAnimation =new RotateAnimation(-10, 10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	 		 rotateAnimation.setDuration(200);
	 		 rotateAnimation.setAnimationListener(this);
	 		 iv_yaoyiyao.startAnimation(rotateAnimation);  
	        }break;

	        case 4://第四个动画
	        {
	         index = 0;
	         rotateAnimation =new RotateAnimation(10, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	 		 rotateAnimation.setDuration(200);
	 		 rotateAnimation.setAnimationListener(this);
	 		iv_yaoyiyao.startAnimation(rotateAnimation);   
	      }break;
	      default:break;
			}
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; 				// x轴方向的重力加速度，向右为正
			float y = values[1]; 				// y轴方向的重力加速度，向前为正
			float z = values[2]; 				// z轴方向的重力加速度，向上为正
			//Log.i(TAG, "x轴方向的重力加速度" + x +  "；y轴方向的重力加速度" + y +  "；z轴方向的重力加速度" + z);
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 100 - User.sensitivity;			// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
				//	if(isfirstshark)
				//	vibrator.vibrate(200);
				Message msg = new Message();
				msg.what = SENSOR_SHAKE;
				handler.sendMessage(msg);
			}
			
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			//当传感器精度改变时回调该方法，Do nothing.  		
		}
}
