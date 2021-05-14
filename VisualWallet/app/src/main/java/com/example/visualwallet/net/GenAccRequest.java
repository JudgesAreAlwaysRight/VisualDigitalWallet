package com.example.visualwallet.net;

import com.example.visualwallet.common.Constant;

import java.util.Map;

public class GenAccRequest extends NetRequest {

    private static final String genAccUrl = "/v1/bcy/test/addrs";
    private static String url;

    public GenAccRequest() {
        url = Constant.blockchainTestDomain + genAccUrl;
    }

    @Override
    public void run() {
        Map response = NetUtil.Post(url, null);
        callBack(response);
    }
}
