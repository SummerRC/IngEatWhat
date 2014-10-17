/**
 * 功能：管理数据库的类.对数据的插入删除等操作进行了封装、并提供一个对外的接口.
 */

package com.ing.eatwhat.database;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

	//删除：根据食物名删除一条记录
	public void delete(String foodName) {
        db = getWritableDB();
        db.delete("food", "name=?", new String[]{foodName});
        close();
	}
	
	//更新：更新食物名
	public void updateFoodName(String foodName, String newFoodName) {	
		 db = getWritableDB();
         ContentValues values = new ContentValues();
         values.put("name", newFoodName);
         db.update("food", values, "name=?", new String[]{foodName});
         close();
	}
	
	//更新：更新照片的储存地址
	public void updateFoodPic(String foodName, String newPicPath) {	
		db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put("picPath", newPicPath);
        db.update("food", values, "name=?", new String[]{foodName});
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
	
	/********************************  对forum表的操作    *************************************************/
	/********************************  对forum表的操作    *************************************************/
	//插入：将从网上下载的帖子信息插入到forum表
	public void insertMessages(HashMap<String, ArrayList<String>> map) {
		db = this.getWritableDB();
		ContentValues cv = new ContentValues();
		ArrayList<String> date = map.get("date");
		ArrayList<String> userName = map.get("userName");
		ArrayList<String> content = map.get("content");
		for(int i=0; i<date.size(); i++) {
			cv.put("id", i);
			cv.put("date", date.get(i));
			cv.put("userName", userName.get(i));
			cv.put("content", content.get(i));
			db.insert("forum", null, cv);
			cv.clear();
		}
		close();
	}
	
	//查询：从数据库返回帖子信息、一次最多返回十条
	public HashMap<String, ArrayList<String>> getMessages() {
		//HashMap用于存储帖子信息
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> date = new ArrayList<String>();
		ArrayList<String> userName = new ArrayList<String>();	
		ArrayList<String> content = new ArrayList<String>();   
		//对forum表的查询操作
		db = this.getReadableDB();
		Cursor cursor = db.rawQuery("select * from forum", null);
		int count = 0;
		while(cursor.moveToNext() && count < 10) {
			date.add(cursor.getString(cursor.getColumnIndex("date")));
			userName.add(cursor.getString(cursor.getColumnIndex("userName")));
			content.add(cursor.getString(cursor.getColumnIndex("content")));
			count++;
		}
		close();
		//将查询结果存储到map中，并发送出去
		map.put("date", date);
		map.put("userName", userName);
		map.put("content", content);
		//map.put("id", id);
		return map;
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
