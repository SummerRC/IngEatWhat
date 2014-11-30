package com.ing.eatwhat.entity;

import java.io.File;
import java.io.IOException;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.ing.eatwhat.R;

public class Food  {
	private int id;
	private String name;
	private String picPath;
	private final  static String DEFAULT_PIC_PATH = Environment .getExternalStorageDirectory().getAbsolutePath() + "/ingZone/eatWhat/defaultPic.jpg";
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPicPath() {
		return picPath;
	}
	
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	
	
	//返回默认食物图片的路径，如果文件不存在则创建之
	public static String getDefaultPicPath(Context context) {	
		File file = new File(DEFAULT_PIC_PATH);
		if(!file.exists()) {
			try {
				//保存默认图片到指定路径
				file.createNewFile();
				Log.e("d", DEFAULT_PIC_PATH);
				AllUse.savePicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.pic_default), DEFAULT_PIC_PATH);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return DEFAULT_PIC_PATH;
	}
	
}
