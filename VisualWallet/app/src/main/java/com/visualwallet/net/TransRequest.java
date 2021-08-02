package com.visualwallet.net;

import com.visualwallet.common.Constant;

import java.util.HashMap;
import java.util.Map;

public class TransRequest extends NetRequest {

    private static final String subUrl = "/transact";
    private static final String reqFlag = "testTransact";

    public TransRequest() { }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);

        Map response = NetUtil.Post(NetUtil.getUrlBase() + subUrl, args);

        callBack(response);
    }
}
