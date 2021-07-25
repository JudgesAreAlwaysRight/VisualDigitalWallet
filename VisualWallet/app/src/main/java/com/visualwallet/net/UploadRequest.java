package com.visualwallet.net;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadRequest extends NetRequest {
    private static final String subUrl = "/upload";
    private static final String reqFlag = "fileUpload";
    private int id;
    private int mode;
    private String type;
    private File file;

    public UploadRequest(int id, int mode, String type, File file) {
        this.id = id;
        this.mode = mode;
        this.type = type;
        this.file = file;
    }

    @Override
    public void run() {
        Map<String, Object> args = new HashMap<String, Object>();

        args.put("reqFlag", reqFlag);
        if (mode != 0) {
            args.put("id", id);
        }
        args.put("mode", mode);
        args.put("type", type);
        args.put("file", file);

        Map response = NetUtil.filePost(NetUtil.getUrlBase() + subUrl, args, true);

        callBack(response);
    }
}
