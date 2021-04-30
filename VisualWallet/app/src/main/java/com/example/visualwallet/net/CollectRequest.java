package com.example.visualwallet.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectRequest extends NetRequest {

    private static final String subUrl = "/validate";
    private static final String reqFlag = "validQR";
    private int id;
    private List<Integer> index;
    private List<String> keys;

    public CollectRequest(int id, List<Integer> index, List<String> keys) {

        if (index.size() != keys.size()) return;

        this.id = id;
        this.index = new ArrayList<>();
        this.index.addAll(index);
        this.keys = new ArrayList<>();
        this.keys.addAll(keys);
    }

    @Override
    public void run() {
        Map<String, String> args = new HashMap<String, String>();

        args.put("reqFlag", reqFlag);

        args.put("id", String.valueOf(id));

        boolean first = true;
        StringBuilder indexBuilder = new StringBuilder("[");
        for (Integer i : index) {
            if (!first) indexBuilder.append(",");
            else first = false;
            indexBuilder.append(i);
        }
        indexBuilder.append("]");
        args.put("index", indexBuilder.toString());

        first = true;
        StringBuilder keysBuilder = new StringBuilder("[");
        for (String i : keys) {
            if (!first) keysBuilder.append(",");
            else first = false;
            keysBuilder.append(i);
        }
        keysBuilder.append("]");
        args.put("keys", keysBuilder.toString());

        Map response = NetUtil.Post(subUrl, args);

        callBack(response);
    }
}
