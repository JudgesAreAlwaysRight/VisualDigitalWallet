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
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import cn.milkyship.zxing.android.CaptureActivity;
import cn.milkyship.zxing.bean.ZxingConfig;
import cn.milkyship.zxing.common.Constant;
import cn.milkyship.zxing.encode.CodeCreator;

import java.util.List;

public class Collect extends AppCompatActivity implements View.OnClickListener {

    private Button local;
    private Button scan;
    private Button bluetooth;

    private TextView tempTextView ;

    private ImageView imageView;

    private static final int TAKE_CAMERA = 101;
    private static final int PICK_PHOTO = 102;
    private static final int REQUEST_CODE_SCAN = 111;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        getSupportActionBar().hide();//标题栏隐藏

        local = (Button)findViewById(R.id.local);
        local.setOnClickListener(this);
        scan = (Button)findViewById(R.id.scan);
        scan.setOnClickListener(this);
        bluetooth = (Button)findViewById(R.id.bluetooth);
        bluetooth.setOnClickListener(this);

        tempTextView = (TextView)findViewById(R.id.textView2);

        imageView = (ImageView) findViewById(R.id.image_show);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Log.i("Collect click", v.toString());
        switch (v.getId()) {
            case R.id.local:
                /*打开相册*/
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Constant.REQUEST_IMAGE);
                break;
            case R.id.scan:
                AndPermission.with(this)
                        .permission(Permission.CAMERA)
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
        switch (requestCode) {
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) { // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case REQUEST_CODE_SCAN:
                if (resultCode == RESULT_OK && data != null) {
                    String content = data.getStringExtra(Constant.CODED_CONTENT);
                    tempTextView.setText(content);
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        displayImage(imagePath);
    }

    /**
     * android 4.4以前的处理方式
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }
}