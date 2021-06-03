package com.visualwallet.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectRequest extends NetRequest {

    private static final String subUrl = "/validate";
    private static final String reqFlag = "validQR";
    private int id;
    private List<Integer> index;
    private List<int[][]> keys;

    public CollectRequest(int id, List<Integer> index, List<int[][]> keys) {

        if (index.size() != keys.size()) return;

        this.id = id;
        this.index = new ArrayList<>();
        this.index.addAll(index);
        this.keys = new ArrayList<>();
        this.keys.addAll(keys);
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
