package com.moe.x4jdm.model;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;
import android.content.ContentValues;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Database extends SQLiteOpenHelper
{
	private SQLiteDatabase sql;
	private static Database mDatabase;
	private Database(Context c){
		super(c,"data",null,1);
		sql=getReadableDatabase();
	}
	public static Database getInstance(Context context){
		if(mDatabase==null){
			mDatabase=new Database(context);
		}
		return mDatabase;
	}
	@Override
	public void onCreate(SQLiteDatabase p1)
	{
		p1.execSQL("create table data(id_ integer primary key,url text,key text,src text,title text,desc text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase p1, int p2, int p3)
	{
	}
	public boolean query(String url){
		Cursor c=sql.query("data",new String[]{"id_"},"url=?",new String[]{url},null,null,null);
		boolean exist=c.getCount()>0;
		c.close();
		return exist;
	}
	public String query(){
		Cursor c=sql.query("data",new String[]{"url","key","src","title","desc"},null,null,null,null,null);
		JSONArray ja=new JSONArray();
		while(c.moveToNext()){
			JSONObject jo=new JSONObject();
			jo.put("href",c.getString(0));
			jo.put("src",c.getString(2));
			jo.put("key",c.getString(1));
			jo.put("title",c.getString(3));
			jo.put("desc",c.getString(4));
			ja.add(jo);
		}
		c.close();
		return ja.toJSONString();
	}
	public void insert(String url,String key){
		ContentValues cv=new ContentValues();
		cv.put("url",url);
		cv.put("key",key);
		sql.insertOrThrow("data",null,cv);
	}
	public void delete(String url){
		if(url==null)
			sql.delete("data",null,null);
			else
		sql.delete("data","url=?",new String[]{url});
	}
	public void update(String url,String src,String title,String desc){
		ContentValues cv=new ContentValues();
		cv.put("src",src);
		cv.put("title",title);
		cv.put("desc",desc);
		sql.update("data",cv,"url=?",new String[]{url});
	}
}
