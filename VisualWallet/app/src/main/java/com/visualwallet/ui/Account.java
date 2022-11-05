package com.visualwallet.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.visualwallet.R;
import com.visualwallet.common.Constant;
import com.visualwallet.entity.VisualWallet;

import java.util.Objects;

public class Account extends AppCompatActivity {

    private VisualWallet visualWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Objects.requireNonNull(getSupportActionBar()).hide();//标题栏隐藏

        Intent intent1 = getIntent();
        this.visualWallet = (VisualWallet) intent1.getSerializableExtra(Constant.WALLET_ARG);
        TextView addrView = findViewById(R.id.account_address02);
        addrView.setText(visualWallet.getAddress());
        addrView.bringToFront();
        TextView typeView = findViewById(R.id.account_type02);
        typeView.setText(visualWallet.getCurType());
        typeView.bringToFront();
        TextView typeViewTitle = findViewById(R.id.account_type2);
        typeViewTitle.setText(visualWallet.getCurType());
        typeViewTitle.bringToFront();
        TextView nameView = findViewById(R.id.account_balance02);
        nameView.setText(visualWallet.getWalName());
        nameView.bringToFront();
        TextView nView = findViewById(R.id.account_n2);
        nView.setText(String.valueOf(visualWallet.getCoeN()));
        nView.bringToFront();
        TextView kView = findViewById(R.id.account_k2);
        kView.setText(String.valueOf(visualWallet.getCoeK()));
        kView.bringToFront();
        TextView fView = findViewById(R.id.account_f2);
        fView.setText(String.valueOf(visualWallet.getCoeF()));
        fView.bringToFront();

        ImageButton getsecret = findViewById(R.id.get_secret);
        getsecret.bringToFront();
        getsecret.setOnClickListener(v -> {
            Intent intent = new Intent(Account.this, Collect.class);
            intent.putExtra(Constant.WALLET_ARG, visualWallet);
            startActivity(intent);
        });
    }

    public void onReturnClick(View view) {
        finish();
    }
}