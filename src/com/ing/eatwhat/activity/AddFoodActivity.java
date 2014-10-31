package com.ing.eatwhat.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONObject;
import com.ing.eatwhat.R;
import com.ing.eatwhat.database.DBManager;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.Food;
import com.ing.eatwhat.entity.User;
import com.ing.eatwhat.thread.NetThread;
import com.qiniu.auth.JSONObjectRet;
import com.qiniu.io.IO;
import com.qiniu.io.PutExtra;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;

public class AddFoodActivity extends Activity implements View.OnClickListener{
	
	private Button bt_addfood_save;				//保存按钮
	private Button bt_addfood_back;
	private static EditText et_foodname;		//输入菜名的的EditText
	private static ImageView iv_food;			//显示图片的ImageView
	private static TextView tv_addfood_hint;	//上传进度提示的TextView
	private Intent getIntent;
	private final int CAMERA = 1;				//回调函数的标示
	private final int PHOTO_ALBUM = 2;			//回调函数的标示
	private Uri picUri;  						//定位图片的uri
	private String picPath;						//从相机或者相册获取的图片经过处理后存储的位置
	private String picName; 					//图片名（本地和网上名称一致）
	private String oldFoodName;					//旧的菜名
	private String newFoodName;					//新的菜名
	private boolean uploadPicture = false;		//用一个布尔值记录上传状态
	private boolean isChanged = false;			//用一个布尔值记录是否被修改过
	private String token;						//上传下载的凭证
	private Handler mHandler;
	private DBManager dbManager;				//数据库管理类的实例
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfood);	
		
		init();			
		if(getIntent.getStringExtra("op").compareToIgnoreCase("edit") == 0) {
			bt_addfood_save.setText("修改");
		}	
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(Integer.valueOf(msg.what)) {
				case 0:
					String result = msg.obj.toString().trim();
					int tag = Integer.valueOf(result).intValue();
					switch(tag){
					case 0:
						AllUse.info(AddFoodActivity.this, "警告：加入菜单失败！菜名重复！");
						tv_addfood_hint.setText("加入失败，菜名重复！");
						clean();
						et_foodname.setText(oldFoodName);
						break;
					case 1:     //添加成功
						Food food = new Food();
						AllUse.editFoodNum(AddFoodActivity.this, 1);
						food.setId(AllUse.getFoodNum(getApplicationContext()));	
						food.setName(newFoodName);
						food.setPicPath(picPath);
						dbManager.insertFood(food);
						clean();
						AddFoodActivity.this.finish();
						break;
					case 4:
						AllUse.info(AddFoodActivity.this, "警告：原菜名不存在！");
						tv_addfood_hint.setText("加入失败，原菜名不存在！");
						clean();
						et_foodname.setText(oldFoodName);
						break;
					case 5:     //修改成功
						dbManager.updateFoodPic(getIntent.getStringExtra("foodname"), picPath);				
						dbManager.updateFoodName(getIntent.getStringExtra("foodname"), newFoodName);
						clean();
						AddFoodActivity.this.finish();
						break;
					case 6:
						AllUse.info(AddFoodActivity.this, "该菜名已经存在！");
						tv_addfood_hint.setText("该菜名存在！");
						clean();
						et_foodname.setText(oldFoodName);
						break;
					}		
					break;
				case 1:
					//处理下载的图片
					break;
				case 2:
					//上传凭证
					token = msg.obj.toString().trim();
					break;
				case 222:
					AllUse.info(AddFoodActivity.this, "食物名不能包含特殊字符：? | & 空格等.");
					tv_addfood_hint.setText("食物名不能包含特殊字符：? | & 空格等.");
					clean();
					break;
				}
			}			
		};
	}
	
	//初始化
	private void init() {
		dbManager = new DBManager(this);
		getIntent = this.getIntent();	
		et_foodname = (EditText)findViewById(R.id.et_foodname);
		tv_addfood_hint = (TextView)findViewById(R.id.tv_addfood_hint);
		iv_food = (ImageView )findViewById(R.id.iv_food);	
		bt_addfood_save = (Button)findViewById(R.id.bt_addfood_save);
		bt_addfood_back = (Button)findViewById(R.id.bt_addfood_back);
		iv_food.setOnClickListener(this);
		bt_addfood_save.setOnClickListener(this);
		bt_addfood_back.setOnClickListener(this);
		if(getIntent.getStringExtra("op").compareToIgnoreCase("edit") == 0) {
			bt_addfood_save.setText("修改");
			oldFoodName = getIntent.getStringExtra("foodname");
			et_foodname.setText(oldFoodName);			
			iv_food.setImageURI(Uri.fromFile(new File(dbManager.query(oldFoodName).getPicPath())));
		}
	}
		
	//保存按钮、取消按钮、ImageView的监听器
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.bt_addfood_back:				//返回按钮
			if(uploadPicture) {
				AllUse.info(AddFoodActivity.this, "正在上传图片，请稍后");
				return;
			}
			AddFoodActivity.this.finish();
			break;
		case R.id.bt_addfood_save:				//保存按钮
			newFoodName = et_foodname.getText().toString();
			bt_save();
			break;
		case R.id.iv_food:						//点击ImageView
			dialogClick(view);
			break;
		}		
	}	
		
	//提示选择相机还是相册
	public void dialogClick(View view) {
		// 检测sd是否可用
		String SDStatus = Environment.getExternalStorageState();
	    if (!SDStatus.equals(Environment.MEDIA_MOUNTED)) { 
	    	AllUse.info(AddFoodActivity.this, "警告：SD卡不可用！");
	        return;
	    }
		//为获得上传凭证开启一个线程
		NetThread tokenThread = new NetThread(mHandler, null, null, null, null, 6);
		tokenThread.start();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示")
			   .setMessage("请选择插入图片方式：")
			   .setCancelable(true)           //按返回键可以退出 
			   .setNegativeButton("相机", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   //调用相机
					   Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			           startActivityForResult(camera, CAMERA);   	//CAMERA是个标记，根据标记是哪个Intent引起的回调函数			           
				   }
			   })  
			   .setNeutralButton("相册", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   //调用系统相册
					   Intent picture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库 
					   startActivityForResult(picture, PHOTO_ALBUM); 		//PHOTO_ALBUM是个标记，根据标记是哪个Intent引起的回调函数			
					   }
			   })	   
			   .setPositiveButton("取消", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   dialog.dismiss();
				   }
			   }).show();	 
	}
	
	//回调函数
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isChanged = true;
        if(!(resultCode == Activity.RESULT_OK)) {
	    	AllUse.info(AddFoodActivity.this, "操作失败，请重试！");
	    	return;
	    }
        switch(requestCode) {
        case CAMERA:	
        	this.camera(resultCode, data);
        	break;
        case PHOTO_ALBUM:
        	this.photoAlbum(resultCode, data);
        	break;
        }       
        
	}
	  
	//相册选择照片后，回调函数会调用这个方法处理图片
	private void photoAlbum(int resultCode, Intent data) {
		if(data == null || resultCode != RESULT_OK) {
	    	AllUse.info(AddFoodActivity.this, "未选中任何图片!");
	    	return;
	    }
		
		picUri = data.getData();		//定位到图片位置，上传的是原图
	    String[] filePathColumn = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getContentResolver().query(picUri, filePathColumn, null, null, null);
	    cursor.moveToFirst();
	    int columnIndex = cursor.getColumnIndex(filePathColumn[0]); 
	    String picturePath = cursor.getString(columnIndex);
	    cursor.close();
	    Bitmap bitmap = null;        
	   
		try {
			BitmapFactory.Options options = new BitmapFactory.Options(); 
	        options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一 
	        bitmap = BitmapFactory.decodeFile(picturePath, options);   	
		} catch (Exception e) {
			e.printStackTrace();
			AllUse.info(AddFoodActivity.this, "图片加载失败!");
			return;
		} catch (OutOfMemoryError e) {
			AllUse.info(AddFoodActivity.this, "图片太大,请换张图片！");
			return;
		} catch (Error e) {
			e.printStackTrace();
			AllUse.info(AddFoodActivity.this, "图片太大，内存溢出!");
			return;
	    } 
		//将图片显示在ImageView
	    iv_food.setImageBitmap(bitmap);  
	    //获得图片存储的绝对路径，并存储图片
		picPath = this.getPicPath();
		AllUse.savePicture(bitmap, picPath);		
	}

	//相机拍照后回调方法会调用这个函数，处理拍摄的照片
	private void camera(int resultCode, Intent data) {
		if(data == null || resultCode != RESULT_OK) {
	    	AllUse.info(AddFoodActivity.this, "操作取消！");
	    	return;
	    }
		// 获取相机返回的数据，并转换为Bitmap图片格式
        Bundle bundle = data.getExtras();  
        Bitmap bitmap = (Bitmap) bundle.get("data");		
		if (data.getData() != null) {  
            picUri = data.getData();  
        } else {  
            picUri  = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));      
        }       
        //将图片宽和高均压缩成100
        Bitmap rbitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);  
        //将图片显示在ImageView
        iv_food.setImageBitmap(rbitmap);
        //获得图片存储的绝对路径，并存储图片
        picPath = this.getPicPath();
		AllUse.savePicture(rbitmap, picPath);	
	}
	
	//上传图片到七牛云存储，用的是UI线程
	public void uploadPicture() {
        uploadPicture = true;
		PutExtra extra = new PutExtra();
		extra.params = new HashMap<String, String>();
		extra.params.put("x:a", "测试中文信息");
		tv_addfood_hint.setText("上传中...");
		IO.putFile(AddFoodActivity.this, token, picName, Uri.fromFile(new File(picPath)), extra, new JSONObjectRet() {
			@Override
			public void onProcess(long current, long total) {
				tv_addfood_hint.setText("已上传:" + current + "/" + "共计" + total);
			}

			@Override
			public void onSuccess(JSONObject resp) {
				tv_addfood_hint.setText("上传成功！");
				//如果上传成功（只有保存或者修改成功才会上传），那么插入到数据库或者更新数据库
				if(getIntent.getStringExtra("op").compareToIgnoreCase("save") == 0) {
					//为存储用户的菜名开启一个线程
					NetThread thread = new NetThread(mHandler, User.userName, newFoodName, null, picName, 3);
					thread.start();		
				} else
					if(getIntent.getStringExtra("op").compareToIgnoreCase("edit") == 0) {
						NetThread thread = new NetThread(mHandler, User.userName, getIntent.getStringExtra("foodname"), newFoodName, picName, 4);
						thread.start();									
					}
			}

			@Override
			public void onFailure(Exception ex) {
				tv_addfood_hint.setText("错误: "+ ex.getMessage());	
				String str = "{\"error\"" + ":" + "\"file exists\"}";
				if(ex.getMessage().toString().equals(str)) {
					AllUse.info(getApplicationContext(), "菜名重复，添加失败！");
					clean();
					et_foodname.setText(oldFoodName);
					return;
				} else {			
					AllUse.info(AddFoodActivity.this, "亲，网络不给力，稍后再试吧!" + ex.getMessage());
					clean();
					AddFoodActivity.this.finish();
				}
				
			}
		});	
	}
	
	//返回一个绝对路径、作为照片的存储位置
  	public String getPicPath() {
  		//获得SD卡的路径
        final String SDpath = Environment .getExternalStorageDirectory().getAbsolutePath();
        File file = new File(SDpath +"/ingZone/eatWhat/");
        // 创建文件夹
        file.mkdirs(); 
        //照片的命名，目标文件夹下，以用户名加当前时间数字串为名称，即可确保每张照片名称不相同
        Date date = new Date();
        //获取当前时间并且进一步转化为字符串
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmSS", Locale.getDefault());
        picName = User.userName + format.format(date);
        String path = SDpath + "/ingZone/eatWhat/" + picName + ".jpg";
        return path;
  	}	
  	
  	//监听器监听到用户点击保存按钮，则调用该函数
  	public void bt_save() {
  		//判断是否联网
		if(!AllUse.isHaveInternet(this)){
			AllUse.info(AddFoodActivity.this, "未连接网络,操作失败");
			return;
		}
		if(uploadPicture) {
			AllUse.info(AddFoodActivity.this, "正在上传图片，请稍后");
			return;
		}	
		if(newFoodName.equals("")) {
			AllUse.info(AddFoodActivity.this, "警告：菜名不能为空！");
			return;
		}	
		if(newFoodName.length() > 18) {
			AllUse.info(AddFoodActivity.this, "警告：菜名太长！");
			return;
		}						
		if(getIntent.getStringExtra("op").compareToIgnoreCase("edit") == 0){		
			//判断食物名和对应图片有没有被修改或插入
			if(isChanged == false) {
				AllUse.info(AddFoodActivity.this, "请修改图片！");
				return;
			}
			if(newFoodName.equals(oldFoodName)) {
				AllUse.info(AddFoodActivity.this, "请修改菜名！");
				return;
			}
			//设置imagebutton、edittext均不可点击
			et_foodname.setEnabled(false);
			iv_food.setClickable(false);
			//上传照片到七牛云存储
			uploadPicture();	
		} else 
			if(getIntent.getStringExtra("op").compareToIgnoreCase("save") == 0) {			
				if(isChanged == false) {
					AllUse.info(AddFoodActivity.this, "请插入图片！");
					return;
				}			
				//设置imagebutton、edittext均不可点击
				et_foodname.setEnabled(false);
				iv_food.setClickable(false);	
				//上传照片到七牛云存储
				uploadPicture();						
			} else {
				AllUse.info(AddFoodActivity.this, "操作失败！");
				return;
			}
		
  		
  	}
  	
  	//处理返回键，防止用户在上传过程中按返回键
  	@Override
  	public void onBackPressed() {
  		if(uploadPicture) {       
  			AllUse.info(AddFoodActivity.this, "正在上传图片，请稍后操作");
  			return;
  		}
  		AddFoodActivity.this.finish();
  	}
	
  	//
	private void clean() {
		et_foodname.setText("");
		picPath = null;
		picUri = null;
		picName = "";
		isChanged = false;
		uploadPicture = false;	
		et_foodname.setEnabled(true);
		iv_food.setClickable(true);
	}
	
  	
}