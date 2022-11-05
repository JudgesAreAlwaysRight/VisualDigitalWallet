package com.visualwallet.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.visualwallet.common.GlobalVariable;

public class AppModeUtil {

    public static void initAppMode(Context context) {
        int mode = getAppMode(context);
        if (mode < 0) {
            mode = 1;
            setAppMode(context, 1);
        }
        GlobalVariable.appMode = mode;
    }

    public static int getAppMode(Context context) {
        SharedPreferences pref = context.getSharedPreferences("base", Context.MODE_PRIVATE);
        return pref.getInt("appMode", -1);
    }

    public static void setAppMode(Context context, int appMode) {
        SharedPreferences.Editor editor = context.getSharedPreferences("base", Context.MODE_PRIVATE)
                .edit();
        editor.putInt("appMode", appMode);
        editor.apply();
    }
}
