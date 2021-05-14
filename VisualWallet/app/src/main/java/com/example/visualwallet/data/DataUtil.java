package com.example.visualwallet.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class DataUtil {

    /*
     * 使用SharedPreferences进行数据持久化，存储的数据位于
     *      /data/data/com.example.visualwallet/share.xml
     */

    /*
     * 本地数据持久化模块初始化，如果没有存储过列表，就初始化一个空列表
     */
    public static void initData(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("share", Context.MODE_PRIVATE).edit();
        editor.apply();
        Log.i("init data", "init");
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
}
