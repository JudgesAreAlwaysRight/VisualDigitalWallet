package com.visualwallet.net;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.visualwallet.data.DataUtil;

import java.util.HashMap;
import java.util.Map;

import static com.visualwallet.Start.androidId;

public class SplitRequest extends NetRequest {
    private static final String subUrl = "/generate";
    private static final String reqFlag = "genSplit";
    private final String secretKey;
    private final int coeK;
    private final int coeN;
    private final int coeF;
    private final String seed;
    private final int needAudio;
    private final String audioName;
    private final String type;
    private final String curType;

    public SplitRequest(String secretKey, int coeK, int coeN, int coeF, int needAudio, String audioName, String type, String curType) {
        this.secretKey = secretKey;
        this.coeK = coeK;
        this.coeN = coeN;
        this.coeF = coeF;
        this.needAudio = (needAudio > 0 ? 1 : 0);
        this.audioName = audioName;
        this.type = type;
        this.curType = curType;
        this.seed = DataUtil.getRandomString(8);
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        args.put("secretKey", secretKey);
        args.put("seed", seed);
        args.put("coeK", coeK);
        args.put("coeN", coeN);
        args.put("fixed_num", coeF);
        args.put("needAudio", needAudio);
        args.put("audioName", audioName);
        args.put("type", type);
        args.put("android_id", androidId);
        args.put("curType", curType);

        Map response = NetUtil.Post(NetUtil.getUrlBase() + subUrl, args);

        callBack(response);
    }
}
