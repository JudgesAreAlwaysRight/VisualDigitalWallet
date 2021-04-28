package com.example.visualwallet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Collect extends AppCompatActivity implements View.OnClickListener {

    private Button local;
    private Button scan;
    private Button bluetooth;

    private TextView tempTextView;

    private ImageView imageView;

    private static final int TAKE_CAMERA = 101;
    private static final int PICK_PHOTO = 102;
    private static final int REQUEST_CODE_SCAN = 111;

    private Uri imageUri;

    private List<String> splitInfo;
    private List<String> splitMat;

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

        tempTextView = (TextView) findViewById(R.id.textView2);

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
                    String path = ImageUtil.getImageAbsolutePath(this, data.getData());
                    Log.i("img path", path);
                    new DecodeImgThread(path, new DecodeImgCallback() {
                        @Override
                        public void onImageDecodeSuccess(Result result) {
                            Log.i("local pic scan result", result.getText());
                            tempTextView.setText(result.getText());
                        }

                        @Override
                        public void onImageDecodeFailed() {
                            Toast.makeText(Collect.this, cn.milkyship.zxing.R.string.scan_failed_tip, Toast.LENGTH_SHORT).show();
                        }
                    }).run();
                }
                break;
            case REQUEST_CODE_SCAN:
                if (resultCode == RESULT_OK && data != null) {
                    String content = data.getStringExtra(Constant.CODED_CONTENT);
                    String pointMatrix = data.getStringExtra(Constant.CODED_POINT_MATRIX);

                    splitInfo.add(content);
                    splitMat.add(pointMatrix);

                    // TODO: 刷新页面recycleview?
                    tempTextView.setText(content);

                    // todo:remove log code
                    Log.i("point matrix size", String.valueOf(pointMatrix.length()));
                    Log.i("point matrix", " \n" + pointMatrix.replace("1", "#").replace("0", " "));
                }
                break;
            default:
                break;
        }
    }
}