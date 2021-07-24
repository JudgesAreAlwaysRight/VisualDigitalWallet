package com.visualwallet.net;

import java.util.HashMap;
import java.util.Map;

import static com.visualwallet.Start.androidId;

public class UpdateRequest extends NetRequest {

    private static final String subUrl = "/update";
    private static final String reqFlag = "updateQR";
    private int id;
    private String secretKey;

    public UpdateRequest(int id, String secretKey) {
        this.id = id;
        this.secretKey = secretKey;
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        args.put("id", id);
        args.put("secretKey", secretKey);
        args.put("android_id", androidId);

        Map response = NetUtil.Post(NetUtil.getUrlBase() + subUrl, args);

        callBack(response);
    }
}