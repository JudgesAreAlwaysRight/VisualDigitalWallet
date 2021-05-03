package com.example.visualwallet.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.visualwallet.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataUtil {

    /*
     * 本地数据持久化模块初始化，如果没有存储过列表，就初始化一个空列表
     */
    public static void initData(Context context) {
        SharedPreferences pref = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        int accNum = pref.getInt("accNum", -1);
        Log.i("init data", String.format("get accNum=%d", accNum));

        if (accNum == -1) {
            SharedPreferences.Editor editor = context.getSharedPreferences("share", Context.MODE_PRIVATE).edit();
            editor.putInt("accNum", 0);
            editor.apply();
            Log.i("init data", "init");
        }
    }

    public static String serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        String string = byteArrayOutputStream.toString("ISO-8859-1");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return string;
    }

    public static Object deserialize(String str) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return object;
    }
}
