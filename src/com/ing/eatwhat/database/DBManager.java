/**
 * ���ܣ��������ݿ����.�����ݵĲ���ɾ���Ȳ��������˷�װ�����ṩһ������Ľӿ�.
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
	
	//���캯���������ݿ⣬������
	public DBManager(Context ctx) {
		context = ctx;
		db = this.getReadableDB();		//��ʼ��һ��DBHelperʱ��������ݿⲻ���ڣ�������֮
		close();						//�ͷ���Դ
	}
	
	/************************************  ��food��Ĳ���     **********************************/
	/************************************  ��food��Ĳ���     **********************************/
	//���룺����һ��food��Ϣ
	public void insertFood(Food food) {
		ContentValues cv = new ContentValues();
		cv.put("id", food.getId());
		cv.put("name", food.getName());
		cv.put("picPath", food.getPicPath());
		db = this.getWritableDB();
		db.insert("food", null, cv);
		close();
	}

	//ɾ��������ʳ����ɾ��һ����¼
	public void delete(String foodName) {
        db = getWritableDB();
        db.delete("food", "name=?", new String[]{foodName});
        close();
	}
	
	//���£�����ʳ����
	public void updateFoodName(String foodName, String newFoodName) {	
		 db = getWritableDB();
         ContentValues values = new ContentValues();
         values.put("name", newFoodName);
         db.update("food", values, "name=?", new String[]{foodName});
         close();
	}
	
	//���£�������Ƭ�Ĵ����ַ
	public void updateFoodPic(String foodName, String newPicPath) {	
		db = getWritableDB();
        ContentValues values = new ContentValues();
        values.put("picPath", newPicPath);
        db.update("food", values, "name=?", new String[]{foodName});
        close();
	}
	
	//��ѯ������food����Ϣ��ֵ��HashMap<String, ArrayList<String>>����
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
	
	//��ѯ������ʳ������ѯ��Ӧ��һ����¼
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
	
	/********************************  ��forum��Ĳ���    *************************************************/
	/********************************  ��forum��Ĳ���    *************************************************/
	//���룺�����������ص�������Ϣ���뵽forum��
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
	
	//��ѯ�������ݿⷵ��������Ϣ��һ����෵��ʮ��
	public HashMap<String, ArrayList<String>> getMessages() {
		//HashMap���ڴ洢������Ϣ
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> date = new ArrayList<String>();
		ArrayList<String> userName = new ArrayList<String>();	
		ArrayList<String> content = new ArrayList<String>();   
		//��forum��Ĳ�ѯ����
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
		//����ѯ����洢��map�У������ͳ�ȥ
		map.put("date", date);
		map.put("userName", userName);
		map.put("content", content);
		//map.put("id", id);
		return map;
	}
	
	//����һ��ReadableDatabase���͵����ݿ�
	private SQLiteDatabase getReadableDB() {
		if(dbHelper == null) {
			dbHelper = new DBHelper(context, User.userName + "eatwhat.db", null, 1);			
		}
		return dbHelper.getReadableDatabase();
	}
	
	//����һ��WritableDatabase���͵����ݿ�
	private SQLiteDatabase getWritableDB() {
		if(dbHelper == null) {
			dbHelper = new DBHelper(context, User.userName + "eatwhat.db", null, 1);			
		}
		return dbHelper.getWritableDatabase();
	}
	
	//�ر����ݿ⡢�α����Դ
	private void close() {
		if(db != null) {
			/*db.endTransaction();		//4.0�Ժ�İ汾��Ϊ��ȫ�Ե����⣬���������endTransactiony�Ժ�����ٴη��ʱ������ݿ�
			*/db.close();				//4.0��ǰ�İ汾��db.close()֮ǰ�����ʵ��
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
