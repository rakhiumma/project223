 package com.oakspro.a1rice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

 public class MainActivity extends AppCompatActivity {

     private SharedPreferences preferences;
     Boolean islooged=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences=getSharedPreferences("MyLogin", MODE_PRIVATE);
        islooged=preferences.getBoolean("islogged", false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (islooged==true){
                    Intent intent1=new Intent(MainActivity.this, DashActivity.class);
                    startActivity(intent1);
                }else{
                    Intent intent=new Intent(MainActivity.this, SigninActivity.class);
                    startActivity(intent);
                }
            }
        }, 4000);

    }
}