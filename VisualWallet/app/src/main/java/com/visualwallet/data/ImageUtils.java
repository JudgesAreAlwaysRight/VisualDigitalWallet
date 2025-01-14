package com.visualwallet.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.visualwallet.ui.AddNewTag;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import cn.milkyship.zxing.android.Intents;

public class ImageUtils {

    @SuppressLint("DefaultLocale")
    public static boolean export(Context context, String walName, int[][][] bitImgList) {
        int index = 1;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        for (int[][] img : bitImgList) {
            Bitmap imgBitmap = bitMat2Bitmap(img);
            Date date = new Date(System.currentTimeMillis());
            saveBitmap(context, imgBitmap,
                    String.format("%s_%d_%s", walName, index, simpleDateFormat.format(date))
            );
            index++;
        }
        return true;
    }

    public static boolean export(Context context, String walName, int[][][] bitImgList, int audioNum) {
        int newLen = bitImgList.length - audioNum;
        if (newLen >= 0) {
            int[][][] newBitImgList = new int[newLen][][];
            System.arraycopy(bitImgList, audioNum, newBitImgList, 0, bitImgList.length - audioNum);
            return export(context, walName, newBitImgList);
        }
        return bitImgList.length == 0;
    }

    public static Bitmap bitMat2Bitmap(int[][] imgBitMat) {
        return bitMat2Bitmap(imgBitMat, 8, 16);
    }

    public static Bitmap bitMat2Bitmap(int[][] imgBitMat, int scale, int padding) {
        Bitmap imgBitmap = Bitmap.createBitmap(
                imgBitMat[0].length * scale + padding * 2,
                imgBitMat.length * scale + padding * 2,
                Bitmap.Config.ARGB_8888);
        imgBitmap.eraseColor(Color.parseColor("#FFFFFFFF"));
        // TIP：性能还可以优化一下
        for (int i = 0; i < imgBitMat.length; i++) {
            for (int j = 0; j < imgBitMat[0].length; j++) {
                int tarPix = 0xFFFFFFFF;
                if (imgBitMat[i][j] == 0) {
                    tarPix = 0xFF000000;
                }
                for (int y = padding + i * scale; y < padding + (i + 1) * scale; y++) {
                    for (int x = padding + j * scale; x < padding + (j + 1) * scale; x++) {
                        imgBitmap.setPixel(x, y, tarPix);
                    }
                }
            }
        }
        return imgBitmap;
    }

    public static void saveBitmap(Context context, Bitmap bitmap, String bitName) {
        String pathName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/VisualWallet/";
        File path = new File(pathName);
        if(!path.exists()) {
            path.mkdirs();
        }

        String fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/VisualWallet/" + bitName + ".jpg";
        File file = new File(fileName);
        Log.d("save bitMap", fileName);

        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)) {
                out.flush();
                out.close();
                // 插入图库
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        // context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

    }

    public static int[][] encodeBitMat(String qrCodeText, int qrVersion) throws WriterException {
        // 使用最高纠错等级H， k < 4 版本=28， k >= 4 版本=32，boxsize=1， border=0
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.QR_VERSION, qrVersion);
//        hints.put(EncodeHintType.DATA_MATRIX_SHAPE, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 25,25, hints);
        return bitMatrix.getArray();
    }

}
