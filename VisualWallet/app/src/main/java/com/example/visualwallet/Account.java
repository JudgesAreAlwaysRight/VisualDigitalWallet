package com.example.visualwallet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.visualwallet.common.Constant;
import com.example.visualwallet.entity.Wallet;

public class Account extends AppCompatActivity {

    private Button getsecret;
    private Button delete;
    private Wallet wallet;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getSupportActionBar().hide();//标题栏隐藏
        this.wallet = (Wallet) savedInstanceState.getSerializable(Constant.WALLET_ARG);
        getsecret = (Button)findViewById(R.id.get_secret);
        getsecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, Collect.class);
                intent.putExtra(Constant.WALLET_ARG, wallet);
                startActivity(intent);
            }
        });
        delete = (Button)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TODO：这里应该搞个返回值啥的然后在Notification里根据返回值看要不要移除按钮
//                我对不起你因为我实在不会搞这玩意，如果可以的话你写个样例代码，我照着改
            }
        });
    }
}