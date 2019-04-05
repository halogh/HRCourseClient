package com.zhuang.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite帮助类
 * Created by zhuang on 2019/3/29.
 */
public class DBHelper extends SQLiteOpenHelper {
    //数据库名称
    private static final String DB_NAME="course.db";
    //表名
    private static final String TBL_NAME="courseTbl";
    //声明SQLiteDatabase对象
    private SQLiteDatabase db;
    public DBHelper(Context c) {
        super(c, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //获取SQLiteDatabase对象
        this.db=db;
        //创建表
        String CREATE_TBL= "create table courseTbl(" +
                "id integer primary key autoincrement,"+
                "term text,"+
                "name text,"+
                "room text,"+
                "teacher text,"+
                "weeks text,"+
                "start integer,"+
                "step integer,"+
                "day integer,"+
                "number text"+
                ")";
        db.execSQL(CREATE_TBL);
    }
    //插入
    public void insert(ContentValues values){
        SQLiteDatabase db=getWritableDatabase();
        db.insert(TBL_NAME,null,values);
        db.close();
    }

    //查询
    public Cursor query(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.query(TBL_NAME,null,null,null,null,null,null);
        return  c;
    }

    //删除
    public void del(){
        if(db==null)
            db=getWritableDatabase();
        db.delete(TBL_NAME,null,null);
    }

    //关闭数据库
    public void close(){
        if(db !=null)
            db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
