package com.example.visualwallet.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageExporter {

    public static boolean export(Context context, String walName, int[][][] bitImgList) {
        int index = 1;
        for (int[][] img : bitImgList) {
            Bitmap imgBitmap = bitMat2Bitmap(img);
            saveBitmap(context, imgBitmap, String.format("%s_%d", walName, index));
            index++;
        }
        return true;
    }

    public static Bitmap bitMat2Bitmap(int[][] imgBitMat) {
        return bitMat2Bitmap(imgBitMat, 8, 16);
    }

    public static Bitmap bitMat2Bitmap(int[][] imgBitMat, int scale, int padding) {
        Bitmap imgBitmap = Bitmap.createBitmap(
                imgBitMat[0].length * scale + padding * 2,
                imgBitMat.length * scale + padding * 2,
                Bitmap.Config.RGB_565);
        // TODO：性能还可以优化一下
        for (int i = 0; i < imgBitMat.length; i++) {
            for (int j = 0; j < imgBitMat[0].length; j++) {
                int tarPix = 0x000000;
                if (imgBitMat[i][j] == 1) {
                    tarPix = 0xFFFFFF;
                }

                for (int y = padding + i * scale; y < scale; y++) {
                    for (int x = padding + j * scale; x < scale; x++) {
                        imgBitmap.setPixel(x, y, tarPix);
                    }
                }
            }
        }
        return imgBitmap;
    }

    public static void saveBitmap(Context context, Bitmap bitmap, String bitName) {
        String fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/VisualWallet/" + bitName;
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
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

    }

}
