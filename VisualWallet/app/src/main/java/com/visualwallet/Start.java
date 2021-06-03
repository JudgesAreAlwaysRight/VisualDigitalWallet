package com.visualwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.visualwallet.data.DataUtil;
import com.visualwallet.net.SplitRequest;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//状态栏隐藏
        getSupportActionBar().hide();//标题栏隐藏
        setContentView(R.layout.activity_start);
        SplitRequest.setAndroidId(Start.this);
        DataUtil.initData(Start.this);
        Thread myThread = new Thread()  //子线程
        {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    Intent it = new Intent(getApplication(), MainActivity.class);
                    startActivity(it);
                    finish();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}