package com.visualwallet.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.visualwallet.common.GlobalVariable;
import com.visualwallet.entity.VisualWallet;

import java.io.IOException;
import java.util.Map;

public class WalletQuery {

    private final Context context;
    public static String prefName;

    private static Integer localMaxId = null;

    public WalletQuery(Context context) {
        this.context = context;
    }

    public synchronized int getNewLocalId() {
        SharedPreferences pref = context.getSharedPreferences("base", Context.MODE_PRIVATE);
        // 一个小trick，本地的id从10000开始，这样能向前兼容，同时也好区分在线账号和本地账号
        int prefLocalMaxId = pref.getInt("localMaxId", 10000);
        localMaxId = prefLocalMaxId + 1;
        SharedPreferences.Editor editor = context.getSharedPreferences("base", Context.MODE_PRIVATE)
                .edit();
        editor.putInt("localMaxId", localMaxId);
        editor.apply();
        return localMaxId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initPrefName() {
        if (GlobalVariable.appMode == 0)
            prefName = "offline";
        else if (GlobalVariable.appMode == 1)
            prefName = "online";
        else
            prefName = "";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addWallet(VisualWallet visualWallet) {
        String walStr = null;
        try {
            walStr = DataUtil.serialize(visualWallet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("data to save", walStr);

        SharedPreferences.Editor editor = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                .edit();
        editor.putString(String.valueOf(visualWallet.getId()), walStr);
        editor.apply();
    }

    public void deleteWallet(int walId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                .edit();
        editor.remove(String.valueOf(walId));
        editor.apply();
    }

    public int getAccNum() {
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        Map<String, ?> datas = pref.getAll();
        return datas.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public VisualWallet[] getWallets() {

        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);

        Map<String, ?> datas = pref.getAll();
        if (datas.isEmpty()) {
            return new VisualWallet[0];
        }

        VisualWallet[] visualWallets = new VisualWallet[datas.size()];
        int ind = 0;
        for (Map.Entry<String, ?> entry : datas.entrySet()) {
            try {
                visualWallets[ind] = (VisualWallet) DataUtil.deserialize((String) entry.getValue());
            } catch (IOException | ClassNotFoundException e) {
                Log.e("get Ws", String.format("accNum=%d, index=%s", datas.size(), entry.getKey()));
                e.printStackTrace();
            }
            ind++;
        }

        return visualWallets;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public VisualWallet getWallet(int walNo) {

        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);

        Map<String, ?> datas = pref.getAll();
        if (datas.isEmpty()) {
            return null;
        }

        String accStr = pref.getString(String.valueOf(walNo), "");

        try {
            return (VisualWallet) DataUtil.deserialize(accStr);
        } catch (IOException | ClassNotFoundException e) {
            Log.e("get Ws", String.format("accNum=%d, index=%d", datas.size(), walNo));
            e.printStackTrace();
        }
        return null;
    }
}
