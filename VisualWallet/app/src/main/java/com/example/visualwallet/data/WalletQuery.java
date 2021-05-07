package com.example.visualwallet.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.visualwallet.entity.Wallet;

import java.io.IOException;

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
        Log.i("data save", walStr);

        int accNum = getAccNum();
        Log.i("pre add accNum", String.valueOf(accNum));

        if (accNum < 0) {
            DataUtil.initData(this.context);
            accNum = 0;
        }

        accNum += 1;
        SharedPreferences.Editor editor = context.getSharedPreferences("share", Context.MODE_PRIVATE)
                .edit();
        editor.putInt("accNum", accNum);
        editor.putString(String.valueOf(accNum), walStr);
        editor.apply();
    }

    public int getAccNum() {
        SharedPreferences pref = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return pref.getInt("accNum", -1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Wallet[] getWallets() {

        SharedPreferences pref = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        int accNum = pref.getInt("accNum", -1);

        if (accNum < 0) {
            DataUtil.initData(this.context);
            return new Wallet[0];
        }

        Wallet[] wallets = new Wallet[accNum];
        for (int i = 1; i <= accNum; i++) {
            String accStr = pref.getString(String.valueOf(i), "");

            if (accStr.equals("")) {
                wallets[i - 1] = null;
                Log.e("get Ws", String.format("accNum=%d, index=%d", accNum, i));
            } else {
                try {
                    wallets[i - 1] = (Wallet) DataUtil.deserialize(accStr);
                } catch (IOException | ClassNotFoundException e) {
                    Log.e("get Ws", String.format("accNum=%d, index=%d", accNum, i));
                    e.printStackTrace();
                }
            }
        }

        return wallets;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Wallet getWallet(int walNo) {

        SharedPreferences pref = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        int accNum = pref.getInt("accNum", -1);

        if (accNum < 0) {
            DataUtil.initData(this.context);
        }

        if (accNum < walNo) {
            Log.e("get W", String.format("get walNo over limit, accNum=%d. index=%d", accNum, walNo));
            return null;
        }

        String accStr = pref.getString(String.valueOf(walNo), "");

        try {
            return (Wallet) DataUtil.deserialize(accStr);
        } catch (IOException | ClassNotFoundException e) {
            Log.e("get Ws", String.format("accNum=%d, index=%d", accNum, walNo));
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getNewNo() {
        Wallet[] wallets = getWallets();
        int no = 1, wno = 0;
        for (Wallet w : wallets) {
            wno = w.getWalNo();
            no = (wno >= no ? wno + 1 : no);
        }
        return no;
    }
}
