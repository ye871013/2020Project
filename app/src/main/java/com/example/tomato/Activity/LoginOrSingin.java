package com.example.tomato.Activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tomato.UserInfoConfig;
import com.example.tomato.background.BackgroundWorker;
import com.example.tomato.R;

public class LoginOrSingin extends AppCompatActivity{
    EditText AccountEt, PasswordEt;
    TextView Sign_Up, performer_verify;
    String password, username, account;
    public LoginOrSingin LogIn;
    //public boolean Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //設定隱藏標題
        getSupportActionBar().hide();
        /*設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);*/
        LogIn = this;
        AccountEt = (EditText) findViewById(R.id.Account);
        PasswordEt = (EditText) findViewById(R.id.Password);
        Sign_Up = (TextView) findViewById(R.id.sign_up);
        performer_verify= (TextView)findViewById(R.id.performer_vertify);

/*
        ///測試用直接跳過登入介面---------------------------------------------------------------------------------
        UserInfoConfig.setConfig(this,"UserInfo","role","Performer");
        UserInfoConfig.setConfig(this,"UserInfo","ID","1");
        UserInfoConfig.setConfig(this,"UserInfo","Img","");
        UserInfoConfig.setConfig(this,"UserInfo","Name","");
        UserInfoConfig.setConfig(this,"UserInfo","username","test");
        //------------------------------------------------------------------------------------------------------------


 */

        // UserInfoConfig.setConfig NAME是link key是url 的value值是放目前使用的伺服器網域-----------------------------
        UserInfoConfig.setConfig(this,"link","url","http://172.20.10.3");
        UserInfoConfig.setConfig(this, "sign_up", "status", "false");
       // UserInfoConfig.setConfig(this,"link","url","http://10.3.204.241");

        if(!UserInfoConfig.getConfig(this,"UserInfo","username","").equals("")){
            change_intent(UserInfoConfig.getConfig(this, "UserInfo", "username", ""));
        }
    }

    //註冊按鈕功能------------------------------------------------------------------------------------------------------------
    public void Click_Sign_Up(View view){
        Intent intent = new Intent(LogIn, SignUp.class);
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(LogIn, R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
        LoginOrSingin.this.startActivity(intent, options.toBundle());
    }

    //登入按鈕功能------------------------------------------------------------------------------------------------------------
    public void ClickLogin(View view) {
        username = AccountEt.getText().toString();
        password = PasswordEt.getText().toString();

        if (username.trim().equals("")) {
            Toast.makeText(this, "請輸入帳號", Toast.LENGTH_SHORT).show();
        } else if (password.trim().equals("")) {
            Toast.makeText(this, "請輸入密碼", Toast.LENGTH_SHORT).show();
        } else {
            String type = "login";

            BackgroundWorker backgroundWorker = new BackgroundWorker(this);

            backgroundWorker.LogIn = this.LogIn;
            backgroundWorker.execute(type, username, password,
                    UserInfoConfig.getConfig(this,"link","url","localhost"));
        }
       /* //以下為跳過登入用測試頁面
        changeintent();*/
    }

    //登入成功換畫面功能-------------------------------------------------------------------------------------------------------
    void change_intent(String user) {
        Intent intent = new Intent(LogIn, MainPage.class);
        UserInfoConfig.setConfig(this,"UserInfo","username",user);
        UserInfoConfig.setConfig(this,"UserInfo","password",password);
        intent.putExtra("login_user", user);
        startActivity(intent);
        finish();
    }


    //讀登入多執行續回傳值的功能-------------------------------------------------------------------------------------------------------
    public void TaskFinish(String result, String id, String img, String name) {

        switch (result) {
            case "Artist":
                Toast.makeText(this, "藝人登入成功", Toast.LENGTH_LONG).show();
                UserInfoConfig.setConfig(this, "UserInfo", "role", "Performer");
                UserInfoConfig.setConfig(this, "UserInfo", "ID", id);
                UserInfoConfig.setConfig(this, "UserInfo", "Img", img);
                UserInfoConfig.setConfig(this, "UserInfo", "Name", name);
                change_intent(UserInfoConfig.getConfig(this, "UserInfo", "ID", id));
                break;
            case "User":
                Toast.makeText(this, "會員登入成功", Toast.LENGTH_LONG).show();
                UserInfoConfig.setConfig(this, "UserInfo", "role", "User");
                UserInfoConfig.setConfig(this, "UserInfo", "ID", id);
                UserInfoConfig.setConfig(this, "UserInfo", "Img", img);
                UserInfoConfig.setConfig(this, "UserInfo", "Name", name);
                change_intent(username);
                break;
            case "fail":
                Toast.makeText(this, "登入失敗", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                break;
        }
    }

    //藝人驗證功能------------------------------------------------------------------------------------------------------------
    public void Click_Verify(View view) {

        Intent intent = new Intent(LogIn, Performer_Verify_Page.class);
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(LogIn, R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
        LoginOrSingin.this.startActivity(intent, options.toBundle());

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UserInfoConfig.getConfig(this, "sign_up", "status", "false").equals("true")) {

            String type = "login";
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);

            username = UserInfoConfig.getConfig(this,"sign_up_account","temp","");
            password = UserInfoConfig.getConfig(this,"sign_up_password","temp","");
            backgroundWorker.LogIn = this.LogIn;
            backgroundWorker.execute(type, username, password,
                    UserInfoConfig.getConfig(this,"link","url","localhost"));

            UserInfoConfig.setConfig(this,"sign_up_account","temp","");
            UserInfoConfig.setConfig(this,"sign_up_password","temp","");
        }
    }

}


