package com.visualwallet.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.visualwallet.AddNewTag;
import com.visualwallet.common.Constant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DataUtil {

    /*
     * 使用SharedPreferences进行数据持久化，存储的数据位于
     *      /data/data/com.visualwallet/shared_prefs/<prefName>.xml
     * 其中<prefName>项可以为：base, offline, online
     */

    private static final Map<Integer, int[][]> s0 = new HashMap<>();
    private static final Map<Integer, int[][]> s1 = new HashMap<>();

    /*
     * 本地数据持久化模块初始化，如果没有存储过列表，就初始化一个空列表
     */
    public static void initData(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("base", Context.MODE_PRIVATE).edit();
        editor.apply();
        Log.i("init data", "init base");
        editor = context.getSharedPreferences(WalletQuery.prefName, Context.MODE_PRIVATE).edit();
        editor.apply();
        Log.i("init data", "init share");
    }

    public static String resolveAudioUri(Context context, Uri uri) {
        if (uri.getAuthority().equals("com.huawei.filemanager.share.fileprovider")) {
            // 文件管理器
            return uri.getPath().substring(5);
        } else if (uri.getAuthority().equals("com.android.providers.media.documents")) {
            // 媒体选择器
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    split[1]
            };
            return DataUtil.getDataColumn(context, contentUri, selection, selectionArgs);
        } else return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static int[][] getS0(int k, int n) {
        return s0.get(k * 10 + n);
    }

    public static int[][] getS1(int k, int n) {
        return s1.get(k * 10 + n);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        String str = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return str;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Object deserialize(String str) throws IOException, ClassNotFoundException {
        Log.i("deserialize", str);
        byte[] bytes = Base64.getDecoder().decode(str);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
        } catch (EOFException eEof) {
            // 正常的异常（JAVA的迷惑设计）反序列化读到序列尾部必然抛出异常
            Log.i("DataUtil", "got eof");
        }
        assert objectInputStream != null;
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return object;
    }

    static {
        s0.put(23, new int[][]{
                {0, 0, 1, 0},
                {0, 0, 1, 0},
                {0, 0, 1, 0}
        });
        s1.put(23, new int[][]{
                {0, 1, 0, 0},
                {1, 0, 0, 0},
                {0, 0, 1, 0}
        });

        s0.put(33, new int[][]{
                {0, 1, 1, 0},
                {0, 0, 1, 1},
                {0, 1, 0, 1}
        });
        s1.put(33, new int[][]{
                {0, 1, 0, 1},
                {1, 0, 0, 1},
                {0, 0, 1, 1}
        });

        s0.put(24, new int[][]{
                {0, 0, 0, 1},
                {0, 0, 0, 1},
                {0, 0, 0, 1},
                {0, 0, 0, 1}
        });
        s1.put(24, new int[][]{
                {0, 0, 0, 1},
                {0, 0, 1, 0},
                {0, 1, 0, 0},
                {1, 0, 0, 0}
        });

        s0.put(34, new int[][]{
                {0, 0, 0, 1, 1, 1, 0, 0, 0},
                {0, 0, 1, 0, 1, 1, 0, 0, 0},
                {0, 0, 1, 1, 1, 0, 0, 0, 0},
                {0, 0, 1, 1, 0, 1, 0, 0, 0}
        });
        s1.put(34, new int[][]{
                {0, 0, 0, 1, 1, 1, 0, 0, 0},
                {0, 0, 1, 0, 1, 1, 0, 0, 0},
                {0, 1, 0, 0, 1, 1, 0, 0, 0},
                {1, 0, 0, 0, 1, 1, 0, 0, 0}
        });

        s0.put(44, new int[][]{
                {0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        s1.put(44, new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1},
                {0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1}
        });

        s0.put(25, new int[][]{
                {0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0}
        });
        s1.put(25, new int[][]{
                {0, 0, 0, 1, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0}
        });

        s0.put(35, new int[][]{
                {0, 0, 0, 0, 1, 1, 1, 1, 0},
                {0, 0, 0, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 1, 1, 0, 1, 1, 0},
                {0, 0, 0, 1, 1, 1, 0, 1, 0},
                {0, 0, 0, 1, 0, 1, 1, 1, 0}
        });
        s1.put(35, new int[][]{
                {0, 0, 0, 1, 0, 1, 1, 1, 0},
                {0, 1, 0, 0, 0, 1, 1, 1, 0},
                {1, 0, 0, 0, 0, 1, 1, 1, 0},
                {0, 0, 1, 0, 0, 1, 1, 1, 0},
                {0, 0, 0, 0, 1, 1, 1, 1, 0}
        });

        s0.put(45, new int[][]{
                {0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        s1.put(45, new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1},
                {0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0},
                {1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1}
        });

        s0.put(55, new int[][]{
                {0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        s1.put(55, new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        });
    }
}
