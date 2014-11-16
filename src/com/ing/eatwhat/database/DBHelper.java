/**
 * 功能：数据库的创建和更新
 */

package com.ing.eatwhat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private String food_sql;		//建food表执行的SQL语句
	
	//该构造函数4个参数,直接调用父类的构造函数.其中第一个参数为上下文、第二个参数为数据库的名字、第3个参数是用来设置游标对象的，这里一般设置为null、参数四是数据库的版本号.
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);		
	}

	//该函数在数据库第一次被建立时调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		//建立food表（三个字段：食物的id、食物名、食物图片存储在SD卡的绝对路径）
		food_sql = "create table if not exists food(id int, name varchar(20), picPath varchar(50))";
		db.execSQL(food_sql);
		
	}

	//如果当前传入的数据库版本号比上一次创建的版本高，SQLiteOpenHelper就会调用onUpgrade()方法
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//	do  nothing
	}

}
