package com.example.visualwallet.net;

import java.util.HashMap;
import java.util.Map;

public class DetectRequest extends NetRequest {

    private static final String subUrl = "/detect";
    private static final String reqFlag = "cheatDetect";
    private int id;
    private int index;
    private int[][] keys;

    public DetectRequest(int id, int index, int[][] keys) {
        this.id = id;
        this.index = index;
        this.keys = keys;
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        args.put("id", id);
        args.put("index", index);
        args.put("keys", keys);

        Map response = NetUtil.Post(NetUtil.getUrlBase() + subUrl, args);

        callBack(response);
    }
}
