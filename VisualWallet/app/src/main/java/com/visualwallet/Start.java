package com.visualwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import com.visualwallet.common.Constant;
import com.visualwallet.data.AppModeUtil;
import com.visualwallet.data.DataUtil;
import com.visualwallet.data.WalletQuery;
import com.visualwallet.net.SplitRequest;

import java.util.Objects;

public class Start extends AppCompatActivity {

    public static String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//状态栏隐藏
        Objects.requireNonNull(getSupportActionBar()).hide();//标题栏隐藏
        setContentView(R.layout.activity_start);

        // 各个子模块初始化
        androidId = Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Constant.initAndroidId(Start.this);
        AppModeUtil.initAppMode(Start.this);
        DataUtil.initData(Start.this);
        WalletQuery.initPrefName();

        // 一段时间后跳转
        new Thread()  //子线程
        {
            @Override
            public void run() {
            try {
                sleep(500);
                Intent it = new Intent(getApplication(), MainActivity.class);
                startActivity(it);
                finish();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            }
        }.start();
    }
}