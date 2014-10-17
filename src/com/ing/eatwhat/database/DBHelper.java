/**
 * ���ܣ����ݿ�Ĵ����͸���
 */

package com.ing.eatwhat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private String food_sql;		//��food��ִ�е�SQL���
	private String forum_sql;		//��forum��ִ�е�SQL���
	
	//�ù��캯��4������,ֱ�ӵ��ø���Ĺ��캯��.���е�һ������Ϊ�����ġ��ڶ�������Ϊ���ݿ�����֡���3�����������������α����ģ�����һ������Ϊnull�������������ݿ�İ汾��.
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);		
	}

	//�ú��������ݿ��һ�α�����ʱ����
	@Override
	public void onCreate(SQLiteDatabase db) {
		//����food�������ֶΣ�ʳ���id��ʳ������ʳ��ͼƬ�洢��SD���ľ���·����
		food_sql = "create table if not exists food(id int, name varchar(20), picPath varchar(50))";
		db.execSQL(food_sql);
		
		//����forum���ĸ��ֶΣ����ӵ�id�����ӷ���ʱ�䡢�������ӵ��û������������ݣ�
		forum_sql = "create table if not exists forum(id int, date date(50), userName varchar(50), content varchar(50))";
		db.execSQL(forum_sql);
	}

	//�����ǰ��������ݿ�汾�ű���һ�δ����İ汾�ߣ�SQLiteOpenHelper�ͻ����onUpgrade()����
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//	do  nothing
	}

}
