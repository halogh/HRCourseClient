package com.zhuang.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zhuang.application.CourseApplication;
import com.zhuang.application.LogUtil;
import com.zhuang.model.MySubject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.zhuang.model.SubjectRepertory.getWeekList;

/**数据库增删查
 * Created by zhuang on 2019/3/29.
 */

public class DaoImpl {
    //保存数据
    public static boolean  SaveCourceData(List<MySubject> subject){
       try{


           ContentValues values=null;
//           db.replace("course",null,values);
           DBHelper db=new DBHelper(CourseApplication.getContext());
           for (MySubject mySubject : subject) {
               values=new ContentValues();
//               values.put("id",mySubject.getId());
               values.put("term","2018");
               values.put("name",mySubject.getName());
               values.put("room",mySubject.getRoom());
               values.put("teacher",mySubject.getTeacher());
               values.put("weeks",mySubject.getTime());//time为无用数据，暂时存放周数
               values.put("start",mySubject.getStart());
               values.put("step",mySubject.getStep());
               values.put("day",mySubject.getDay());
               values.put("number",mySubject.getNumber());


               db.insert(values);
           }

           return true;
       }catch(Exception e) {
           LogUtil.e("DaoimplError",e.getMessage());
           return false;
       }
    }

    //得到课表数据
    public static List<MySubject> GetCourceData(){
        try{
            DBHelper cource=new DBHelper(CourseApplication.getContext());
            Cursor cursor=cource.query();
            List<MySubject> subjects = new ArrayList<>();
            if(cursor.moveToFirst()){
                do {
                    String term=cursor.getString(1);
                    String name=cursor.getString(2);
                    String room=cursor.getString(3);
                    String teacher=cursor.getString(4);
                    String weeks=cursor.getString(5);
                    int start=cursor.getInt(6);
                    int step=cursor.getInt(7);
                    int day=cursor.getInt(8);
                    String number = cursor.getString(9);

                    subjects.add(new MySubject(term,name, room, teacher, getWeekList(weeks), start, step, day, -1,null,number));
                }while (cursor.moveToNext());
                cursor.close();
                return subjects;
            }else{
                cursor.close();
                return null;
            }
        }catch(Exception e){
            e.getStackTrace();
            return null;
        }
    }

    //清除课表
    public static boolean ClearCource(){
        try{
            DBHelper db=new DBHelper(CourseApplication.getContext());
            db.del();
            return true;
        }catch(Exception e){
            e.getStackTrace();
            return false;
        }
    }



}
