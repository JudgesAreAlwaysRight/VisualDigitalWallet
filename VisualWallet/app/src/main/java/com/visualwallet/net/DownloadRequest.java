package com.visualwallet.net;

import java.util.HashMap;
import java.util.Map;

import static com.visualwallet.Start.androidId;

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
        args.put("id", id);
        args.put("type", type);

        Map response = NetUtil.filePost(NetUtil.getUrlBase() + subUrl, args, true, true);

        callBack(response);
    }
}
