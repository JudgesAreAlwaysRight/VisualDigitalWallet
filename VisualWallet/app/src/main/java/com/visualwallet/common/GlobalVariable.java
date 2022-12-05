package com.visualwallet.common;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;

/**
 * 公共变量
 */
public class GlobalVariable {
    /**
     * app运作模式
     *  本地模式（随身安全）：0
     *  在线模式（服务器）：1
     */
    public static int appMode;
    public static String androidId;

    /**
     * 获取androidId，必须在使用前，由任何一个Activity调用进行初始化，传入Activity的this指针
     */
    public static void initAndroidId(Context context) {
        androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i("init android_id", androidId);
    }

    /**
     * 还原私钥之后自动填充密码框使用
     */
    public static String privateKey = null;
}
