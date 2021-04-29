package com.example.visualwallet;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualwallet.adapter.showinfo_adapter;
import com.example.visualwallet.net.CollectRequest;
import com.example.visualwallet.net.NetCallback;
import com.google.zxing.Result;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import cn.milkyship.zxing.android.CaptureActivity;
import cn.milkyship.zxing.bean.ZxingConfig;
import cn.milkyship.zxing.common.Constant;
import cn.milkyship.zxing.decode.DecodeImgCallback;
import cn.milkyship.zxing.decode.DecodeImgThread;
import cn.milkyship.zxing.decode.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Collect extends AppCompatActivity implements View.OnClickListener {

    private Button local;
    private Button scan;
    private Button bluetooth;
    private Button collectK;
    private RecyclerView showinfo;

    private ImageView imageView;

    private static final int TAKE_CAMERA = 101;
    private static final int PICK_PHOTO = 102;
    private static final int REQUEST_CODE_SCAN = 111;

    private Uri imageUri;

    private List<String> splitInfo;
    private List<String> splitMat;

    // TODO: K 应当存储在数据库中
    private final int k = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        getSupportActionBar().hide();//标题栏隐藏

        local = (Button) findViewById(R.id.local);
        local.setOnClickListener(this);
        scan = (Button) findViewById(R.id.scan);
        scan.setOnClickListener(this);
        bluetooth = (Button) findViewById(R.id.bluetooth);
        bluetooth.setOnClickListener(this);
        collectK = (Button) findViewById(R.id.collectK);
        collectK.setOnClickListener(this);

        showinfo = (RecyclerView) findViewById(R.id.showinfo);
        showinfo.setLayoutManager(new LinearLayoutManager(Collect.this));
        showinfo.setAdapter(new showinfo_adapter(Collect.this));

        imageView = (ImageView) findViewById(R.id.image_show);

        splitInfo = new ArrayList<String>();
        splitMat = new ArrayList<String>();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Log.i("Collect click", v.toString());
        switch (v.getId()) {
            case R.id.local:
                AndPermission.with(this)
                        .permission(Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                /*打开相册*/
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, Constant.REQUEST_IMAGE);
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Uri packageURI = Uri.parse("package:" + getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

                                Toast.makeText(Collect.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                            }
                        }).start();
                break;
            case R.id.scan:
                AndPermission.with(this)
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Intent intent = new Intent(Collect.this, CaptureActivity.class);
                                ZxingConfig config = new ZxingConfig();//可选的配置类
                                //config.setPlayBeep(false);//是否播放扫描声音 默认为true
                                //config.setShake(false);//是否震动  默认为true
                                config.setDecodeBarCode(false);//是否扫描条形码 默认为true
                                //config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
                                //config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
                                //config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                                //config.setFullScreenScan(false);//是否全屏扫描  默认为true 设为false则只会在扫描框中扫描
                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                startActivityForResult(intent, REQUEST_CODE_SCAN);
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Uri packageURI = Uri.parse("package:" + getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

                                Toast.makeText(Collect.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                            }
                        }).start();
                break;
            case R.id.bluetooth:
                break;
            case R.id.collectK:
                // 提交内容到服务器计算Collect
                List<Integer> ind = new ArrayList<Integer>();
                ind.add(new Integer(1));
                ind.add(new Integer(2));
                CollectRequest collectRequest = (CollectRequest) new CollectRequest(0, ind, splitMat).setNetCallback(new NetCallback() {
                    @Override
                    public void callBack(Map res) {
                        if (res == null || !res.get("code").equals("200")) {
                            String logInfo = "网络响应异常";
                            Log.e("Collect", "Net response illegal");
                            if (res.get("code") != null) {
                                logInfo += " " + res.get("code");
                                Log.e("Collect", "http code " + res.get("code"));
                            }
                            Looper.prepare();
                            Toast.makeText(Collect.this, logInfo, Toast.LENGTH_LONG).show();
                            Looper.loop();
                            return;
                        }
                        String flag = res.get("flag").toString();
                        if (flag.equals("1")) {
                            String secretKey = res.get("secretKey").toString();
                            AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(Collect.this);
                            alertdialogbuilder.setMessage("私钥已找回：\n" + secretKey);
                            alertdialogbuilder.setPositiveButton("确定", null);
                            alertdialogbuilder.setNeutralButton("复制到剪贴板", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData mClipData = ClipData.newPlainText("私钥", "secretKey");
                                    cm.setPrimaryClip(mClipData);
                                    Looper.prepare();
                                    Toast.makeText(Collect.this, "复制成功", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }
                            });
                            final AlertDialog alertdialog = alertdialogbuilder.create();
                            alertdialog.show();
                        } else if (flag.equals("0")) {
                            Log.e("Collect", "Net flag 0, pics were modified");
                            Looper.prepare();
                            Toast.makeText(Collect.this, "分存图遭到篡改，请检查图片内容", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        } else if (flag.equals("-1")) {
                            Log.e("Collect", "Net flag -1, pics were not qrcode");
                            Looper.prepare();
                            Toast.makeText(Collect.this, "分存图无法识别", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        } else {
                            Log.e("Collect", "Net flag illegal");
                            Looper.prepare();
                            Toast.makeText(Collect.this, "服务器响应异常", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                });
                collectRequest.start();

                break;
            default:
                Toast.makeText(Collect.this, "无效点击输入源", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("collect activity result", String.valueOf(requestCode));

        switch (requestCode) {
            case Constant.REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    String path = ImageUtil.getImageAbsolutePath(this, data.getData());
                    Log.i("img path", path);
                    new DecodeImgThread(path, new DecodeImgCallback() {
                        @Override
                        public void onImageDecodeSuccess(Result result) {
                            Log.i("local pic scan result", result.getText());
                            // TODO: 相册扫码成功，向recycleview添加对象，对应处理
                        }

                        @Override
                        public void onImageDecodeFailed() {
                            Toast.makeText(Collect.this, cn.milkyship.zxing.R.string.scan_failed_tip, Toast.LENGTH_SHORT).show();
                        }
                    }).start();
                }
                break;
            case REQUEST_CODE_SCAN:
                if (resultCode == RESULT_OK && data != null) {
                    String content = data.getStringExtra(Constant.CODED_CONTENT);
                    String pointMatrix = data.getStringExtra(Constant.CODED_POINT_MATRIX);

                    // todo:remove log code
                    Log.i("camera scan result", content);
                    Log.i("point matrix size", String.valueOf(pointMatrix.length()));
                    //Log.i("point matrix", " \n" + pointMatrix.replace("1", "#").replace("0", " "));

                    splitInfo.add(content);
                    splitMat.add(pointMatrix);
                    // TODO: 刷新页面recycleview?

                }
                break;
            default:
                break;
        }

        if (splitInfo.size() == k) {
            collectK.setVisibility(View.VISIBLE);
        }
    }
}