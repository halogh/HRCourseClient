package com.zhuang.hrcourseclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuang.dao.DaoImpl;
import com.zhuang.model.MySubject;
import com.zhuang.model.SubjectRepertory;
import com.zhuang.spider.HREduSystemSpider;
import com.zhuang.utils.CalendarUtil;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.OnItemBuildAdapter;
import com.zhuangfei.timetable.view.WeekView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.listener.OnSpaceItemClickAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 课表Activity
 * Created by zhuang on 2019/3/27.
 */

public class BaseFuncActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "BaseFuncActivity";

    //控件
    TimetableView mTimetableView;
    WeekView mWeekView;

    Button moreButton;
    LinearLayout layout;
    TextView titleTextView;
    List<MySubject> mySubjects;

    //记录切换的周次，不一定是当前周
    int target = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_func);

        moreButton = (Button) findViewById(R.id.id_more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopmenu();
            }
        });
        //数据源
//        mySubjects = SubjectRepertory.loadDefaultSubjects2();
        //获取本地数据库数据
        mySubjects = getLocalSubjectData();
        //mySubjects.addAll(SubjectRepertory.loadDefaultSubjects());

        titleTextView = (TextView) findViewById(R.id.id_title);
        layout = (LinearLayout) findViewById(R.id.id_layout);
        layout.setOnClickListener(this);
        initTimetableView();//初始化
        setMonthWidth();//设置月份宽度
//        showTime();//显示时间
        buildItemText();//设置课程文本内容
    }

    //获取本地数据库数据
    private List<MySubject> getLocalSubjectData() {
        List<MySubject> data = DaoImpl.GetCourceData();
        if(!GetSaveStatus()){
            DaoImpl.ClearCource();
        }
        return data;
    }
    //判断登录状态
    private boolean GetSaveStatus() {
        SharedPreferences Preferences=getSharedPreferences("userInfo",MODE_PRIVATE);
        String username=Preferences.getString("username","");
        String password=Preferences.getString("password","");
        if(username.equals("")&&password.equals("")){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 初始化课程控件
     */
    private void initTimetableView() {
        //获取控件
        mWeekView = (WeekView) findViewById(R.id.id_weekview);
        mTimetableView = (TimetableView) findViewById(R.id.id_timetableView);

        //设置周次选择属性
        mWeekView.source(mySubjects)
                .curWeek(getLocalWeek())
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //更新切换后的日期，从当前周cur->切换的周week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week);
                        mTimetableView.changeWeekOnly(week);
                    }
                })
                .callback(new IWeekView.OnWeekLeftClickedListener() {
                    @Override
                    public void onWeekLeftClicked() {
                        onWeekLeftLayoutClicked();
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();

        mTimetableView.source(mySubjects)
                .curWeek(getLocalWeek())
                .curTerm("大三下学期")
                .maxSlideItem(16)
                .monthWidthDp(30)
                //透明度
                //日期栏0.1f、侧边栏0.1f，周次选择栏0.6f
                //透明度范围为0->1，0为全透明，1为不透明
//                .alpha(0.1f, 0.1f, 0.6f)
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        display(scheduleList);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start) {
                        Toast.makeText(BaseFuncActivity.this,
                                "长按:周" + day  + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
                        titleTextView.setText("第" + curWeek + "周");
                    }
                })
                //旗标布局点击监听
                .callback(new ISchedule.OnFlaglayoutClickListener() {
                    @Override
                    public void onFlaglayoutClick(int day, int start) {
                        mTimetableView.hideFlaglayout();
                        Toast.makeText(BaseFuncActivity.this,
                                "点击了旗标:周" + (day + 1) + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .showView();
    }
    /*
    获取周数
    * */
    private int getLocalWeek() {
        int weekResult = -1;
        SharedPreferences preferences=getSharedPreferences("userInfo",MODE_PRIVATE);

        int startWeek = preferences.getInt("weekNumber",-1);//，起始周从SharedPreferences获取
        //华软教务系统总共17周
        if(startWeek<=17){
            String startDay=preferences.getString("date","");//起始周的星期一，从SharedPreferences获取，为yyyy-MM-dd格式
//            String endDay= CalendarUtil.getMondayOfWeek();//当前周的星期一，为yyyy-MM-dd格式
            String endDay= CalendarUtil.getMondayOfWeek2();//当前周的星期一，为yyyy-MM-dd格式
            SharedPreferences.Editor editor=getSharedPreferences("userInfo",MODE_PRIVATE).edit();
            editor.putString("current", endDay);
            editor.commit();
            int tempweek=CalendarUtil.getTwoDay(endDay,startDay);//间隔天数
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
            return weekResult;
        }else{
            weekResult=17;
            return weekResult;
        }


    }

    /**
     * 周次选择布局的左侧被点击时回调<br/>
     * 对话框修改当前周次
     */
    protected void onWeekLeftLayoutClicked() {
        final String items[] = new String[20];
        int itemCount = mWeekView.itemCount();
        for (int i = 0; i < itemCount; i++) {
            items[i] = "第" + (i + 1) + "周";
        }
        target = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置当前周");
        builder.setSingleChoiceItems(items, mTimetableView.curWeek() - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        target = i;

                    }
                });
        builder.setPositiveButton("设置为当前周", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (target != -1) {
                    mWeekView.curWeek(target + 1).updateView();
                    mTimetableView.changeWeekForce(target + 1);
                    //把修改的周次存入SharedPreferences中
                    SharedPreferences.Editor editor=getSharedPreferences("userInfo",MODE_PRIVATE).edit();
                    editor.putInt("weekNumber",target+1);
//                    editor.putString("date", CalendarUtil.getMondayOfWeek());
                    editor.putString("date", CalendarUtil.getMondayOfWeek2());
                    editor.commit();

//                    Log.d("info",Integer.toString(target+1));
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 显示内容
     *
     * @param beans
     */
    protected void display(List<Schedule> beans) {
        String str = "";
        for (Schedule bean : beans) {
            str += bean.getName() + ","+bean.getWeekList().toString()+","+bean.getTeacher()+",["+bean.getExtras().get(MySubject.EXTRAS_NUMBER)+"]\n";
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    /**
     * 显示弹出菜单
     */
    private void showPopmenu() {
        PopupMenu popup = new PopupMenu(this, moreButton);
        popup.getMenuInflater().inflate(R.menu.popmenu_base_func, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    /*case R.id.top1:
                        addSubject();
                        break;
                    case R.id.top2:
                        deleteSubject();
                        break;*/

                    case R.id.top4:
                        hideNonThisWeek();
                        break;
                    case R.id.top5:
                        showNonThisWeek();
                        break;
                    /*case R.id.top6:
                        setMaxItem(8);
                        break;
                    case R.id.top7:
                        setMaxItem(10);
                        break;
                    case R.id.top8:
                        setMaxItem(12);
                        break;*/
                    case R.id.top9:
                        showTime();
                        break;
                    case R.id.top10:
                        hideTime();
                        break;
                    case R.id.top11:
                        showWeekView();
                        break;
                    case R.id.top12:
                        hideWeekView();
                        break;
                    /*case R.id.top13:
                        setMonthWidth();
                        break;
                    case R.id.top16:
                        resetMonthWidth();
                        break;*/
                    case R.id.top14:
                        hideWeekends();
                        break;
                    case R.id.top15:
                        showWeekends();
                        break;
                    case R.id.top17:
                        exitCurrentAccount();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        popup.show();
    }

    /**
     * 退出当前账号
     * 清空本地账号及SharedPreferences
     */
    private void exitCurrentAccount() {
        //清空数据库
        DaoImpl.ClearCource();
        //清空SharedPreferences
        SharedPreferences.Editor editor=getSharedPreferences("userInfo",MODE_PRIVATE).edit();
        editor.putInt("weekNumber",-1);
        editor.putString("date", "");
        editor.putString("username","");
        editor.putString("password","");
        editor.putString("current", "");

        editor.commit();
        Intent intent=new Intent(BaseFuncActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    /**
     * 删除课程
     * 内部使用集合维护课程数据，操作集合的方法来操作它即可
     * 最后更新一下视图（全局更新）
     */
    protected void deleteSubject() {
        int size = mTimetableView.dataSource().size();
        int pos = (int) (Math.random() * size);
        if (size > 0) {
            mTimetableView.dataSource().remove(pos);
            mTimetableView.updateView();
        }
    }
    /**
     * 添加课程
     * 内部使用集合维护课程数据，操作集合的方法来操作它即可
     * 最后更新一下视图（全局更新）
     */
    protected void addSubject() {
        List<Schedule> dataSource = mTimetableView.dataSource();
        int size = dataSource.size();
        if (size > 0) {
            Schedule schedule = dataSource.get(0);
            dataSource.add(schedule);
            mTimetableView.updateView();
        }
    }
    /**
     * 隐藏非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     * <p>
     * updateView()被调用后，会重新构建课程，课程会回到当前周
     */
    protected void hideNonThisWeek() {
        mTimetableView.isShowNotCurWeek(false).updateView();
    }

    /**
     * 显示非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     */
    protected void showNonThisWeek() {
        mTimetableView.isShowNotCurWeek(true).updateView();
    }

    /**
     * 设置侧边栏最大节次，只影响侧边栏的绘制，对课程内容无影响
     *
     * @param num
     */
    protected void setMaxItem(int num) {
        mTimetableView.maxSlideItem(num).updateSlideView();
    }

    /**
     * 显示时间
     * 设置侧边栏构建监听，TimeSlideAdapter是控件实现的可显示时间的侧边栏
     */
    protected void showTime() {
        String[] times = new String[]{
                "09:00", "10:20", "10:40", "12:00",
                "12:30", "13:50", "14:00", "15:20",
                "15:30", "16:50", "17:00", "18:20",
                "19:00", "20:20", "20:30", "21:50"
        };
        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setTimes(times)
                .setTimeTextColor(Color.BLACK);
        mTimetableView.updateSlideView();
    }

    /**
     * 隐藏时间
     * 将侧边栏监听置Null后，会默认使用默认的构建方法，即不显示时间
     * 只修改了侧边栏的属性，所以只更新侧边栏即可（性能高），没有必要更新全部（性能低）
     */
    protected void hideTime() {
        mTimetableView.callback((ISchedule.OnSlideBuildListener) null);
        mTimetableView.updateSlideView();
    }

    /**
     * 显示WeekView
     */
    protected void showWeekView() {
        mWeekView.isShow(true);
    }

    /**
     * 隐藏WeekView
     */
    protected void hideWeekView() {
        mWeekView.isShow(false);
    }

    /**
     * 设置月份宽度
     */
    private void setMonthWidth() {
        mTimetableView.monthWidthDp(50).updateView();
    }

    /**
     * 设置月份宽度,默认40dp
     */
    private void resetMonthWidth() {
        mTimetableView.monthWidthDp(40).updateView();
    }

    /**
     * 修改显示的文本
     */
    public void buildItemText() {
        mTimetableView.callback(new OnItemBuildAdapter() {
            @Override
            public String getItemText(Schedule schedule, boolean isThisWeek) {
                if (isThisWeek) return schedule.getName()+"["+schedule.getExtras().get(MySubject.EXTRAS_NUMBER)+"]"+"\n@"+schedule.getRoom();
                return "[非本周]" + schedule.getName()+"["+schedule.getExtras().get(MySubject.EXTRAS_NUMBER)+"]"+"\n@"+schedule.getRoom();
            }
        })
                .updateView();
    }

    /**
     * 隐藏周末
     */
    private void hideWeekends() {
        mTimetableView.isShowWeekends(false).updateView();
    }

    /**
     * 显示周末
     */
    private void showWeekends() {
        mTimetableView.isShowWeekends(true).updateView();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_layout:
                //如果周次选择已经显示了，那么将它隐藏，更新课程、日期
                //否则，显示
                if (mWeekView.isShowing()) {
                    mWeekView.isShow(false);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_course_textcolor_blue));
                    int cur = mTimetableView.curWeek();
                    mTimetableView.onDateBuildListener()
                            .onUpdateDate(cur, cur);
                    mTimetableView.changeWeekOnly(cur);
                } else {
                    mWeekView.isShow(true);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_red));
                }
                break;
        }
    }
}
