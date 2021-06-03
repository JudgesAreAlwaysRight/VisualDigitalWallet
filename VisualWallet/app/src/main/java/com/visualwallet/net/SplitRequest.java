package com.visualwallet.net;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class SplitRequest extends NetRequest {
    private static final String subUrl = "/generate";
    private static final String reqFlag = "genSplit";
    private String secretKey;
    private int coeK;
    private int coeN;
    private static String androidId;
    private String curType;

    public SplitRequest(String secretKey, int coeK, int coeN, String curType) {
        this.secretKey = secretKey;
        this.coeK = coeK;
        this.coeN = coeN;
        this.curType = curType;
    }

    /*
     * 获取androidId，必须在使用前，由任何一个Activity调用进行初始化，传入Activity的this指针
     */
    public static void setAndroidId(Context context) {
        SplitRequest.androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i("android_id", androidId);
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        args.put("secretKey", secretKey);
        args.put("coeK", coeK);
        args.put("coeN", coeN);
        args.put("android_id", androidId);
        args.put("curType", curType);

        Map response = NetUtil.Post(NetUtil.getUrlBase() + subUrl, args);

        callBack(response);
    }
}
