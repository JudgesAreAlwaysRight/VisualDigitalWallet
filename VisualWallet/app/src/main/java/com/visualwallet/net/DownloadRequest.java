package com.visualwallet.net;

import java.util.HashMap;
import java.util.Map;

public class DownloadRequest extends NetRequest {
    private static final String subUrl = "/download";
    private static final String reqFlag = "fileDownload";
    private int id;
    private String type;

    public DownloadRequest(int id, String type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        // 傻逼后台，这里的id都是数字，其他API的id也都是int，就这个接口要我传string
        // 这个地方坑我半天，老是http500，而且完全没有response，浪费我两三个小时
        args.put("id", String.valueOf(id));
        args.put("type", type);

        Map response = NetUtil.filePost(NetUtil.getUrlBase() + subUrl, args, true, "g_" + id + ".wav");

        callBack(response);
    }
}
