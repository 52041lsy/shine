package com.example.myapp;


import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import java.lang.System;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //绑定界面原件
        usernameEditText = findViewById(R.id.login_input_username);
        passwordEditText = findViewById(R.id.login_input_password);
        Button loginButton = (Button) this.findViewById(R.id.login_btn);
        Button registerButton = (Button) this.findViewById(R.id.signup_btn);
        //登录按钮监听器
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String a=usernameEditText.getText().toString();
                String b=passwordEditText.getText().toString();
                login(a,b);
            }
        });
        //注册按钮监听器
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Rigister.class);
                startActivity(intent);
            }
        });
    }

    //登录功能实现
    protected void login(String a ,String b) {

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://192.168.45.1:8080/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApiService.Login service=retrofit.create(ApiService.Login.class);
        Call<User> call=service.toLogin(a);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.body()!=null){
                    if(response.body().getPassword().equals(b)){
                        Toast.makeText(MainActivity.this,"Welcome!",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MainActivity.this,Locate.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Wrong password!",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,"Your id no exist!",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this,a+","+b+","+"Fail to connect",Toast.LENGTH_SHORT).show();
            }
        });
    }

        /*if (a.equals("suzy")) {
            Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, Locate.class);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, a+b+"Wrong password!", Toast.LENGTH_SHORT).show();
        }*/


    }

