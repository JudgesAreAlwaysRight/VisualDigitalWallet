package com.example.visualwallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visualwallet.common.Constant;
import com.example.visualwallet.entity.Wallet;

import java.util.Objects;

public class Account extends AppCompatActivity {

    private Button getsecret;
    private Button delete;
    private Wallet wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Objects.requireNonNull(getSupportActionBar()).hide();//标题栏隐藏
        this.wallet = (Wallet) savedInstanceState.getSerializable(Constant.WALLET_ARG);
        getsecret = findViewById(R.id.get_secret);
        getsecret.setOnClickListener(v -> {
            Intent intent = new Intent(Account.this, Collect.class);
            intent.putExtra(Constant.WALLET_ARG, wallet);
            startActivity(intent);
        });
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(v -> {
//                TODO：这里应该搞个返回值啥的然后在Notification里根据返回值看要不要移除按钮
//                我对不起你因为我实在不会搞这玩意，如果可以的话你写个样例代码，我照着改
        });
    }
}