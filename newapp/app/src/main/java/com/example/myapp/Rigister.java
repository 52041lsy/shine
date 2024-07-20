package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Rigister  extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        Button returnButton = (Button) this.findViewById(R.id.return_btn);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Rigister.this,MainActivity.class);
                startActivity(intent);

            }
        });

        Button signupButton = (Button) this.findViewById(R.id.signup_btn);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Rigister.this,"Succeed!",Toast.LENGTH_SHORT);
                Intent intent=new Intent(Rigister.this,MainActivity.class);
                startActivity(intent);

            }
        });
}

    //注册功能实现
    protected void register(String a ,String b,String c) {
        //待完善
    }
}
