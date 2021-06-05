package com.visualwallet;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.visualwallet.data.ImageExporter;
import com.visualwallet.data.WalletQuery;
import com.visualwallet.entity.Wallet;
import com.visualwallet.net.NetUtil;
import com.visualwallet.net.SplitRequest;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.Objects;

public class AddNewTag extends AppCompatActivity {

    private EditText viewAddress;
    private EditText viewKey;
    private EditText viewType;
    private EditText viewName;
    private Spinner viewK;
    private Spinner viewN;
    private ImageButton submit;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewtag);
        Objects.requireNonNull(getSupportActionBar()).hide();//标题栏隐藏

        viewAddress = findViewById(R.id.ant_address_input);
        viewKey = findViewById(R.id.ant_pk_input);
        viewType = findViewById(R.id.ant_type_input);
        viewName = findViewById(R.id.ant_info_input);
        viewK = findViewById(R.id.ant_k_spinner);
        viewN = findViewById(R.id.ant_n_spinner);

        submit = findViewById(R.id.ant_submit);
        submit.setOnClickListener(v -> {
            WalletQuery walletQuery = new WalletQuery(AddNewTag.this);

            String address = viewAddress.getText().toString();
            String keyHex = viewKey.getText().toString();
            String key = NetUtil.key2bin(keyHex);
            // key是否合法
            if (key == null) {
                Toast.makeText(AddNewTag.this, "私钥不合法", Toast.LENGTH_LONG).show();
                return;
            }
            String name = viewName.getText().toString();
            String type = viewType.getText().toString();
            int K = Integer.parseInt(viewK.getSelectedItem().toString());
            int N = Integer.parseInt(viewN.getSelectedItem().toString());
            Wallet wallet = new Wallet(address, K, N, type, name);

            Intent intent = getIntent();
            AndPermission.with(this)
                    .permission(Permission.WRITE_EXTERNAL_STORAGE)
                    .onGranted(permissions -> {
                        // 新建一个网络请求线程类并启动线程
                        SplitRequest splitRequest = new SplitRequest(key, K, N, type);
                        splitRequest.setNetCallback(res -> {
                            String logInfo = "网络响应异常";
                            if (res == null || !Objects.requireNonNull(res.get("code")).equals("200")) {
                                Log.e("AddNewTag", "Net response illegal");
                                if (res != null && res.get("code") != null) {
                                    logInfo += " " + res.get("code");
                                    Log.e("AddNewTag", "http code " + res.get("code"));
                                }
                                Looper.prepare();
                                Toast.makeText(AddNewTag.this, logInfo, Toast.LENGTH_LONG).show();
                                Looper.loop();
                                return;
                            }

                            int id = (int) res.get("id");
                            int[][][] split = NetUtil.arrayJson2java((JSONArray) res.get("split"));
                            if (split == null) {
                                Looper.prepare();
                                Toast.makeText(AddNewTag.this, logInfo + " 无法获取分存图", Toast.LENGTH_LONG).show();
                                Looper.loop();
                                return;
                            }

                            wallet.setId(id);
                            walletQuery.addWallet(wallet);  // 数据接口调用
                            ImageExporter.export(AddNewTag.this, name, split);  // 调用图像模块，直接全部保存到本地
                        });
                        splitRequest.start();

                        try {
                            splitRequest.join(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        intent.putExtra("add", address);
                        intent.putExtra("name", name);
                        intent.putExtra("type", type);
                        intent.putExtra("K", K);
                        intent.putExtra("N", N);
                    })
                    .onDenied(permissions -> {
                        Toast.makeText(AddNewTag.this, "没有权限无法保存分存图片", Toast.LENGTH_LONG).show();
                    })
                    .start();

            finish();
        });
    }
}