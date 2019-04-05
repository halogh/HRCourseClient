package com.zhuang.hrcourseclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.zhuang.dao.DaoImpl;
import com.zhuang.model.MySubject;
import com.zhuang.model.SubjectRepertory;
import com.zhuang.spider.HREduSystemSpider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 登录Activity
 * Created by zhuang on 2019/3/27.
 */

public class MainActivity extends AppCompatActivity{
    private EditText userName, password;
    private CheckBox auto_login;
    private Button btn_login;
    private String userNameValue,passwordValue;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //检查自动登录
        CheckSaveLogin();
        //获得实例对象
        userName = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        auto_login = (CheckBox) findViewById(R.id.remember_login);
        btn_login = (Button) findViewById(R.id.login);


        // 登录监听事件
        btn_login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                userNameValue = userName.getText().toString();
                passwordValue = password.getText().toString();
                login(userNameValue,passwordValue);

            }
        });



    }
    /*
    *  登录方法
    * 根据输入的学号和密码爬取课表信息，存入数据库，如果自动登录被选中，存用户信息，然后跳转课表界面
    * */
    private void login(final String userNameValue, final String passwordValue) {
        new Thread(){
            @Override
            public void run() {
                try {
                    //爬取课表数据
                    List<MySubject> mysubject = HREduSystemSpider.getCourse(userNameValue,passwordValue);
                    if (mysubject!=null){
                        //课表存入数据库
                        Boolean b = DaoImpl.SaveCourceData(mysubject);
                        Log.d("saveinfo:",b.toString());
                        //如果自动登录被选中，存用户信息
                        saveUser();
                        Intent intent=new Intent(MainActivity.this,BaseFuncActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this,
                                "连接网络失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this,
                            "网络连接失败",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }.start();
    }
    /*
    * 保存用户信息
    * */
    private void saveUser() {
        SharedPreferences.Editor editor=getSharedPreferences("userInfo",MODE_PRIVATE).edit();
        if(auto_login.isChecked()){//如果自动登录被选中，存入SharedPreferences
            editor.putString("username",userName.getText().toString());
            editor.putString("password",password.getText().toString());
        }
        editor.commit();
    }

    //检查是否保存登录，即检查SharedPreferences是否有数据，有则直接跳转课表界面
    private void CheckSaveLogin() {
        try{
            SharedPreferences Preferences=getSharedPreferences("userInfo",MODE_PRIVATE);
            String username=Preferences.getString("username","");
            String password=Preferences.getString("password","");
            if(!username.equals("")&&!password.equals("")){
                Intent intent=new Intent(this,BaseFuncActivity.class);
                startActivity(intent);
                finish();
            }
        }catch (Exception e){

        }
    }

}
