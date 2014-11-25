/**
 * 功能：管理数据库的类.对数据的插入删除等操作进行了封装、并提供一个对外的接口.
 */

package com.ing.eatwhat.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.ing.eatwhat.entity.AllUse;
import com.ing.eatwhat.entity.Food;
import com.ing.eatwhat.entity.User;

public class DBManager {
	private Context context;
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;
	
	//构造函数创建数据库，并建表
	public DBManager(Context ctx) {
		context = ctx;
		db = this.getReadableDB();		//初始化一个DBHelper时，如果数据库不存在，将创建之
		close();						//释放资源
	}
	
	/************************************  对food表的操作     **********************************/
	/************************************  对food表的操作     **********************************/
	//插入：插入一条food信息
	public void insertFood(Food food) {
		ContentValues cv = new ContentValues();
		cv.put("id", food.getId());
		cv.put("name", food.getName());
		cv.put("picPath", food.getPicPath());
		db = this.getWritableDB();
		db.insert("food", null, cv);
		close();
	}

	//当注册账号成功后，将游客账号对应的food信息插入到新账号
	public void insertAll(HashMap<String, ArrayList<String>> map) {
		db = this.getWritableDB();
		ArrayList<String> arr_name = map.get("name");
		ArrayList<String> arr_picPath = map.get("picPath");
		for(int i=0; i<arr_name.size(); i++) {
			ContentValues cv = new ContentValues();
			cv.put("id", i);
			cv.put("name", arr_name.get(i));
			cv.put("picPath", arr_picPath.get(i));
			db.insert("food", null, cv);
		}
		db.close(); 
	}
	
	//删除：根据食物名删除一条记录
	public void delete(String picPath) {
        db = getWritableDB();
        db.delete("food", "picPath=?", new String[]{picPath});
        close();
	}
	
	//更新：更新食物名
	public void updateFoodName(String picPath, String newFoodName) {	
		 db = getWritableDB();
         ContentValues values = new ContentValues();
         values.put("name", newFoodName);
         db.update("food", values, "picPath=?", new String[]{picPath});
         close();
	}
	
	//更新：更新照片的储存地址
	public void updateFoodPic(String picPath, String newPicPath) {	
		db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put("picPath", newPicPath);
        db.update("food", values, "picPath=?", new String[]{picPath});
        close();
	}
	
	//查询：返回food表信息、值是HashMap<String, ArrayList<String>>类型
	public HashMap<String, ArrayList<String>> getAllFood() {
		db = this.getReadableDB();
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();        
        ArrayList<String> arr_name = new ArrayList<String>(); 
        ArrayList<String> arr_picPath = new ArrayList<String>(); 
        
        String sql = "select * from food";
        cursor =  db.rawQuery(sql, null);
        while(cursor.moveToNext()) {
        	String name = cursor.getString(cursor.getColumnIndex("name"));       
            String picPath = cursor.getString(cursor.getColumnIndex("picPath"));       
            arr_name.add(name);
            arr_picPath.add(picPath);
        }
        map.put("name", arr_name);
        map.put("picPath", arr_picPath);
        close();
		return map;       
	}
	
	//查询：根据食物名查询对应的一条记录
	public Food query(String foodName) {
		Food food = new Food();
		db = this.getReadableDB();
        Cursor cursor = db.rawQuery("select * from food where name=?",new String[]{foodName});
        if(cursor.moveToNext()) {
             String picPath = cursor.getString(cursor.getColumnIndex("picPath"));
             food.setPicPath(picPath);
        }
        close();
		return food;      
	 }
	
	//查询：随即返回一个Food对象
	public Food randomFood() {
		Food food = new Food();
		db = this.getReadableDB();
	    int count = AllUse.getFoodNum(context);
	    Random random = new Random();
        Cursor cursor = db.rawQuery("select * from food ", null);
        if(cursor.moveToPosition(random.nextInt(count))) {
        	String picPath = cursor.getString(cursor.getColumnIndex("picPath"));
            food.setPicPath(picPath);
            String name = cursor.getString(cursor.getColumnIndex("name"));
            food.setName(name);
        }
		return food;
	}
	
	//返回一个ReadableDatabase类型的数据库
	private SQLiteDatabase getReadableDB() {
		if(dbHelper == null) {
			dbHelper = new DBHelper(context, User.userName + "eatwhat.db", null, 1);			
		}
		return dbHelper.getReadableDatabase();
	}
	
	//返回一个WritableDatabase类型的数据库
	private SQLiteDatabase getWritableDB() {
		if(dbHelper == null) {
			dbHelper = new DBHelper(context, User.userName + "eatwhat.db", null, 1);			
		}
		return dbHelper.getWritableDatabase();
	}
	
	//关闭数据库、游标等资源
	private void close() {
		if(db != null) {
			/*db.endTransaction();		//4.0以后的版本因为安全性的问题，必须结束即endTransactiony以后才能再次访问本地数据库
			*/db.close();				//4.0以前的版本在db.close()之前会结束实物
		}
		if(cursor != null) {
			cursor.close();
		}	
		if(dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
		}
	}

}
