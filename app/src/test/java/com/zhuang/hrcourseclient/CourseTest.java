package com.zhuang.hrcourseclient;

import com.zhuang.utils.CalendarUtil;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuang on 2019/3/28.
 */

public class CourseTest {

    @Test
    public void testWeek(){
        int weekResult = -1;
        int startWeek = 4;//，起始周从SharedPreferences获取
        if(startWeek<=17){
            String startDay="2019-03-25";//起始周的星期一，从SharedPreferences获取
            String endDay= "2019-04-07";
//            String endDay= CalendarUtil.getMondayOfWeek();
            System.out.println(endDay);
            int tempweek=CalendarUtil.getTwoDay(endDay,startDay);
            int week=tempweek/7;
            if(week==0){
                weekResult=startWeek;
            }else{
                int finalweek=startWeek+week;
                if(finalweek>17){
                    weekResult=17;
                }else{
                    weekResult=finalweek;
                }
            }
        }else{
            weekResult=17;
        }
        System.out.println(weekResult);
    }

    @Test
    public void testgetMonday(){
        System.out.println(CalendarUtil.getMondayOfWeek());
        System.out.println(CalendarUtil.getMondayOfWeek2());
//
//        cal.add(Calendar.DATE, 6);
//        cal.set(Calendar.HOUR, 23);
//        cal.set(Calendar.MINUTE, 59);
//        cal.set(Calendar.SECOND, 59);
//
//        String imptimeEnd = sdf.format(cal.getTime());
//        Date sundayDate = cal.getTime();
//        System.out.println("所在周星期日的日期：" + imptimeEnd);
//
//        DateFormat datetimeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println("星期一的开始："+datetimeDf.format(mondayDate));
//        System.out.println("星期天的结束："+datetimeDf.format(sundayDate));
    }

}
