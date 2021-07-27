package com.visualwallet.net;

import com.visualwallet.data.DataUtil;

import java.util.HashMap;
import java.util.Map;

import static com.visualwallet.Start.androidId;

public class UpdateRequest extends NetRequest {

    private static final String subUrl = "/update";
    private static final String reqFlag = "updateQR";
    private final int id;
    private final String seed;
    private final String secretKey;

    public UpdateRequest(int id, String secretKey) {
        this.id = id;
        this.secretKey = secretKey;
        this.seed = DataUtil.getRandomString(8);
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        args.put("id", id);
        args.put("seed", seed);
        args.put("secretKey", secretKey);
        args.put("android_id", androidId);

        Map response = NetUtil.Post(NetUtil.getUrlBase() + subUrl, args);

        callBack(response);
    }
}