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
	
	private Button bt_addfood_save;				//���水ť
	private Button bt_addfood_back;
	private static EditText et_foodname;		//��������ĵ�EditText
	private static ImageView iv_food;			//��ʾͼƬ��ImageView
	private static TextView tv_addfood_hint;	//�ϴ�������ʾ��TextView
	private Intent getIntent;
	private final int CAMERA = 1;				//�ص������ı�ʾ
	private final int PHOTO_ALBUM = 2;			//�ص������ı�ʾ
	private Uri picUri;  						//��λͼƬ��uri
	private String picPath;						//�������������ȡ��ͼƬ���������洢��λ��
	private String picName; 					//ͼƬ�������غ���������һ�£�
	private String oldFoodName;					//�ɵĲ���
	private String newFoodName;					//�µĲ���
	private boolean uploadPicture = false;		//��һ������ֵ��¼�ϴ�״̬
	private boolean isChanged = false;			//��һ������ֵ��¼�Ƿ��޸Ĺ�
	private String token;						//�ϴ����ص�ƾ֤
	private Handler mHandler;
	private DBManager dbManager;				//���ݿ�������ʵ��
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfood);	
		
		init();			
		if(getIntent.getStringExtra("op").compareToIgnoreCase("edit") == 0) {
			bt_addfood_save.setText("�޸�");
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
						AllUse.info(AddFoodActivity.this, "���棺����˵�ʧ�ܣ������ظ���");
						tv_addfood_hint.setText("����ʧ�ܣ������ظ���");
						clean();
						et_foodname.setText(oldFoodName);
						break;
					case 1:     //��ӳɹ�
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
						AllUse.info(AddFoodActivity.this, "���棺ԭ���������ڣ�");
						tv_addfood_hint.setText("����ʧ�ܣ�ԭ���������ڣ�");
						clean();
						et_foodname.setText(oldFoodName);
						break;
					case 5:     //�޸ĳɹ�
						dbManager.updateFoodPic(getIntent.getStringExtra("foodname"), picPath);				
						dbManager.updateFoodName(getIntent.getStringExtra("foodname"), newFoodName);
						clean();
						AddFoodActivity.this.finish();
						break;
					case 6:
						AllUse.info(AddFoodActivity.this, "�ò����Ѿ����ڣ�");
						tv_addfood_hint.setText("�ò������ڣ�");
						clean();
						et_foodname.setText(oldFoodName);
						break;
					}		
					break;
				case 1:
					//�������ص�ͼƬ
					break;
				case 2:
					//�ϴ�ƾ֤
					token = msg.obj.toString().trim();
					break;
				case 222:
					AllUse.info(AddFoodActivity.this, "ʳ�������ܰ��������ַ���? | & �ո��.");
					tv_addfood_hint.setText("ʳ�������ܰ��������ַ���? | & �ո��.");
					clean();
					break;
				}
			}			
		};
	}
	
	//��ʼ��
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
			bt_addfood_save.setText("�޸�");
			oldFoodName = getIntent.getStringExtra("foodname");
			et_foodname.setText(oldFoodName);			
			iv_food.setImageURI(Uri.fromFile(new File(dbManager.query(oldFoodName).getPicPath())));
		}
	}
		
	//���水ť��ȡ����ť��ImageView�ļ�����
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.bt_addfood_back:				//���ذ�ť
			if(uploadPicture) {
				AllUse.info(AddFoodActivity.this, "�����ϴ�ͼƬ�����Ժ�");
				return;
			}
			AddFoodActivity.this.finish();
			break;
		case R.id.bt_addfood_save:				//���水ť
			newFoodName = et_foodname.getText().toString();
			bt_save();
			break;
		case R.id.iv_food:						//���ImageView
			dialogClick(view);
			break;
		}		
	}	
		
	//��ʾѡ������������
	public void dialogClick(View view) {
		// ���sd�Ƿ����
		String SDStatus = Environment.getExternalStorageState();
	    if (!SDStatus.equals(Environment.MEDIA_MOUNTED)) { 
	    	AllUse.info(AddFoodActivity.this, "���棺SD�������ã�");
	        return;
	    }
		//Ϊ����ϴ�ƾ֤����һ���߳�
		NetThread tokenThread = new NetThread(mHandler, null, null, null, null, 6);
		tokenThread.start();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("��ʾ")
			   .setMessage("��ѡ�����ͼƬ��ʽ��")
			   .setCancelable(true)           //�����ؼ������˳� 
			   .setNegativeButton("���", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   //�������
					   Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			           startActivityForResult(camera, CAMERA);   	//CAMERA�Ǹ���ǣ����ݱ�����ĸ�Intent����Ļص�����			           
				   }
			   })  
			   .setNeutralButton("���", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   //����ϵͳ���
					   Intent picture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//����android��ͼ�� 
					   startActivityForResult(picture, PHOTO_ALBUM); 		//PHOTO_ALBUM�Ǹ���ǣ����ݱ�����ĸ�Intent����Ļص�����			
					   }
			   })	   
			   .setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int id) {
					   dialog.dismiss();
				   }
			   }).show();	 
	}
	
	//�ص�����
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isChanged = true;
        if(!(resultCode == Activity.RESULT_OK)) {
	    	AllUse.info(AddFoodActivity.this, "����ʧ�ܣ������ԣ�");
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
	  
	//���ѡ����Ƭ�󣬻ص���������������������ͼƬ
	private void photoAlbum(int resultCode, Intent data) {
		if(data == null || resultCode != RESULT_OK) {
	    	AllUse.info(AddFoodActivity.this, "δѡ���κ�ͼƬ!");
	    	return;
	    }
		
		picUri = data.getData();		//��λ��ͼƬλ�ã��ϴ�����ԭͼ
	    String[] filePathColumn = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getContentResolver().query(picUri, filePathColumn, null, null, null);
	    cursor.moveToFirst();
	    int columnIndex = cursor.getColumnIndex(filePathColumn[0]); 
	    String picturePath = cursor.getString(columnIndex);
	    cursor.close();
	    Bitmap bitmap = null;        
	   
		try {
			BitmapFactory.Options options = new BitmapFactory.Options(); 
	        options.inSampleSize = 2;//ͼƬ��߶�Ϊԭ���Ķ���֮һ����ͼƬΪԭ�����ķ�֮һ 
	        bitmap = BitmapFactory.decodeFile(picturePath, options);   	
		} catch (Exception e) {
			e.printStackTrace();
			AllUse.info(AddFoodActivity.this, "ͼƬ����ʧ��!");
			return;
		} catch (OutOfMemoryError e) {
			AllUse.info(AddFoodActivity.this, "ͼƬ̫��,�뻻��ͼƬ��");
			return;
		} catch (Error e) {
			e.printStackTrace();
			AllUse.info(AddFoodActivity.this, "ͼƬ̫���ڴ����!");
			return;
	    } 
		//��ͼƬ��ʾ��ImageView
	    iv_food.setImageBitmap(bitmap);  
	    //���ͼƬ�洢�ľ���·�������洢ͼƬ
		picPath = this.getPicPath();
		AllUse.savePicture(bitmap, picPath);		
	}

	//������պ�ص������������������������������Ƭ
	private void camera(int resultCode, Intent data) {
		if(data == null || resultCode != RESULT_OK) {
	    	AllUse.info(AddFoodActivity.this, "����ȡ����");
	    	return;
	    }
		// ��ȡ������ص����ݣ���ת��ΪBitmapͼƬ��ʽ
        Bundle bundle = data.getExtras();  
        Bitmap bitmap = (Bitmap) bundle.get("data");		
		if (data.getData() != null) {  
            picUri = data.getData();  
        } else {  
            picUri  = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));      
        }       
        //��ͼƬ��͸߾�ѹ����100
        Bitmap rbitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);  
        //��ͼƬ��ʾ��ImageView
        iv_food.setImageBitmap(rbitmap);
        //���ͼƬ�洢�ľ���·�������洢ͼƬ
        picPath = this.getPicPath();
		AllUse.savePicture(rbitmap, picPath);	
	}
	
	//�ϴ�ͼƬ����ţ�ƴ洢���õ���UI�߳�
	public void uploadPicture() {
        uploadPicture = true;
		PutExtra extra = new PutExtra();
		extra.params = new HashMap<String, String>();
		extra.params.put("x:a", "����������Ϣ");
		tv_addfood_hint.setText("�ϴ���...");
		IO.putFile(AddFoodActivity.this, token, picName, Uri.fromFile(new File(picPath)), extra, new JSONObjectRet() {
			@Override
			public void onProcess(long current, long total) {
				tv_addfood_hint.setText("���ϴ�:" + current + "/" + "����" + total);
			}

			@Override
			public void onSuccess(JSONObject resp) {
				tv_addfood_hint.setText("�ϴ��ɹ���");
				//����ϴ��ɹ���ֻ�б�������޸ĳɹ��Ż��ϴ�������ô���뵽���ݿ���߸������ݿ�
				if(getIntent.getStringExtra("op").compareToIgnoreCase("save") == 0) {
					//Ϊ�洢�û��Ĳ�������һ���߳�
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
				tv_addfood_hint.setText("����: "+ ex.getMessage());	
				String str = "{\"error\"" + ":" + "\"file exists\"}";
				if(ex.getMessage().toString().equals(str)) {
					AllUse.info(getApplicationContext(), "�����ظ������ʧ�ܣ�");
					clean();
					et_foodname.setText(oldFoodName);
					return;
				} else {			
					AllUse.info(AddFoodActivity.this, "�ף����粻�������Ժ����԰�!" + ex.getMessage());
					clean();
					AddFoodActivity.this.finish();
				}
				
			}
		});	
	}
	
	//����һ������·������Ϊ��Ƭ�Ĵ洢λ��
  	public String getPicPath() {
  		//���SD����·��
        final String SDpath = Environment .getExternalStorageDirectory().getAbsolutePath();
        File file = new File(SDpath +"/ingZone/eatWhat/");
        // �����ļ���
        file.mkdirs(); 
        //��Ƭ��������Ŀ���ļ����£����û����ӵ�ǰʱ�����ִ�Ϊ���ƣ�����ȷ��ÿ����Ƭ���Ʋ���ͬ
        Date date = new Date();
        //��ȡ��ǰʱ�䲢�ҽ�һ��ת��Ϊ�ַ���
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmSS", Locale.getDefault());
        picName = User.userName + format.format(date);
        String path = SDpath + "/ingZone/eatWhat/" + picName + ".jpg";
        return path;
  	}	
  	
  	//�������������û�������水ť������øú���
  	public void bt_save() {
  		//�ж��Ƿ�����
		if(!AllUse.isHaveInternet(this)){
			AllUse.info(AddFoodActivity.this, "δ��������,����ʧ��");
			return;
		}
		if(uploadPicture) {
			AllUse.info(AddFoodActivity.this, "�����ϴ�ͼƬ�����Ժ�");
			return;
		}	
		if(newFoodName.equals("")) {
			AllUse.info(AddFoodActivity.this, "���棺��������Ϊ�գ�");
			return;
		}	
		if(newFoodName.length() > 18) {
			AllUse.info(AddFoodActivity.this, "���棺����̫����");
			return;
		}						
		if(getIntent.getStringExtra("op").compareToIgnoreCase("edit") == 0){		
			//�ж�ʳ�����Ͷ�ӦͼƬ��û�б��޸Ļ����
			if(isChanged == false) {
				AllUse.info(AddFoodActivity.this, "���޸�ͼƬ��");
				return;
			}
			if(newFoodName.equals(oldFoodName)) {
				AllUse.info(AddFoodActivity.this, "���޸Ĳ�����");
				return;
			}
			//����imagebutton��edittext�����ɵ��
			et_foodname.setEnabled(false);
			iv_food.setClickable(false);
			//�ϴ���Ƭ����ţ�ƴ洢
			uploadPicture();	
		} else 
			if(getIntent.getStringExtra("op").compareToIgnoreCase("save") == 0) {			
				if(isChanged == false) {
					AllUse.info(AddFoodActivity.this, "�����ͼƬ��");
					return;
				}			
				//����imagebutton��edittext�����ɵ��
				et_foodname.setEnabled(false);
				iv_food.setClickable(false);	
				//�ϴ���Ƭ����ţ�ƴ洢
				uploadPicture();						
			} else {
				AllUse.info(AddFoodActivity.this, "����ʧ�ܣ�");
				return;
			}
		
  		
  	}
  	
  	//�����ؼ�����ֹ�û����ϴ������а����ؼ�
  	@Override
  	public void onBackPressed() {
  		if(uploadPicture) {       
  			AllUse.info(AddFoodActivity.this, "�����ϴ�ͼƬ�����Ժ����");
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