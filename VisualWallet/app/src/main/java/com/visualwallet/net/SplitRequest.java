package com.visualwallet.net;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static com.visualwallet.Start.androidId;

public class SplitRequest extends NetRequest {
    private static final String subUrl = "/generate";
    private static final String reqFlag = "genSplit";
    private String secretKey;
    private int coeK;
    private int coeN;
    private int coeF;
    private String curType;

    public SplitRequest(String secretKey, int coeK, int coeN, int coeF, String curType) {
        this.secretKey = secretKey;
        this.coeK = coeK;
        this.coeN = coeN;
        this.coeF = coeF;
        this.curType = curType;
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        args.put("secretKey", secretKey);
        args.put("coeK", coeK);
        args.put("coeN", coeN);
        args.put("fixed_num", coeF);
        args.put("android_id", androidId);
        args.put("curType", curType);

        Map response = NetUtil.Post(NetUtil.getUrlBase() + subUrl, args);

        callBack(response);
    }
}
