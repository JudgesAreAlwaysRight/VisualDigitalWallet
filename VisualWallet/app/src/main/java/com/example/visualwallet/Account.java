package com.example.visualwallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visualwallet.common.Constant;
import com.example.visualwallet.data.WalletQuery;
import com.example.visualwallet.entity.Wallet;

import java.util.Objects;

public class Account extends AppCompatActivity {

    private Intent intent;

    private Button getsecret;
    private Button delete;

    private Wallet wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Objects.requireNonNull(getSupportActionBar()).hide();//标题栏隐藏

        intent = getIntent();
        this.wallet = (Wallet) intent.getSerializableExtra(Constant.WALLET_ARG);
        TextView addrView = findViewById(R.id.account_address02);
        addrView.setText(wallet.getAddress());
        TextView typeView = findViewById(R.id.account_type02);
        typeView.setText(wallet.getCurType());
        TextView nameView = findViewById(R.id.account_balance02);
        nameView.setText(wallet.getWalName());

        getsecret = findViewById(R.id.get_secret);
        getsecret.setOnClickListener(v -> {
            Intent intent = new Intent(Account.this, Collect.class);
            intent.putExtra(Constant.WALLET_ARG, wallet);
            startActivity(intent);
        });

        delete = findViewById(R.id.delete);
        delete.setOnClickListener(v -> {
            WalletQuery query = new WalletQuery(Account.this);
            query.deleteWallet(wallet.getId());
            setResult(RESULT_OK, intent);
            this.finish();
        });
    }
}