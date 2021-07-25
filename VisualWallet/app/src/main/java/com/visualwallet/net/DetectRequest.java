package com.visualwallet.net;

import java.util.HashMap;
import java.util.Map;

public class DetectRequest extends NetRequest {

    private static final String subUrl = "/detect";
    private static final String reqFlag = "cheatDetect";
    private final int id;
    private final int index;
    private final int[][] keys;
    private final int isAudio;
    private final String audioType;

    public DetectRequest(int id, int index, int[][] keys) {
        this.id = id;
        this.index = index;
        this.keys = keys;
        this.isAudio = 0;
        this.audioType = ".wav";
    }

    public DetectRequest(int id, int index) {
        this.id = id;
        this.index = index;
        this.keys = null;
        this.isAudio = 1;
        this.audioType = ".wav";
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        args.put("id", id);
        args.put("index", index);
        if (keys != null)
            args.put("keys", keys);
        else
            args.put("keys", "");
        args.put("isAudio", isAudio);
        args.put("type", audioType);

        Map response = NetUtil.Post(NetUtil.getUrlBase() + subUrl, args);

        callBack(response);
    }
}
