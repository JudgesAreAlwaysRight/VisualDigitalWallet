package com.visualwallet.net;

import com.visualwallet.common.Constant;

import java.util.HashMap;
import java.util.Map;

public class TransRequest extends NetRequest {

    private static final String subUrl = "/transact";
    private static final String reqFlag = "testTransact";
    private final String chain;
    private final String address;
    private final String key;
    private final String num;
    private final String fee;

    public TransRequest(String address, String key, String num, String fee) {
        this.chain = "test";
        this.address = address;
        this.key = key;
        this.num = num;
        this.fee = fee;
    }

    public TransRequest(String address, String key, String num, String fee, String chain) {
        this.chain = chain;
        this.address = address;
        this.key = key;
        this.num = num;
        this.fee = fee;
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        args.put("chain", chain);
        args.put("address", address);
        args.put("num", num);
        args.put("fee", fee);

        Map response = NetUtil.Post(NetUtil.getUrlBase() + subUrl, args);

        callBack(response);
    }
}
