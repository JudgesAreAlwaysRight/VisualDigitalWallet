package com.example.visualwallet.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.visualwallet.entity.Wallet;

import java.io.IOException;
import java.util.Map;

public class WalletQuery {
    private final Context context;

    public WalletQuery(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addWallet(Wallet wallet) {
        String walStr = null;
        try {
            walStr = DataUtil.serialize(wallet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("data to save", walStr);

        SharedPreferences.Editor editor = context.getSharedPreferences("share", Context.MODE_PRIVATE)
                .edit();
        editor.putString(String.valueOf(wallet.getId()), walStr);
        editor.apply();
    }

    public void deleteWallet(int walId) {
        // TODO 删除账户
    }

    public int getAccNum() {
        SharedPreferences pref = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        Map<String, ?> datas = pref.getAll();
        return datas.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Wallet[] getWallets() {

        SharedPreferences pref = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        Map<String, ?> datas = pref.getAll();
        if (datas.isEmpty()) {
            return new Wallet[0];
        }

        Wallet[] wallets = new Wallet[datas.size()];
        int ind = 0;
        for (Map.Entry<String, ?> entry : datas.entrySet()) {
            try {
                wallets[ind] = (Wallet) DataUtil.deserialize((String) entry.getValue());
            } catch (IOException | ClassNotFoundException e) {
                Log.e("get Ws", String.format("accNum=%d, index=%s", datas.size(), entry.getKey()));
                e.printStackTrace();
            }
            ind++;
        }

        return wallets;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Wallet getWallet(int walNo) {

        SharedPreferences pref = context.getSharedPreferences("share", Context.MODE_PRIVATE);

        Map<String, ?> datas = pref.getAll();
        if (datas.isEmpty()) {
            return null;
        }

        String accStr = pref.getString(String.valueOf(walNo), "");

        try {
            return (Wallet) DataUtil.deserialize(accStr);
        } catch (IOException | ClassNotFoundException e) {
            Log.e("get Ws", String.format("accNum=%d, index=%d", datas.size(), walNo));
            e.printStackTrace();
        }
        return null;
    }
}
