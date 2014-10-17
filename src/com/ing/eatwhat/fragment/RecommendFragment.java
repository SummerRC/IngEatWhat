package com.ing.eatwhat.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import com.ing.eatwhat.R;
import com.ing.eatwhat.database.DBManager;
import com.ing.eatwhat.entity.AllUse;
import android.annotation.SuppressLint;
import android.content.Context;
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
	private SoundPool soundPool;// ��Ƶ��
	private int hitOkSfx;
	private Vibrator mVibrator;// ������

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
				
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		switch (index){
        case 1://��һ������
        {
         index++;
         rotateAnimation =new RotateAnimation(-10, 10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
 		 rotateAnimation.setDuration(200);
 		 rotateAnimation.setAnimationListener(this);
 		 wait.startAnimation(rotateAnimation);
        }break;

        case 2://�ڶ�������
        {
         index++;
         rotateAnimation =new RotateAnimation(10, -10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
 		 rotateAnimation.setDuration(200);
 		 rotateAnimation.setAnimationListener(this);
 		 wait.startAnimation(rotateAnimation);  
        }break;

        case 3://����������
        {
         index++;
         rotateAnimation =new RotateAnimation(-10, 10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
 		 rotateAnimation.setDuration(200);
 		 rotateAnimation.setAnimationListener(this);
 		 wait.startAnimation(rotateAnimation);  
        }break;

        case 4://���ĸ�����
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
		if (sensorManager != null) {// ȡ��������
			sensorManager.unregisterListener(sensorEventListener);
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (sensorManager != null) {// ȡ��������
			sensorManager.unregisterListener(sensorEventListener);
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
			if (sensorManager != null) {// ע�������
				sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
				// ��һ��������Listener���ڶ������������ô��������ͣ�����������ֵ��ȡ��������Ϣ��Ƶ��
			}
			isfirstshark=true;
		
		
	};
	
	@Override
	public void onStop() {
		super.onStop();
		if (sensorManager != null) {// ȡ��������
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
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "��ʾ����·�ߡ�", Toast.LENGTH_SHORT).show();
			}
		});
        //panel.setOpen(true, true);
        
        //shake ����
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
//		vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		
		DisplayMetrics dm = new DisplayMetrics();
		
		//��ȡ��Ļ��Ϣ
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
	 * ������Ӧ����
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// ��������Ϣ�ı�ʱִ�и÷���
			float[] values = event.values;
			float x = values[0]; // x�᷽����������ٶȣ�����Ϊ��
			float y = values[1]; // y�᷽����������ٶȣ���ǰΪ��
			float z = values[2]; // z�᷽����������ٶȣ�����Ϊ��
			Log.i(TAG, "x�᷽����������ٶ�" + x +  "��y�᷽����������ٶ�" + y +  "��z�᷽����������ٶ�" + z);
			// һ����������������������ٶȴﵽ40�ʹﵽ��ҡ���ֻ���״̬��
			int medumValue = 14;// ���� i9250��ô�ζ����ᳬ��20��û�취��ֻ����19��
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
	 * ����ִ��
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
//					String result[] = {"ƻ��","����","����","â��"};
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
						draw_name.setText("�������");//+result[0]
						address.setText("��ַ����ҵ��·44���Ƹ�˽����");
						gongjiao.setText("����·�ߣ�");
						draw_name.setVisibility(View.VISIBLE);
						draw_show.setVisibility(View.VISIBLE);
						dianji.setVisibility(View.VISIBLE);
						address.setVisibility(View.VISIBLE);
						gongjiao.setVisibility(View.VISIBLE);
						Log.i("url", result[1]+screenHeigh+" "+screenWidth);
					}
//					Toast.makeText(getActivity(), "��⵽ҡ�Σ�ִ�в�����"+random(), Toast.LENGTH_SHORT).show();
				}
				else{
//					AllUse.info(getActivity(), "ֻ��ҡһ��Ŷ��");
					if (sensorManager != null) {// ȡ��������
						sensorManager.unregisterListener(sensorEventListener);
					}
				}
				Log.i(TAG, "��⵽ҡ�Σ�ִ�в�����");				
				break;
			case ISFIRSTSHAKE:
				if (sensorManager != null) {// ע�������
					sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
					// ��һ��������Listener���ڶ������������ô��������ͣ�����������ֵ��ȡ��������Ϣ��Ƶ��
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
			AllUse.info(getActivity(), "ֻ��һ������ľ��ҡһҡ����ȤŶ~");
			String[] result={arr_name.get(0),arr_picPath.get(0)};
			return result;
		} else {
			AllUse.info(getActivity(), "�����һ�����Ȱ�~");
		}
		return null;
	}
	
	public void startAnimation(){
		
		index = 1;
		rotateAnimation =new RotateAnimation(0, -10, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(200);
		rotateAnimation.setAnimationListener(this);
		wait.startAnimation(rotateAnimation);
		// �������0.5���Ϊ2��1���� �����ٶ�
		soundPool.play(hitOkSfx, 1, 1, 0, 0, 1);
		mVibrator.vibrate(300);
		// ��һ�����������ǽ������飬 �ڶ����������ظ�������-1Ϊ���ظ�����-1���մ�pattern��ָ���±꿪ʼ�ظ�
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
		updateThread.start(); /* �����߳� */
		
	}
	/** 
	  * ��ͼƬ����ΪԲ�� 
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
		// ����ָ�������ص������Ƶ����ĿΪ10��
			// ����Ʒ��Ϊ5��ҿ����� �����Ը�����Ч��
			soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
			// ������Ƶ��
			hitOkSfx = soundPool.load(getActivity(), R.raw.shake, 0);
	 }
}
