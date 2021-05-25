package com.example.visualwallet.net;

import com.example.visualwallet.common.Constant;

import java.util.HashMap;
import java.util.Map;

public class TransRequest extends NetRequest {

    private static final String transUrl = "/v1/btc/test3/txs/new";
    private static String url;
    private final String from;
    private final String to;
    private final long value;

    public TransRequest(String from, String to, long value) {
        url = Constant.blockchainTestDomain + transUrl;
        this.from = from;
        this.to = to;
        this.value = value;
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        String[] fromList = {from};
        Map<String, String[]> input = new HashMap<>();
        input.put("addresses", fromList);
        Map[] inputList = {input};
        args.put("inputs", inputList);

        String[] toList = {to};
        Map<String, Object> output = new HashMap<>();
        output.put("addresses", toList);
        output.put("value", value);
        Map[] outputList = {output};
        args.put("outputs", outputList);

        Map response = NetUtil.Post(url, args, false);

        callBack(response);
    }
}
