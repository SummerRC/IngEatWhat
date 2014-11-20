package com.ing.eatwhat.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.ing.eatwhat.R;
import com.ing.eatwhat.database.DBManager;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.Food;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;

public class AddFoodActivity extends Activity implements View.OnClickListener {
	
	private Button bt_addfood_save;					//保存按钮
	private Button bt_addfood_back;
	private static EditText et_foodname;			//输入菜名的的EditText
	private static ImageView iv_picture;			//显示图片的ImageView
	private Intent getIntent;
	private final int CAMERA = 1;						//回调函数的标示
	private final int PHOTO_ALBUM = 2;			//回调函数的标示
	private Uri picUri;  											//定位图片的uri
	private String picPath;									//从相机或者相册获取的图片经过处理后存储的位置
	//private String picName; 								//图片名（本地和网上名称一致）
	private String oldFoodName;						//旧的菜名
	private String newFoodName;						//新的菜名
	private DBManager dbManager;					//数据库管理类的实例
	private boolean haveInsertedPic	= false;	//是否插入图片
	private static int op;
	private final static int EDIT = 1;					//修改操作
	private final static int ADD = 0;					//添加操作
	private ImageView iv_photo;
	private ImageView iv_camera;
	private Bitmap MyBitmap = null;
	private String default_path;
	private TextView tv_addfood_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//全屏显示  无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfood);	
		
		init();			
	}
	
	//初始化
	private void init() {				
		et_foodname = (EditText)findViewById(R.id.et_foodname);	
		iv_picture = (ImageView )findViewById(R.id.iv_picture);	
		bt_addfood_save = (Button)findViewById(R.id.bt_addfood_save);
		bt_addfood_back = (Button)findViewById(R.id.bt_addfood_back);
		iv_picture.setOnClickListener(this);
		bt_addfood_save.setOnClickListener(this);
		bt_addfood_back.setOnClickListener(this);
		tv_addfood_title = (TextView) this.findViewById(R.id.tv_addfood_title);
		
		dbManager = new DBManager(this);
		getIntent = this.getIntent();	
		if(getIntent.getStringExtra("op").compareToIgnoreCase("edit") == 0) {
			op = EDIT;	//是修改操作
			tv_addfood_title.setText("修改");
			oldFoodName = getIntent.getStringExtra("foodname");
			et_foodname.setText(oldFoodName);			
			iv_picture.setImageURI(Uri.fromFile(new File(dbManager.query(oldFoodName).getPicPath())));
		} else {
			op = ADD;	//是添加操作
		}
		
		iv_photo = (ImageView) this.findViewById(R.id.iv_photo);
		iv_photo.setOnClickListener(this);
		iv_camera = (ImageView) this.findViewById(R.id.iv_camera);
		iv_camera.setOnClickListener(this);
	}
		
	//保存按钮、返回按钮、ImageView的监听器
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.bt_addfood_back:				//返回按钮		
			AddFoodActivity.this.finish();
			break;
		case R.id.bt_addfood_save:				//保存按钮
			newFoodName = et_foodname.getText().toString();
			bt_save();
			break;
		case R.id.iv_picture:							//点击ImageView
			dialogClick(view);	
			break;
		case R.id.iv_photo:
			//调用系统相册
			Intent picture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(picture, PHOTO_ALBUM); 
			break;
		case R.id.iv_camera:
			//调用相机
			default_path = getPicPath();
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(default_path)));
			startActivityForResult(camera, CAMERA);   	
			break;
		}		
	}	
		
	//对话框：提示选择相机还是相册
	public void dialogClick(View view) {
		// 检测sd是否可用
		String SDStatus = Environment.getExternalStorageState();
	    if (!SDStatus.equals(Environment.MEDIA_MOUNTED)) { 
	    	AllUse.info(getApplication(), "警告：SD卡不可用！");
	        return;
	    }
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示")
			   .setMessage("请选择插入图片方式：")
			   .setCancelable(true)           
			   .setNegativeButton("相机", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   //调用相机
					   default_path = getPicPath();
					   Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					   camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(default_path)));
					   startActivityForResult(camera, CAMERA);   				
				   }
			   })  
			   .setNeutralButton("相册", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   //调用系统相册
					   Intent picture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					   startActivityForResult(picture, PHOTO_ALBUM); 		
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) throws java.lang.IllegalArgumentException{
        super.onActivityResult(requestCode, resultCode, data);
      
        if(!(resultCode == Activity.RESULT_OK)) {
	    	AllUse.info(getApplication(), "操作失败，请重试！");
	    	return;
	    }
        switch(requestCode) {
        case CAMERA:									//处理相机返回的数据
        	this.camera(resultCode, data);
        	break;
        case PHOTO_ALBUM:							//处理相册返回的数据
        	this.photoAlbum(resultCode, data);
        	break;
        }           
	}
	  
	//相机拍照后回调方法会调用这个函数，处理拍摄的照片
	private void camera(int resultCode, Intent data){
		if(resultCode != RESULT_OK) {
	    	AllUse.info(getApplication(), "操作取消！");
	    	return;
	    }
		haveInsertedPic = true;
		Bitmap bitmap = BitmapFactory.decodeFile(default_path);
		try {			
			int scale = reckonThumbnail(bitmap.getWidth(),bitmap.getHeight(), 250, 250);   
			if(MyBitmap != null && !MyBitmap.isRecycled()) {
				MyBitmap.recycle();
				MyBitmap = null;
			}
			 MyBitmap = PicZoom(bitmap, bitmap.getWidth() / scale,bitmap.getHeight() / scale);  
		     if(bitmap !=null) {
			     bitmap.recycle();
		    	 bitmap = null;
		     }
         } catch (Exception e) {
             e.printStackTrace();
         }
		haveInsertedPic = true;
		//将图片显示在ImageView
        iv_picture.setImageBitmap(MyBitmap);
	}
	
	//相册选择照片后，回调函数会调用这个方法处理图片
	private void photoAlbum(int resultCode, Intent data) {
		if(data == null || resultCode != RESULT_OK) {
	    	AllUse.info(getApplication(), "未选中任何图片!");
	    	return;
	    }
		haveInsertedPic = true;
		picUri = data.getData();		//定位到图片位置，上传的是原图
	    String[] filePathColumn = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getContentResolver().query(picUri, filePathColumn, null, null, null);
	    cursor.moveToFirst();
	    int columnIndex = cursor.getColumnIndex(filePathColumn[0]); 
	    String picturePath = cursor.getString(columnIndex);
	    cursor.close();
	    Bitmap bitmap = null;        
		try {
			bitmap = BitmapFactory.decodeFile(picturePath);
			if(bitmap.isRecycled()) {			//4.1.1之后会出现Cannot draw recycled bitmaps错误   若用户选择的是处理过的图片，则提示返回
				AllUse.info(this.getApplication(), "这是张缩略图，请添加原图");
				return;
			}
			int scale = reckonThumbnail(bitmap.getWidth(),bitmap.getHeight(), 250, 250);   
			if(MyBitmap != null) {
				if(!MyBitmap.isRecycled()) {
					MyBitmap.recycle();
					MyBitmap = null;
				}
			}
			MyBitmap = PicZoom(bitmap, bitmap.getWidth() / scale,bitmap.getHeight() / scale);  
			if(bitmap != null) {
				if(!bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
				}
			}
		} catch (Exception e) {
			AllUse.info(getApplication(), "图片加载失败!");
			return;
		} catch (OutOfMemoryError e) {
			AllUse.info(getApplication(), "图片太大,请换张图片！");
			return;
		} catch (Error e) {
			AllUse.info(getApplication(), "图片太大，加载失败!");
			return;
	    } 
		
		iv_picture.setImageBitmap(MyBitmap);  
	}

	
	//返回一个绝对路径、作为照片的存储位置
  	public String getPicPath() {
  		//获得SD卡的路径
        final String SDpath = Environment .getExternalStorageDirectory().getAbsolutePath();
        File file = new File(SDpath +"/ingZone/eatWhat/");
        if(!file.exists()) {
        	// 创建文件夹
            file.mkdirs(); 
        }
        //照片的命名，目标文件夹下，以用户名加当前时间数字串为名称，即可确保每张照片名称不相同
        Date date = new Date();
        //获取当前时间并且进一步转化为字符串
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmSS", Locale.getDefault());
        String picName = format.format(date);
        String path = SDpath + "/ingZone/eatWhat/" + picName + ".jpg";
        return path;
  	}	
  	
  	//监听器监听到用户点击保存按钮，则调用该函数。分两种操作：添加（op=ADD) 和 修改（op=EDIT)
  	public void bt_save() {
		if(newFoodName.equals("")) {
			AllUse.info(getApplication(), "警告：菜名不能为空！");
			return;
		}	
		if(newFoodName.length() > 18) {
			AllUse.info(getApplication(), "警告：菜名太长！");
			return;
		}		
	
		switch(op) {
		case ADD:
			add();
			break;
		case EDIT:
			edit();
			break;
		default:
			break;
		}
			
  	}
  	
  	//添加操作（菜名已经输入）
  	private void add() {
  		Food food = new Food();
		AllUse.editFoodNum(AddFoodActivity.this, 1);
		food.setId(AllUse.getFoodNum(this));	
		food.setName(newFoodName);
		
  		if(haveInsertedPic) {			//如果插入了图片
  			//获得图片存储的绝对路径，并存储图片到对象
  			picPath = this.getPicPath();
  			AllUse.savePicture(MyBitmap, picPath);	 			
			food.setPicPath(picPath);
  		} else {									//否则存储的是默认图片的路径
  			food.setPicPath(Food.getDefaultPicPath(this));
  		}
  		
  		dbManager.insertFood(food);
  		AddFoodActivity.this.finish();	
  	}
  	
  	//修改操作（菜名与图片必须修改一个)
  	private void edit() {
  		if(newFoodName.equalsIgnoreCase(oldFoodName) && !haveInsertedPic) {	//两者都没修改
  			AllUse.info(getApplication(), "请修改菜单");
  			return;
  		}
  		if(newFoodName.equals(oldFoodName)) {															//修改了图片	
  			dbManager.updateFoodPic(getIntent.getStringExtra("foodname"), picPath);		
		} else {																														//修改了菜名
			dbManager.updateFoodName(getIntent.getStringExtra("foodname"), newFoodName);
		}
  		AddFoodActivity.this.finish();	
  	}
  	
  	//处理返回键，防止用户在上传过程中按返回键
  	@Override
  	public void onBackPressed() {
  		AddFoodActivity.this.finish();
  	}
  	
  	 public static int reckonThumbnail(int oldWidth, int oldHeight, int newWidth, int newHeight) {
         if ((oldHeight > newHeight && oldWidth > newWidth)
                 || (oldHeight <= newHeight && oldWidth > newWidth)) {
             int be = (int) (oldWidth / (float) newWidth);
             if (be <= 1)
                 be = 1;
             return be;
         } else if (oldHeight > newHeight && oldWidth <= newWidth) {
             int be = (int) (oldHeight / (float) newHeight);
             if (be <= 1)
                 be = 1;
             return be;
         }
         return 1;
     }
  
     public static Bitmap PicZoom(Bitmap bmp, int width, int height) {
         int bmpWidth = bmp.getWidth();
         int bmpHeght = bmp.getHeight();
         Matrix matrix = new Matrix();
         matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);

         return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
     }
     
     @Override
     public void onDestroy() {
    	 if(MyBitmap != null && !MyBitmap.isRecycled()) {
    		 MyBitmap.recycle();
    		 System.gc();
    	 }
    	 super.onDestroy();
     }
     
}