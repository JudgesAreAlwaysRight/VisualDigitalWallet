package com.visualwallet.ui;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.google.zxing.WriterException;
import com.visualwallet.R;
import com.visualwallet.bitcoin.BitcoinClient;
import com.visualwallet.common.Constant;
import com.visualwallet.common.GlobalVariable;
import com.visualwallet.data.AlgorithmUtil;
import com.visualwallet.data.DataUtil;
import com.visualwallet.data.ImageUtils;
import com.visualwallet.data.WalletQuery;
import com.visualwallet.entity.GenerateResponse;
import com.visualwallet.entity.VisualWallet;
import com.visualwallet.net.DownloadRequest;
import com.visualwallet.net.NetUtil;
import com.visualwallet.net.SplitRequest;
import com.visualwallet.net.UploadRequest;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AddNewTag extends AppCompatActivity {

    private EditText viewAddress;
    private EditText viewKey;
    private EditText viewType;
    private EditText viewName;
    private Spinner viewK;
    private Spinner viewN;
    private Spinner viewF;
    private Spinner viewFile;
    private ImageButton submit;
    private ImageButton fileSelect;

    private VisualWallet visualWallet;
    private String audioPath;
    private String audioName;

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
        viewF = findViewById(R.id.ant_f_spinner);
        viewFile = findViewById(R.id.ant_file_spinner);

        submit = findViewById(R.id.ant_submit);
        submit.setOnClickListener(this::onAddClick);

        fileSelect = findViewById(R.id.ant_file);
        fileSelect.setOnClickListener(v -> onFileClick());

        if (GlobalVariable.appMode == 0) {
            findViewById(R.id.ant_fa).setVisibility(GONE);
            findViewById(R.id.ant_f).setVisibility(GONE);
        }

        Button testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(v -> {
            int wid = 261;
            int fileNum = 1;
            DownloadRequest downloadRequest = new DownloadRequest(wid, ".wav");
            downloadRequest.setNetCallback(res -> {
                Looper.prepare();
                Toast.makeText(AddNewTag.this,  "测试：" + fileNum + "份编码音频已下载成功，保存位置" + Constant.downloadPath, Toast.LENGTH_LONG).show();
                Looper.loop();
            }).start();
            try {
                downloadRequest.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        testButton.setEnabled(false);
        testButton.setVisibility(GONE);

        audioPath = "";
        audioName = "";
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onAddClick(View v) {
        WalletQuery walletQuery = new WalletQuery(AddNewTag.this);

        String address = viewAddress.getText().toString();
        String keyRaw = viewKey.getText().toString();

        Log.i("add account", "privateKey length: " + keyRaw.length()
                + " key is : " + keyRaw);
        String key;
        try {
            if (keyRaw.length() == 64) {
                key = NetUtil.hexKey2bin(keyRaw);
            } else {
                String keyHex = BitcoinClient.getBitcoinKey(keyRaw).getPrivateKeyAsHex();
                Log.i("add account", keyHex);
                key = NetUtil.hexKey2bin(keyHex);
            }
        } catch (Exception e) {
            Log.e("add account", e.toString());
            Toast.makeText(AddNewTag.this, "私钥不合法", Toast.LENGTH_LONG).show();
            return;
        }
        Log.i("add account", "got key: " + key);

        String name = viewName.getText().toString();
        String type = viewType.getText().toString();
        int K = Integer.parseInt(viewK.getSelectedItem().toString());
        int N = Integer.parseInt(viewN.getSelectedItem().toString());
        int F = Integer.parseInt(viewF.getSelectedItem().toString());
        int fileNum = Integer.parseInt(viewFile.getSelectedItem().toString());

        // 参数合法性校验
        if (key == null) {
            Toast.makeText(AddNewTag.this, "私钥不合法", Toast.LENGTH_LONG).show();
            return;
        }
        if (GlobalVariable.appMode == 0) {
            if (F > 0 || fileNum > 0) {
                Toast.makeText(AddNewTag.this, "随身模式不支持静态分存", Toast.LENGTH_LONG).show();
                return;
            }
            if (K > N || (N == 5 && K > 3)) {
                Toast.makeText(AddNewTag.this, "输入分存参数不合法", Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            if (K > N || F > N || (N == 5 && K > 3) || fileNum > F) {
                Toast.makeText(AddNewTag.this, "输入分存参数不合法", Toast.LENGTH_LONG).show();
                return;
            }
            if (fileNum > 0 && audioName.equals("")) {
                Toast.makeText(AddNewTag.this, "您设置了音频分存但是未上传文件或文件还在传输中", Toast.LENGTH_LONG).show();
                return;
            }
        }

        visualWallet = new VisualWallet(address, K, N, F, type, name);

        AndPermission.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(permissions -> {
                    Toast.makeText(AddNewTag.this, "正在生成分存图，请勿退出", Toast.LENGTH_SHORT).show();

                    if (GlobalVariable.appMode == 0) {
                        // 本地模式
                        new Thread(() -> {
                            GenerateResponse res;
                            try {
                                res = AlgorithmUtil.generateSpilt(key, K, N, type);
                            } catch (WriterException e) {
                                Log.e("add account", e.toString());
                                Looper.prepare();
                                Toast.makeText(AddNewTag.this, "二维码生成失败", Toast.LENGTH_LONG).show();
                                Looper.loop();
                                return;
                            }

                            visualWallet.setId(walletQuery.getNewLocalId());
                            visualWallet.setSplitMatSize(res.getSplitMatSize());
                            visualWallet.setCarrierMatSize(res.getCarrierMatSize());
                            walletQuery.addWallet(visualWallet);

                            ImageUtils.export(AddNewTag.this, name, res.getKeyMatrix(), 0);

                            runOnUiThread(AddNewTag.this::finish);
                        }).start();

                    } else {
                        // 在线模式

                        // 新建一个网络请求线程类并启动线程
                        SplitRequest splitRequest = new SplitRequest(key, K, N, F, fileNum, audioName, ".wav", type);
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

                            int[][][] split = NetUtil.arrayJson2java((JSONArray) res.get("split"));
                            if (split == null) {
                                Looper.prepare();
                                Toast.makeText(AddNewTag.this, logInfo + " 无法获取分存图", Toast.LENGTH_LONG).show();
                                Looper.loop();
                                return;
                            }

                            Integer id = (Integer) res.get("id");
                            if (id != null) {
                                visualWallet.setId(id);
                                walletQuery.addWallet(visualWallet);  // 数据接口调用
                                ImageUtils.export(AddNewTag.this, name, split, fileNum);  // 调用图像模块，直接全部保存到本地
                                downloadAudio(); // 下载音频
                            } else {
                                Looper.prepare();
                                Toast.makeText(AddNewTag.this, logInfo + " 无法获取id", Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                            runOnUiThread(this::finish);
                        });
                        splitRequest.start();
                    }
                })
                .onDenied(permissions -> Toast.makeText(AddNewTag.this, "没有权限无法保存分存图片", Toast.LENGTH_LONG).show())
                .start();
    }

    private void onFileClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    public void onReturnClick(View v) {
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onImpClick(View v) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String clipStr = (String) cm.getPrimaryClip().getItemAt(0).getText();
        Log.i("AddNewTag", "got clip string: " + clipStr);
        VisualWallet impW = null;
        try {
            impW = (VisualWallet) DataUtil.deserialize(clipStr);
            Log.e("imp get W", String.format("wid=%d", impW.getId()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (impW != null) {
            WalletQuery wQuery = new WalletQuery(AddNewTag.this);
            wQuery.addWallet(impW);
            Toast.makeText(AddNewTag.this,"导入成功，账户：" + impW.getWalName(), Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(AddNewTag.this,"导入失败，剪贴板数据无法识别", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Log.e("AddNewTag", "onActivityResult() error, resultCode: " + resultCode);
        } else {
            Uri uri = data.getData();
            audioPath = DataUtil.resolveAudioUri(AddNewTag.this, uri);
            Log.i("AddNewTag", "file path: " + uri.getPath());
            uploadAudio(); // 选择好音频文件之后立刻上传，这一步是异步执行的。
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadAudio() {
        if (audioPath.equals("")) {
            Looper.prepare();
            Toast.makeText(AddNewTag.this, "尚未选择音频文件", Toast.LENGTH_LONG).show();
            Looper.loop();
        } else {

            Looper.prepare();
            Toast.makeText(AddNewTag.this, "正在上传文件", Toast.LENGTH_SHORT).show();
            Looper.loop();

            File audioFile = new File(audioPath);
            new UploadRequest(0, 0, ".wav", audioFile).setNetCallback(res -> {
                String logInfo = "网络响应异常";
                if (res == null || !Objects.requireNonNull(res.get("code")).equals("200") || res.get("file_name") == null) {
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
                audioName = Objects.requireNonNull(res.get("file_name")).toString();
                Looper.prepare();
                Toast.makeText(AddNewTag.this, "音频已上传成功", Toast.LENGTH_LONG).show();
                Looper.loop();
            }).start();
        }
        // 等文件上传完成再进行后续请求
        // FIXME: 按理这里应该阻塞一下保证文件传输完成再做后续处理，
        //  但是不知道为什么即便网络请求已经返回这里还是会继续阻塞，
        //  所以只好做成异步的了
//        if (uploadRequest != null) {
//            try {
//                uploadRequest.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private void downloadAudio() {
        int wid = visualWallet.getId();
        int fileNum = Integer.parseInt(viewFile.getSelectedItem().toString());
        if (fileNum > 0) {
            DownloadRequest downloadRequest = new DownloadRequest(wid, ".wav");
            downloadRequest.setNetCallback(res -> {
                Looper.prepare();
                Toast.makeText(AddNewTag.this, fileNum + "份编码音频已下载成功，保存位置" + Constant.downloadPath, Toast.LENGTH_LONG).show();
                Looper.loop();
            }).start();
            try {
                downloadRequest.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
