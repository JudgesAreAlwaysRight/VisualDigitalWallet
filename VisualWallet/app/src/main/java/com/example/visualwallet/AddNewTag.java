package com.example.visualwallet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visualwallet.data.ImageExporter;
import com.example.visualwallet.data.WalletQuery;
import com.example.visualwallet.entity.Wallet;
import com.example.visualwallet.net.NetCallback;
import com.example.visualwallet.net.SplitRequest;
import com.example.visualwallet.ui.notifications.NotificationsFragment;

import java.util.Map;

public class AddNewTag extends AppCompatActivity {

    private EditText viewAddress;
    private EditText viewKey;
    private EditText viewType;
    private EditText viewName;
    private Spinner viewK;
    private Spinner viewN;
    private Button submit;

    @Nullable
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewtag);
        getSupportActionBar().hide();//标题栏隐藏

        viewAddress = (EditText) findViewById(R.id.ant_address_input);
        viewKey = (EditText) findViewById(R.id.ant_pk_input);
        viewType = (EditText) findViewById(R.id.ant_type_input);
        viewName = (EditText) findViewById(R.id.ant_info_input);
        viewK = (Spinner) findViewById(R.id.ant_k_spinner);
        viewN = (Spinner) findViewById(R.id.ant_n_spinner);

        submit = (Button) findViewById(R.id.ant_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalletQuery walletQuery = new WalletQuery(AddNewTag.this);

                int walNo = walletQuery.getNewNo();
                String address = viewAddress.getText().toString();
                String key = viewKey.getText().toString();
                String name = viewName.getText().toString();
                String type = viewType.getText().toString();
                int K = viewK.getTextAlignment();
                int N = viewN.getTextAlignment();
                String Kstr = viewK.getSelectedItem().toString();
                String Nstr = viewN.getSelectedItem().toString();
                Wallet wallet = new Wallet(address, K, N, type, walNo, name);
//以下是bug段
                NotificationsFragment myFragment = new NotificationsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("address",address);
                bundle.putString("K",Kstr);
                bundle.putString("N",Nstr);
                bundle.putString("type",type);
                bundle.putString("name",name);
                myFragment.setArguments(bundle);
//以上是bug段

                // 新建一个网络请求线程类并启动线程
                SplitRequest splitRequest = new SplitRequest(key, K, N, type, walNo);
                splitRequest.setNetCallback(new NetCallback() {
                    @Override
                    public void callBack(@Nullable Map res) {
                        String logInfo = "网络响应异常";
                        if (res == null || !res.get("code").equals("200")) {
                            Log.e("AddNewTag", "Net response illegal");
                            if (res.get("code") != null) {
                                logInfo += " " + res.get("code");
                                Log.e("AddNewTag", "http code " + res.get("code"));
                            }
                            Looper.prepare();
                            Toast.makeText(AddNewTag.this, logInfo, Toast.LENGTH_LONG).show();
                            Looper.loop();
                            return;
                        }

                        int id = (int) res.get("id");
                        int[][][] split = (int[][][]) res.get("split");
                        if (split == null) {
                            Looper.prepare();
                            Toast.makeText(AddNewTag.this, logInfo + " 无法获取分存图", Toast.LENGTH_LONG).show();
                            Looper.loop();
                            return;
                        }

                        wallet.setWalNo(id);  // TODO: 目前的处理是用服务器返回的id覆盖之前生成的，如果没啥问题这个TODO直接删
                        walletQuery.addWallet(wallet);  // 数据接口调用
                        ImageExporter.export(AddNewTag.this, name, split);  // 调用图像模块，直接全部保存到本地
                    }
                });
                splitRequest.start();
            }
        });
    }
}
