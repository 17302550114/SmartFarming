package com.example.smartfarming.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartfarming.MainActivity;
import com.example.smartfarming.R;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditUsername;
    private EditText mEditPassword;
    private CheckBox mCbisSave;
    private Button mbtnLogin;
    private int remember_flag = 0;
    private String pwd;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init_view();
        event();

    }
    private void event() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE);
        if(sharedPreferences != null){
            name = sharedPreferences.getString("username","");
            pwd = sharedPreferences.getString("pwd","");
            remember_flag = sharedPreferences.getInt("remember",0);
        }
        if (remember_flag == 1){
            mCbisSave.setChecked(true);
            mEditPassword.setText(pwd);
            mEditUsername.setText(name);
        }
        //登陆按钮被点击
        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.创建SharedPreferences对象
                String username = mEditUsername.getText().toString().trim();
                String password = mEditPassword.getText().toString().trim();
                SharedPreferences sharedPreferences = getSharedPreferences("login_info", MODE_PRIVATE);
                //2.创建editor对象，写入值
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name",username);
                editor.putString("pwd",password);
                if (TextUtils.isEmpty(mEditUsername.getText())||TextUtils.isEmpty(mEditPassword.getText())){
                    Toast.makeText(LoginActivity.this,"用户名或密码不能为空", Toast.LENGTH_LONG).show();
                }
                else {
                    if (mCbisSave.isChecked()){
                        remember_flag = 1;
                        editor.putInt("remember",remember_flag);
                        editor.putString("password",password);
                        editor.putString("username",username);
                    }
                    else {
                        remember_flag = 0;
                        editor.putInt("remember", remember_flag);
                    }
                    //3.提交
                    editor.commit();
                }
                //3.执行登陆业务逻辑
                if (username.equals("admin")&&password.equals("123456")){
                    Toast.makeText(LoginActivity.this,"登陆成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void init_view(){
        mEditUsername = (EditText)findViewById(R.id.et_username);
//        mEditUsername.setText("admin");
        mEditPassword = (EditText)findViewById(R.id.et_password);
//        mEditPassword.setText("123456");
        mCbisSave = (CheckBox)findViewById(R.id.cb_isSave);
        mbtnLogin = (Button)findViewById(R.id.btn_login);
    }
}