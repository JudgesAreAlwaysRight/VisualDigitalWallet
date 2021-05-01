package com.example.visualwallet.net;

public class SplitRequest extends NetRequest {
    private static final String subUrl = "/generate";
    private static final String reqFlag = "genSplit";
    private String secretKey;
    private int coeK;
    private int coeN;
    private String IMEI;
    private String IMSI;
    private String MAC;
    private String curType;
    private int walNo;

    public SplitRequest(String secretKey, int coeK, int coeN, String curType, int walNo) {
        this.secretKey = secretKey;
        this.coeK = coeK;
        this.coeN = coeN;
        this.curType = curType;
        this.walNo = walNo;
    }
}
