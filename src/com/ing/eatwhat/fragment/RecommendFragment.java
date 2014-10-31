package com.ing.eatwhat.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import com.ing.eatwhat.R;
import com.ing.eatwhat.activity.GetLocationActivity;
import com.ing.eatwhat.database.DBManager;
import com.ing.eatwhat.entity.AllUse;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class RecommendFragment extends Fragment implements AnimationListener{
	private int index = 1; 
	public ImageView wait;
	private ImageButton draw_image;
	public RotateAnimation rotateAnimation;
	private DBManager dbManager;
	private SoundPool soundPool;// 音频池
	private int hitOkSfx;
	private Vibrator mVibrator;// 开启震动

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recommend, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		wait = (ImageView) getView().findViewById(R.id.wait);
		draw_image = (ImageButton) getView().findViewById(R.id.imageButton);		
		dbManager = new DBManager(getActivity());
		init_pull_to_shake();
		initSound();
		mVibrator = (Vibrator) getActivity().getApplication().getSystemService(
				Context.VIBRATOR_SERVICE);
		wait.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation();
				isfirstshark=false;
				String result[] = random();
//				String result[] = {"苹果","西瓜","桃子","芒果"};
				if(result!=null){
					Bitmap bitmap = null;
					try {
						bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(new File(result[1])));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}						
					draw_image.setImageBitmap(toRoundCorner(bitmap,10));
                    draw_image.setAdjustViewBounds(true);
                    LayoutParams para;
                    para = draw_image.getLayoutParams();      
                    para.height = screenWidth/5;
                    para.width = screenWidth/5;
                    draw_image.setLayoutParams(para);
                   
                    draw_image.setMaxHeight(screenHeigh/2);
                    draw_image.setMaxWidth(screenWidth/2);
                    draw_image.setScaleType(ScaleType.CENTER_CROP);
					draw_name.setText("葱烧蹄筋");//+result[0]
					address.setText("地址：工业南路44号闫府私房菜");
					gongjiao.setText("公交路线：");
					draw_name.setVisibility(View.VISIBLE);
					draw_show.setVisibility(View.VISIBLE);
					dianji.setVisibility(View.VISIBLE);
					address.setVisibility(View.VISIBLE);
					gongjiao.setVisibility(View.VISIBLE);
				}
			}
		});
				
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		switch (index){
        case 1://第一个动画
        {
         index++;
         rotateAnimation =new RotateAnimation(-10, 10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
 		 rotateAnimation.setDuration(200);
 		 rotateAnimation.setAnimationListener(this);
 		 wait.startAnimation(rotateAnimation);
        }break;

        case 2://第二个动画
        {
         index++;
         rotateAnimation =new RotateAnimation(10, -10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
 		 rotateAnimation.setDuration(200);
 		 rotateAnimation.setAnimationListener(this);
 		 wait.startAnimation(rotateAnimation);  
        }break;

        case 3://第三个动画
        {
         index++;
         rotateAnimation =new RotateAnimation(-10, 10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
 		 rotateAnimation.setDuration(200);
 		 rotateAnimation.setAnimationListener(this);
 		 wait.startAnimation(rotateAnimation);  
        }break;

        case 4://第四个动画
        {
         index = 0;
         rotateAnimation =new RotateAnimation(10, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
 		 rotateAnimation.setDuration(200);
 		 rotateAnimation.setAnimationListener(this);
 		 wait.startAnimation(rotateAnimation);   
      }break;
      default:break;
}
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
			if (sensorManager != null) {// 注册监听器
				sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
				// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
			}
			isfirstshark=true;
		
		
	};
	
	@Override
	public void onStop() {
		super.onStop();
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
	};
	
//	ImageView draw_image;
	TextView draw_name;
	TextView draw_show;
	TextView address;
	TextView gongjiao;
	Button dianji;
	int screenWidth;
	int screenHeigh;
	private void init_pull_to_shake() {
		// TODO Auto-generated method stub
//        draw_image=(ImageView)getView().findViewById(R.id.draw_image);
        draw_name=(TextView)getView().findViewById(R.id.draw_name);
        draw_show=(TextView)getView().findViewById(R.id.draw_show);
        address = (TextView) getView().findViewById(R.id.address);
        gongjiao = (TextView) getView().findViewById(R.id.gongjiao);
        dianji = (Button) getView().findViewById(R.id.dianji);
        draw_show.setVisibility(View.INVISIBLE);
        draw_name.setVisibility(View.INVISIBLE);
        address.setVisibility(View.INVISIBLE);
        gongjiao.setVisibility(View.INVISIBLE);
        dianji.setVisibility(View.INVISIBLE);
        dianji.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), GetLocationActivity.class);
				startActivity(intent);
				Toast.makeText(getActivity(), "显示公交路线。", Toast.LENGTH_SHORT).show();
				getActivity().finish();
			}
		});
        //panel.setOpen(true, true);
        
        //shake 监听
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
//		vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		
		DisplayMetrics dm = new DisplayMetrics();
		
		//获取屏幕信息
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeigh = dm.heightPixels;
		
	}

	private SensorManager sensorManager;
//	private Vibrator vibrator;

	private static final String TAG = "TestSensorActivity";
	private static final int SENSOR_SHAKE = 10;
	private static final int ISFIRSTSHAKE = 1;
	
	LayoutParams initpara;

	/**
	 * 重力感应监听
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
			Log.i(TAG, "x轴方向的重力加速度" + x +  "；y轴方向的重力加速度" + y +  "；z轴方向的重力加速度" + z);
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 14;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
//				if(isfirstshark)
//				vibrator.vibrate(200);
				Message msg = new Message();
				msg.what = SENSOR_SHAKE;
				handler.sendMessage(msg);
				
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
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
				if(isfirstshark){
					startAnimation();
					isfirstshark=false;
					String result[] = random();
//					String result[] = {"苹果","西瓜","桃子","芒果"};
					if(result!=null){
						Bitmap bitmap = null;
						try {
							bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(new File(result[1])));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
						draw_image.setImageBitmap(toRoundCorner(bitmap,10));
                        draw_image.setAdjustViewBounds(true);
                        LayoutParams para;
                        para = draw_image.getLayoutParams();      
                        para.height = screenWidth/5;
                        para.width = screenWidth/5;
                        draw_image.setLayoutParams(para);
                       
                        draw_image.setMaxHeight(screenHeigh/2);
                        draw_image.setMaxWidth(screenWidth/2);
                        draw_image.setScaleType(ScaleType.CENTER_CROP);
						draw_name.setText("葱烧蹄筋");//+result[0]
						address.setText("地址：工业南路44号闫府私房菜");
						gongjiao.setText("公交路线：");
						draw_name.setVisibility(View.VISIBLE);
						draw_show.setVisibility(View.VISIBLE);
						dianji.setVisibility(View.VISIBLE);
						address.setVisibility(View.VISIBLE);
						gongjiao.setVisibility(View.VISIBLE);
						Log.i("url", result[1]+screenHeigh+" "+screenWidth);
					}
//					Toast.makeText(getActivity(), "检测到摇晃，执行操作！"+random(), Toast.LENGTH_SHORT).show();
				}
				else{
//					AllUse.info(getActivity(), "只能摇一次哦！");
					if (sensorManager != null) {// 取消监听器
						sensorManager.unregisterListener(sensorEventListener);
					}
				}
				Log.i(TAG, "检测到摇晃，执行操作！");				
				break;
			case ISFIRSTSHAKE:
				if (sensorManager != null) {// 注册监听器
					sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
					// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
				}
				isfirstshark=true;
				break;
			}
		}

	};
	
	boolean isfirstshark=false;
	public String[] random() {
		
		ArrayList<String> arr_name=dbManager.getAllFood().get("name");
		ArrayList<String> arr_picPath=dbManager.getAllFood().get("picPath");
		int length = arr_name.size();
		if (length > 1) {			
			Random random = new Random();
			int next=random.nextInt(length);
			String[] result={arr_name.get(next),arr_picPath.get(next)};
			return result;
		} else if (length == 1) {
			AllUse.info(getActivity(), "只有一个菜肴木有摇一摇的乐趣哦~");
			String[] result={arr_name.get(0),arr_picPath.get(0)};
			return result;
		} else {
			AllUse.info(getActivity(), "先添加一个菜肴吧~");
		}
		return null;
	}
	
	public void startAnimation(){
		
		index = 1;
		rotateAnimation =new RotateAnimation(0, -10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(200);
		rotateAnimation.setAnimationListener(this);
		wait.startAnimation(rotateAnimation);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		});
		updateThread.start(); /* 启动线程 */
		
	}
	/** 
	  * 将图片设置为圆角 
	  */ 
	 public  Bitmap toRoundCorner(Bitmap bitmap, int pixels) { 
	  Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), 
	  bitmap.getHeight(), Config.ARGB_8888); 
	  Canvas canvas = new Canvas(output); 
	  final int color = 0xff424242; 
	  final Paint paint = new Paint(); 
	  final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
	  final RectF rectF = new RectF(rect); 
	  final float roundPx = pixels; 
	  paint.setAntiAlias(true); 
	  canvas.drawARGB(0, 0, 0, 0); 
	  paint.setColor(color); 
	  canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
	  paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
	  canvas.drawBitmap(bitmap, rect, rect, paint); 
	  return output; 
	 }
	 public void initSound(){
		// 这里指定声音池的最大音频流数目为10，
			// 声音品质为5大家可以自 己测试感受下效果
			soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
			// 载入音频流
			hitOkSfx = soundPool.load(getActivity(), R.raw.shake, 0);
	 }
}
