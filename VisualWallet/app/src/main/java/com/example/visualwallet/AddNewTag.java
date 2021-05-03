package com.example.visualwallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddNewTag extends AppCompatActivity {
    private Spinner k;
    private Spinner n;
    private Button submit;

    @Nullable
    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewtag);
        getSupportActionBar().hide();//标题栏隐藏

        k = (Spinner)findViewById(R.id.ant_k_spinner);
        n = (Spinner)findViewById(R.id.ant_n_spinner);

        submit = (Button)findViewById(R.id.ant_submit);
        //TODO 点击submit后将当前页面元素上传至数据库，返回分存图
    }
}
