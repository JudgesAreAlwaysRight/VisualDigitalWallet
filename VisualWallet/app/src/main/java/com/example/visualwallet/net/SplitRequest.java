package com.example.visualwallet.net;

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
    private int walNo;

    public SplitRequest(String secretKey, int coeK, int coeN, String curType, int walNo) {
        this.secretKey = secretKey;
        this.coeK = coeK;
        this.coeN = coeN;
        this.curType = curType;
        this.walNo = walNo;
    }

    /*
     * 获取androidId，必须在使用前，由任何一个Activity调用进行初始化，传入Activity的this指针
     */
    public static void setAndroidId(Context context) {
        SplitRequest.androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i("Android_id", androidId);
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        args.put("secretKey", secretKey);
        args.put("coeK", coeK);
        args.put("coeN", coeN);
        args.put("androidId", androidId);
        args.put("curType", curType);
        args.put("walNo", walNo);

        Map response = NetUtil.Post(subUrl, args);

        callBack(response);
    }
}
